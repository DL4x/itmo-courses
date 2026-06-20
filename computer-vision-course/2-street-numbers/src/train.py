from pathlib import Path

from ultralytics import YOLO

ROOT = Path(__file__).resolve().parent.parent
RUNS = ROOT / "runs" / "train"


def main():
    stage1_model = "yolo11n.pt"
    numberdetection_dataset = ROOT / "datasets" / "numberdetection-2" / "data.yaml"

    model1 = YOLO(stage1_model)
    model1.train(
        data=str(numberdetection_dataset),
        epochs=100,
        batch=16,
        imgsz=640,
        project=str(RUNS),
        name="stage1_pretrain",
        patience=10,
        device="cuda",
        exist_ok=True,
    )

    stage2_model = str(RUNS / "stage1_pretrain" / "weights" / "best.pt")
    svhn_dataset = ROOT / "datasets" / "YOLOv5-SVHN-1" / "data.yaml"

    model2 = YOLO(stage2_model)
    model2.train(
        data=str(svhn_dataset),
        epochs=50,
        batch=16,
        imgsz=640,
        project=str(RUNS),
        name="stage2_sft",
        patience=10,
        device="cuda",
        exist_ok=True,
    )

    stage3_model = str(RUNS / "stage2_sft" / "weights" / "best.pt")
    svhn_extra_dataset = ROOT / "datasets" / "SVHN-YOLO-1" / "data.yaml"

    model3 = YOLO(stage3_model)
    model3.train(
        data=str(svhn_extra_dataset),
        epochs=50,
        batch=16,
        imgsz=640,
        project=str(RUNS),
        name="stage3_sft_extra",
        patience=10,
        device="cuda",
        exist_ok=True,
    )


if __name__ == "__main__":
    main()
