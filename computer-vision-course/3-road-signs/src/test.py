from pathlib import Path

import cv2
import numpy as np
from ultralytics import YOLO

ROOT = Path(__file__).resolve().parent.parent
MODEL_PATH = ROOT / "runs" / "segment" / "road_signs" / "weights" / "best.pt"

INPUT_PATH = ROOT / "selfmade_photos" / "IMG_2146.mp4"

CLASS_COLORS = [
    (255, 0, 0),
    (0, 255, 0),
    (0, 0, 255),
    (255, 255, 0),
    (255, 0, 255),
    (0, 255, 255),
    (128, 0, 255),
    (0, 128, 255),
]


def draw_segmentation(img: np.ndarray, result) -> np.ndarray:
    r = result
    h, w = img.shape[:2]
    overlay = img.copy()

    if r.masks is not None:
        for i in range(len(r.masks.data)):
            mask = r.masks.data[i].cpu().numpy()
            mask = cv2.resize(mask, (w, h), interpolation=cv2.INTER_LINEAR)
            mask_bool = mask > 0.5
            cls_id = int(r.boxes.cls[i]) if r.boxes is not None and i < len(r.boxes.cls) else 0
            color = CLASS_COLORS[cls_id % len(CLASS_COLORS)]
            overlay[mask_bool] = overlay[mask_bool] * 0.5 + np.array(color) * 0.5

    out = cv2.addWeighted(img, 0.5, overlay, 0.5, 0)

    if r.masks is not None and r.boxes is not None:
        for i in range(len(r.masks.data)):
            cls_id = int(r.boxes.cls[i])
            color = CLASS_COLORS[cls_id % len(CLASS_COLORS)]
            xyxy = r.boxes.xyxy[i].cpu().numpy()
            x1, y1 = int(xyxy[0]), int(xyxy[1])
            cv2.putText(out, str(cls_id), (x1, y1 - 4), cv2.FONT_HERSHEY_SIMPLEX, 0.6, color, 2, cv2.LINE_AA)

    return out


def run_on_photo(model: YOLO, photo_path: Path) -> None:
    img = cv2.imread(str(photo_path))

    results = model.predict(photo_path, verbose=False)
    r = results[0]

    img_out = draw_segmentation(img, r)
    cv2.imshow("Segmentation (photo)", img_out)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def run_on_video(model: YOLO, video_path: Path) -> None:
    cap = cv2.VideoCapture(str(video_path))

    while True:
        ok, frame = cap.read()
        if not ok:
            break

        results = model.predict(frame, verbose=False)
        if not results:
            cv2.imshow("Segmentation (video)", frame)
        else:
            frame_out = draw_segmentation(frame, results[0])
            cv2.imshow("Segmentation (video)", frame_out)

        key = cv2.waitKey(1)
        if key == 27 or key == ord("q"):
            break

    cap.release()
    cv2.destroyAllWindows()


def track_selfmade():
    model = YOLO(str(MODEL_PATH))
    suffix = INPUT_PATH.suffix.lower()

    if suffix in {".jpg", ".jpeg", ".png", ".bmp"}:
        run_on_photo(model, INPUT_PATH)
        return

    if suffix in {".mp4", ".avi", ".mov", ".mkv"}:
        run_on_video(model, INPUT_PATH)
        return


def track_video():
    model = YOLO(str(MODEL_PATH))

    model.track(
        source=str(INPUT_PATH),
        tracker='bytetrack.yaml',  # или 'botsort.yaml'
        show=True,
        save=True,
    )


def id_switches():
    model = YOLO(str(MODEL_PATH))

    video = cv2.VideoCapture(str(INPUT_PATH))

    unique_ids = []
    id_frames = {}
    frame_index = 0

    while True:
        success, image = video.read()
        if not success:
            break

        predictions = model.track(image, persist=True, verbose=False)

        if predictions[0].boxes.id is not None:
            current_ids = predictions[0].boxes.id.cpu().numpy().astype(int)

            for current_id in current_ids:
                if current_id not in unique_ids:
                    unique_ids.append(current_id)
                    id_frames[current_id] = [frame_index, frame_index]
                else:
                    id_frames[current_id][1] = frame_index

        frame_index += 1

    video.release()

    print(f"Найдено объектов: {len(unique_ids)}")
    print(f"Появлялись неоднократно: {sum(1 for times in id_frames.values() if times[1] - times[0] > 1)}")


def main() -> None:
    # track_selfmade()
    # track_video()
    id_switches()


if __name__ == "__main__":
    main()
