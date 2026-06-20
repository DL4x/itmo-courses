from pathlib import Path
import sys

from facenet_pytorch import MTCNN
from PIL import Image
from tqdm import tqdm

ROOT_DIR = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT_DIR))

INPUT_DIR = "datasets/celeba/img_align_celeba/img_align_celeba"
OUTPUT_DIR = "datasets/celeba/faces_mtcnn"
MAX_IMAGES = 20000


def main() -> None:
    in_dir = Path(INPUT_DIR)
    out_dir = Path(OUTPUT_DIR)
    out_dir.mkdir(parents=True, exist_ok=True)

    mtcnn = MTCNN(
        image_size=128,
        margin=20,
        post_process=False,
        device="cuda",
    )

    image_paths = sorted(in_dir.glob("*.jpg"))
    image_paths = image_paths[: MAX_IMAGES]

    saved = 0
    skipped = 0
    for img_path in tqdm(image_paths, desc="Cropping faces"):
        image = Image.open(img_path).convert("RGB")
        faces = mtcnn(image)
        if faces is None:
            skipped += 1
            continue

        if faces.ndim == 3:
            faces = faces.unsqueeze(0)

        for i, face_tensor in enumerate(faces):
            face_tensor = face_tensor.clamp(0, 255).byte().permute(1, 2, 0).cpu().numpy()
            Image.fromarray(face_tensor).save(out_dir / img_path.name, quality=95)
            saved += 1

    print(f"Saved faces: {saved}")
    print(f"Skipped images: {skipped}")
    print(f"Output: {out_dir}")


if __name__ == "__main__":
    main()
