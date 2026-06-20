from pathlib import Path

from ultralytics import YOLO

ROOT = Path(__file__).resolve().parent.parent
RUNS = ROOT / "runs" / "segment"


def main():
    stage_model = "yolo11n-seg.pt"
    sign_dataset = ROOT / "datasets" / "sign_dataset_yolo_seg" / "data.yaml"

    model = YOLO(stage_model)
    model.train(
        data=str(sign_dataset),
        epochs=50,
        imgsz=640,
        batch=16,
        patience=15,
        project=str(RUNS),
        name="road_signs",
        exist_ok=True,
        pretrained=True,
        optimizer="auto",
        verbose=True,
        seed=42,
        deterministic=True,
        val=True,
        plots=True,
        save=True,
    )


if __name__ == "__main__":
    main()
