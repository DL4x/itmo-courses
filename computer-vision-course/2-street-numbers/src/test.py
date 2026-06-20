from pathlib import Path

from ultralytics import YOLO

ROOT = Path(__file__).resolve().parent.parent
WEIGHTS = ROOT / "runs" / "train" / "stage3_sft_extra" / "weights" / "best.pt"


def test_dataset():
    svhn_dataset = ROOT / "datasets" / "YOLOv5-SVHN-1" / "data.yaml"

    model = YOLO(str(WEIGHTS))
    metrics = model.val(
        data=str(svhn_dataset),
        device="cuda",
    )
    box = metrics.box

    print("Precision:", round(float(box.mp), 4))
    print("Recall:   ", round(float(box.mr), 4))
    print("mAP50:    ", round(float(box.map50), 4))
    print("mAP50-95: ", round(float(box.map), 4))


def test_selfmade():
    input_dir = ROOT / "selfmade_photos"
    output_dir = ROOT / "selfmade_photos_runs"

    input_dir.mkdir(parents=True, exist_ok=True)
    output_dir.mkdir(parents=True, exist_ok=True)

    model = YOLO(str(WEIGHTS))
    model.predict(
        source=str(input_dir),
        save=True,
        project=str(output_dir.parent),
        name=output_dir.name,
        exist_ok=True,
        conf=0.30,
        device="cuda",
    )


def main():
    test_dataset()
    test_selfmade()


if __name__ == '__main__':
    main()
