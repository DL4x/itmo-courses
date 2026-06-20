from collections import Counter
from pathlib import Path
from typing import Tuple, List, Optional

import numpy as np
from PIL import Image
from torch.utils.data import Dataset
from sklearn.model_selection import train_test_split


def parse_color_from_dvm_filename(
        path: Path,
        filename_sep="$$",
        color_field_index=3,
) -> Optional[str]:
    stem = path.stem
    parts = stem.split(filename_sep)
    if len(parts) <= color_field_index:
        return None
    return parts[color_field_index].strip()


def discover_color_classes_from_files(
        data_root: str,
        extensions: Tuple[str, ...] = (".jpg", ".jpeg", ".png"),
) -> Tuple[List[str], dict]:
    data_root = Path(data_root)

    colors = set()
    for path in data_root.rglob("*"):
        if path.is_file() and path.suffix.lower() in extensions:
            color = parse_color_from_dvm_filename(path)
            if color:
                colors.add(color)

    classes = sorted(colors)
    class_to_idx = {c: i for i, c in enumerate(classes)}

    return classes, class_to_idx


def collect_image_paths(
        data_root: str,
        extensions: Tuple[str, ...] = (".jpg", ".jpeg", ".png"),
) -> List[Tuple[str, int]]:
    classes, class_to_idx = discover_color_classes_from_files(data_root, extensions)
    samples = []
    data_root = Path(data_root)

    for path in data_root.rglob("*"):
        if path.is_file() and path.suffix.lower() in extensions:
            color = parse_color_from_dvm_filename(path)
            if color and color in class_to_idx:
                samples.append((str(path), class_to_idx[color]))

    return samples


def get_classes_and_class_to_idx(
        data_root: str,
        extensions: Tuple[str, ...] = (".jpg", ".jpeg", ".png"),
) -> Tuple[List[str], dict]:
    return discover_color_classes_from_files(data_root, extensions)


class CarColorDataset(Dataset):
    def __init__(
        self,
        samples: List[Tuple[str, int]],
        transform=None,
        target_size: Tuple[int, int] = (224, 224),
    ):
        self.samples = samples
        self.transform = transform
        self.target_size = target_size

    def __len__(self) -> int:
        return len(self.samples)

    def __getitem__(self, idx: int):
        path, label = self.samples[idx]
        image = Image.open(path).convert("RGB")
        image = np.array(image)
        if self.transform:
            image = self.transform(image)
        return image, label


def get_train_val_test_splits(
    data_root: str,
    val_ratio: float = 0.15,
    test_ratio: float = 0.15,
    random_state: int = 42,
    min_samples_per_class: int = 2,
):
    samples = collect_image_paths(data_root)

    paths, old_labels = zip(*samples)
    paths, old_labels = list(paths), list(old_labels)
    all_classes, old_class_to_idx = get_classes_and_class_to_idx(data_root)
    idx_to_class = {i: c for c, i in old_class_to_idx.items()}

    label_counts = Counter(old_labels)
    kept_indices = {idx for idx, cnt in label_counts.items() if cnt >= min_samples_per_class}

    kept_class_names = sorted(idx_to_class[i] for i in kept_indices)
    class_to_idx = {c: i for i, c in enumerate(kept_class_names)}
    old_to_new = {old_class_to_idx[c]: class_to_idx[c] for c in kept_class_names}

    filtered_paths, filtered_labels = [], []
    for p, old_lbl in zip(paths, old_labels):
        if old_lbl in old_to_new:
            filtered_paths.append(p)
            filtered_labels.append(old_to_new[old_lbl])

    classes = kept_class_names

    train_val_paths, test_paths, train_val_labels, test_labels = train_test_split(
        filtered_paths,
        filtered_labels,
        test_size=test_ratio,
        random_state=random_state,
        stratify=filtered_labels,
    )

    val_size = val_ratio / (1 - test_ratio)
    train_paths, val_paths, train_labels, val_labels = train_test_split(
        train_val_paths,
        train_val_labels,
        test_size=val_size,
        random_state=random_state,
        stratify=train_val_labels,
    )

    train_samples = list(zip(train_paths, train_labels))
    val_samples = list(zip(val_paths, val_labels))
    test_samples = list(zip(test_paths, test_labels))

    return (train_samples, val_samples, test_samples), classes, class_to_idx
