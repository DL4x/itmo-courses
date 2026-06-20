import json
from collections import Counter
from pathlib import Path

import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from torchvision import transforms
from sklearn.metrics import f1_score, classification_report
from tqdm import tqdm

from src.data import get_train_val_test_splits, CarColorDataset
from src.models import get_model


def get_transforms(is_training: bool, image_size: int = 224):
    normalize = transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225],
    )
    if is_training:
        return transforms.Compose([
            transforms.ToPILImage(),
            transforms.RandomResizedCrop(image_size),
            transforms.RandomHorizontalFlip(),
            transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2),
            transforms.ToTensor(),
            normalize,
        ])
    return transforms.Compose([
        transforms.ToPILImage(),
        transforms.Resize((image_size + 32, image_size + 32)),
        transforms.CenterCrop(image_size),
        transforms.ToTensor(),
        normalize,
    ])


def train_one_epoch(model, loader, criterion, optimizer, device):
    model.train()
    total_loss = 0.0
    all_preds, all_labels = [], []
    for images, labels in tqdm(loader, desc="Train", leave=False):
        images, labels = images.to(device), labels.to(device)
        optimizer.zero_grad()
        logits = model(images)
        loss = criterion(logits, labels)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
        preds = logits.argmax(dim=1)
        all_preds.extend(preds.cpu().numpy())
        all_labels.extend(labels.cpu().numpy())
    avg_loss = total_loss / len(loader)
    f1 = f1_score(all_labels, all_preds, average="macro", zero_division=0)
    return avg_loss, f1


@torch.no_grad()
def evaluate(model, loader, criterion, device, class_names=None):
    model.eval()
    total_loss = 0.0
    all_preds, all_labels = [], []
    for images, labels in loader:
        images, labels = images.to(device), labels.to(device)
        logits = model(images)
        loss = criterion(logits, labels)
        total_loss += loss.item()
        preds = logits.argmax(dim=1)
        all_preds.extend(preds.cpu().numpy())
        all_labels.extend(labels.cpu().numpy())
    avg_loss = total_loss / len(loader)
    f1_macro = f1_score(all_labels, all_preds, average="macro", zero_division=0)
    report = classification_report(
        all_labels, all_preds, target_names=class_names, output_dict=True, zero_division=0
    )
    return avg_loss, f1_macro, report


def run_training(
    data_root: str,
    model_name: str,
    output_dir: str,
    epochs: int = 20,
    batch_size: int = 32,
    lr: float = 1e-3,
    image_size: int = 224,
    num_workers: int = 0,
    device: str = None,
):
    if device is None:
        device = "cuda" if torch.cuda.is_available() else "cpu"

    Path(output_dir).mkdir(parents=True, exist_ok=True)

    (train_samples, val_samples, test_samples), classes, _ = get_train_val_test_splits(data_root)
    num_classes = len(classes)

    train_ds = CarColorDataset(train_samples, transform=get_transforms(True, image_size))
    val_ds = CarColorDataset(val_samples, transform=get_transforms(False, image_size))
    test_ds = CarColorDataset(test_samples, transform=get_transforms(False, image_size))

    train_loader = DataLoader(train_ds, batch_size=batch_size, shuffle=True, num_workers=num_workers, pin_memory=True)
    val_loader = DataLoader(val_ds, batch_size=batch_size, shuffle=False, num_workers=num_workers)
    test_loader = DataLoader(test_ds, batch_size=batch_size, shuffle=False, num_workers=num_workers)

    model = get_model(model_name, num_classes=num_classes).to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.AdamW(model.parameters(), lr=lr, weight_decay=1e-4)
    scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=epochs)

    best_f1 = 0.0
    history = {"train_loss": [], "train_f1": [], "val_loss": [], "val_f1": []}

    for epoch in range(epochs):
        train_loss, train_f1 = train_one_epoch(model, train_loader, criterion, optimizer, device)
        val_loss, val_f1, _ = evaluate(model, val_loader, criterion, device, classes)
        scheduler.step()

        history["train_loss"].append(train_loss)
        history["train_f1"].append(train_f1)
        history["val_loss"].append(val_loss)
        history["val_f1"].append(val_f1)

        print((f"Epoch {epoch+1}/{epochs} "
               f"| train loss={train_loss:.4f} F1={train_f1:.4f} "
               f"| val loss={val_loss:.4f} F1_macro={val_f1:.4f}"))

        if val_f1 > best_f1:
            best_f1 = val_f1
            torch.save(
                obj={
                    "model_state_dict": model.state_dict(),
                    "classes": classes,
                    "num_classes": num_classes,
                },
                f=Path(output_dir) / "best.pt",
            )

    ckpt = torch.load(Path(output_dir) / "best.pt", map_location=device)
    model.load_state_dict(ckpt["model_state_dict"])
    test_loss, test_f1_macro, test_report = evaluate(model, test_loader, criterion, device, classes)

    with open(Path(output_dir) / "test_metrics.json", "w", encoding="utf-8") as f:
        json.dump(
            obj={
                "f1_macro": test_f1_macro,
                "classification_report": test_report,
            },
            fp=f,
            indent=2,
            ensure_ascii=False,
        )
    with open(Path(output_dir) / "history.json", "w", encoding="utf-8") as f:
        json.dump(
            obj=history,
            fp=f,
            indent=2,
        )


def get_filtered_test_splits(
        data_root: str,
        min_samples: int = None,
        top_k: int = None,
):
    (train_samples, val_samples, test_samples), all_classes, _ = get_train_val_test_splits(data_root)

    test_labels = [label for _, label in test_samples]
    label_counts = Counter(test_labels)

    if min_samples is not None:
        valid_indices = {idx for idx, count in label_counts.items() if count >= min_samples}
    else:
        valid_indices = set(label_counts.keys())

    if top_k is not None and len(valid_indices) > top_k:
        sorted_classes = sorted(
            [(idx, count) for idx, count in label_counts.items() if idx in valid_indices],
            key=lambda x: x[1],
            reverse=True
        )
        valid_indices = {idx for idx, _ in sorted_classes[:top_k]}

    valid_class_names = [all_classes[idx] for idx in sorted(valid_indices)]

    new_class_to_idx = {name: i for i, name in enumerate(valid_class_names)}
    old_to_new = {idx: new_class_to_idx[all_classes[idx]] for idx in valid_indices}

    filtered_test = []
    for path, old_label in test_samples:
        if old_label in old_to_new:
            filtered_test.append((path, old_to_new[old_label]))

    return ([], [], filtered_test), valid_class_names, new_class_to_idx


def evaluate_filtered(model, dataloader, criterion, device, classes, class_mapping):
    model.eval()
    running_loss = 0.0
    all_preds = []
    all_labels = []

    reverse_mapping = {v: k for k, v in class_mapping.items()}

    with torch.no_grad():
        for inputs, labels in dataloader:
            inputs = inputs.to(device)
            labels = labels.to(device)
            outputs = model(inputs)

            batch_loss = 0
            batch_preds_mapped = []
            batch_labels_mapped = []

            for i, label in enumerate(labels):
                mapped_label = class_mapping[label.item()]
                full_logits = outputs[i]

                valid_indices = list(class_mapping.values())
                valid_logits = full_logits[valid_indices]

                valid_label_idx = valid_indices.index(mapped_label)
                loss_item = criterion(valid_logits.unsqueeze(0), torch.tensor([valid_label_idx], device=device))
                batch_loss += loss_item

                pred_valid_idx = torch.argmax(valid_logits).item()
                pred_mapped = valid_indices[pred_valid_idx]
                pred_filtered = reverse_mapping[pred_mapped]

                batch_preds_mapped.append(pred_filtered)
                batch_labels_mapped.append(label.item())

            running_loss += batch_loss.item()
            all_preds.extend(batch_preds_mapped)
            all_labels.extend(batch_labels_mapped)

    f1 = f1_score(all_labels, all_preds, average='macro', zero_division=0)
    report = classification_report(
        all_labels,
        all_preds,
        target_names=classes,
        output_dict=True,
        zero_division=0
    )
    avg_loss = running_loss / len(dataloader)

    return avg_loss, f1, report


def run_testing(
        data_root: str,
        checkpoint_path: str,
        model_name: str,
        batch_size: int = 32,
        image_size: int = 224,
        num_workers: int = 0,
        device: str = None,
        output_dir: str = None,
        min_test_samples: int = None,
        top_k_classes: int = None,
):
    if device is None:
        device = "cuda" if torch.cuda.is_available() else "cpu"

    ckpt = torch.load(checkpoint_path, map_location=device)

    if output_dir is None:
        output_dir = Path(checkpoint_path).parent
    else:
        output_dir = Path(output_dir)
        output_dir.mkdir(parents=True, exist_ok=True)

    (_, _, test_samples), test_classes, test_class_to_idx = get_filtered_test_splits(
        data_root,
        min_samples=min_test_samples,
        top_k=top_k_classes
    )

    num_classes_full = len(ckpt.get("classes", []))

    test_ds = CarColorDataset(test_samples, transform=get_transforms(False, image_size))
    test_loader = DataLoader(
        test_ds,
        batch_size=batch_size,
        shuffle=False,
        num_workers=num_workers,
        pin_memory=True if device == "cuda" else False
    )

    model = get_model(model_name, num_classes=num_classes_full).to(device)
    model.load_state_dict(ckpt["model_state_dict"])
    model.eval()

    criterion = nn.CrossEntropyLoss()

    if "classes" in ckpt:
        checkpoint_classes = ckpt["classes"]
        class_mapping = {
            test_class_to_idx[class_name]: checkpoint_classes.index(class_name)
            for class_name in test_classes
        }
    else:
        class_mapping = {i: i for i in range(len(test_classes))}

    test_loss, test_f1_macro, test_report = evaluate_filtered(
        model, test_loader, criterion, device, test_classes, class_mapping
    )

    print(f"\n[Test Results]")
    print(f"Model: {model_name}")
    print(f"Loss: {test_loss:.4f}")
    print(f"F1 macro: {test_f1_macro:.4f}")

    output_file = output_dir / "test_results.json"
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(
            obj={
                "loss": test_loss,
                "f1_macro": test_f1_macro,
                "classification_report": test_report,
                "model_name": model_name,
                "checkpoint": str(checkpoint_path),
                "num_classes_tested": len(test_classes),
                "classes_tested": test_classes,
                "filter_params": {
                    "min_test_samples": min_test_samples,
                    "top_k_classes": top_k_classes
                }
            },
            fp=f,
            indent=2,
            ensure_ascii=False,
        )
