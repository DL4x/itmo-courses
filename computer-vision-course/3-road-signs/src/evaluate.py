from pathlib import Path

import cv2
import numpy as np
from ultralytics import YOLO

ROOT = Path(__file__).resolve().parent.parent
VAL_IMAGES = ROOT / "datasets" / "sign_dataset_yolo_seg" / "val" / "images"
VAL_LABELS = ROOT / "datasets" / "sign_dataset_yolo_seg" / "val" / "labels"
MODEL_PATH = ROOT / "runs" / "segment" / "road_signs" / "weights" / "best.pt"


def load_gt_masks(label_path: Path, h: int, w: int):
    if not label_path.is_file():
        return []

    masks = []
    with label_path.open() as f:
        for line in f:
            parts = line.split()
            if len(parts) < 6:
                continue
            xs = np.array([float(parts[i]) * w for i in range(1, len(parts), 2)], dtype=np.float32)
            ys = np.array([float(parts[i]) * h for i in range(2, len(parts), 2)], dtype=np.float32)
            pts = np.stack([xs, ys], axis=1).astype(np.int32)

            m = np.zeros((h, w), dtype=np.uint8)
            cv2.fillPoly(m, [pts], 1)
            masks.append(m)

    return masks


def mask_iou(a: np.ndarray, b: np.ndarray) -> float:
    inter = np.logical_and(a, b).sum()
    union = np.logical_or(a, b).sum()
    if union == 0:
        return 0.0

    return float(inter / union)


def l2_mask(pred_binary: np.ndarray, gt_binary: np.ndarray) -> float:
    p = pred_binary.astype(np.float32)
    g = gt_binary.astype(np.float32)
    if p.shape != g.shape:
        return float("nan")

    return float(np.sqrt(np.mean((p - g) ** 2)))


def eval_segmentation():
    model = YOLO(str(MODEL_PATH))

    image_paths = sorted(VAL_IMAGES.glob("*.jpg")) + sorted(VAL_IMAGES.glob("*.png"))

    iou_values = []
    l2_values = []
    tp = fp = fn = 0
    per_image_iou = []
    iou_thr = 0.5

    for img_path in image_paths:
        img = cv2.imread(str(img_path))
        if img is None:
            continue
        h, w = img.shape[:2]

        label_path = VAL_LABELS / f"{img_path.stem}.txt"
        gt_masks = load_gt_masks(label_path, h, w)

        results = model.predict(img_path, verbose=False)
        pred_masks = []
        if results and results[0].masks is not None:
            masks = results[0].masks
            for i in range(len(masks.data)):
                m = masks.data[i].cpu().numpy()
                m = cv2.resize(m, (w, h), interpolation=cv2.INTER_LINEAR)
                m = (m > 0.5).astype(np.uint8)
                pred_masks.append(m)

        if not gt_masks and not pred_masks:
            per_image_iou.append(0.0)
            continue
        if not gt_masks:
            fp += len(pred_masks)
            per_image_iou.append(0.0)
            continue
        if not pred_masks:
            fn += len(gt_masks)
            per_image_iou.append(0.0)
            continue

        iou_matrix = np.zeros((len(pred_masks), len(gt_masks)), dtype=np.float32)
        for i, pm in enumerate(pred_masks):
            for j, gm in enumerate(gt_masks):
                iou_matrix[i, j] = mask_iou(pm, gm)

        current_ious = []
        while True:
            idx = np.unravel_index(np.argmax(iou_matrix), iou_matrix.shape)
            best_iou = iou_matrix[idx]
            if best_iou <= 0:
                break

            current_ious.append(float(best_iou))
            iou_values.append(float(best_iou))

            pi, gi = idx
            iou_matrix[pi, :] = 0
            iou_matrix[:, gi] = 0

        n_tp = sum(1 for v in current_ious if v >= iou_thr)
        tp += n_tp
        fp += len(pred_masks) - n_tp
        fn += len(gt_masks) - n_tp

        gt_best = [0.0] * len(gt_masks)
        for i, pm in enumerate(pred_masks):
            for j, gm in enumerate(gt_masks):
                iou_ij = mask_iou(pm, gm)
                if iou_ij > gt_best[j]:
                    gt_best[j] = iou_ij
        per_image_iou.append(float(np.mean(gt_best)))

        pred_binary = np.clip(np.sum(pred_masks, axis=0), 0, 1).astype(np.uint8)
        gt_binary = np.clip(np.sum(gt_masks, axis=0), 0, 1).astype(np.uint8)
        l2_values.append(l2_mask(pred_binary, gt_binary))

    mean_iou = float(np.mean(iou_values)) if iou_values else 0.0
    precision = tp / (tp + fp) if (tp + fp) else 0.0
    recall = tp / (tp + fn) if (tp + fn) else 0.0
    mean_l2 = float(np.mean(l2_values)) if l2_values else float("nan")

    n_images = len(per_image_iou)
    pct_50 = 100.0 * sum(1 for v in per_image_iou if v >= 0.5) / n_images
    pct_75 = 100.0 * sum(1 for v in per_image_iou if v >= 0.75) / n_images
    pct_90 = 100.0 * sum(1 for v in per_image_iou if v >= 0.9) / n_images

    print("Validation (segmentation)")
    print(f"IoU (mean):        {mean_iou:.4f}")
    print(f"Precision:         {precision:.4f}")
    print(f"Recall:            {recall:.4f}")
    print(f"L2 (mean):         {mean_l2:.4f}")
    print(f"Images IoU >= 0.5: {pct_50:.1f}%")
    print(f"Images IoU >= 0.75:{pct_75:.1f}%")
    print(f"Images IoU >= 0.9: {pct_90:.1f}%")
    print(f"Images evaluated:  {n_images}")


if __name__ == "__main__":
    eval_segmentation()
