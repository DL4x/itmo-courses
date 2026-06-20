import argparse
from pathlib import Path
from typing import Dict, List, Tuple

import numpy as np
import torch
import yaml
from PIL import Image
from facenet_pytorch import InceptionResnetV1, MTCNN
from transformers import CLIPModel, CLIPProcessor


def load_config(path: str) -> dict:
    with open(path, "r", encoding="utf-8") as f:
        return yaml.safe_load(f)


def read_manifest(path: Path) -> List[Tuple[Path, str]]:
    rows = []
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        fn, pr = line.split("\t", maxsplit=1)
        rows.append((path.parent / fn, pr))
    return rows


def clip_score(entries: List[Tuple[Path, str]], device: torch.device) -> float:
    m = CLIPModel.from_pretrained("openai/clip-vit-base-patch32").to(device)
    p = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")
    m.eval()
    scores = []
    for path, prompt in entries:
        img = Image.open(path).convert("RGB")
        inp = p(text=[prompt], images=img, return_tensors="pt", padding=True).to(device)
        with torch.no_grad():
            o = m(**inp)
            ie = o.image_embeds / o.image_embeds.norm(dim=-1, keepdim=True)
            te = o.text_embeds / o.text_embeds.norm(dim=-1, keepdim=True)
            scores.append((ie * te).sum(dim=-1).item())
    return float(np.mean(scores)) if scores else 0.0


def face_cosine(entries: List[Tuple[Path, str]], ref_dir: Path, device: torch.device) -> float:
    mtcnn = MTCNN(image_size=160, margin=0, device=device)
    net = InceptionResnetV1(pretrained="vggface2").eval().to(device)
    embs = []
    for path in sorted(ref_dir.iterdir()):
        if path.suffix.lower() not in {".jpg", ".jpeg", ".png", ".webp", ".bmp"}:
            continue
        face = mtcnn(Image.open(path).convert("RGB"))
        if face is None:
            continue
        with torch.no_grad():
            e = net(face.unsqueeze(0).to(device)).squeeze(0)
        embs.append(e / e.norm())

    ref = torch.stack(embs).mean(0)
    ref = ref / ref.norm()
    scores = []
    for path, _ in entries:
        face = mtcnn(Image.open(path).convert("RGB"))
        if face is None:
            continue
        with torch.no_grad():
            e = net(face.unsqueeze(0).to(device)).squeeze(0)
        e = e / e.norm()
        scores.append(torch.dot(ref, e).item())
    return float(np.mean(scores)) if scores else 0.0


def no_ref_quality(entries: List[Tuple[Path, str]], device: torch.device) -> Tuple[str, float]:
    vals = []
    for path, _ in entries:
        gray = np.asarray(Image.open(path).convert("L"), dtype=np.float32) / 255.0
        gy, gx = np.gradient(gray)
        vals.append(float(np.mean(gx * gx + gy * gy)))
    return "tenengrad_higher_better", (float(np.mean(vals)) if vals else 0.0)


def filter_gender(entries: List[Tuple[Path, str]], gender: str) -> List[Tuple[Path, str]]:
    keys = [f"{gender} in a forest", f"{gender} in a city", f"{gender} in a beach"]
    return [(p, pr) for p, pr in entries if any(k in pr for k in keys)]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--config",
        default="config.yaml",
    )
    parser.add_argument(
        "--reference_dir",
        default=None,
    )
    args = parser.parse_args()
    cfg = load_config(args.config)

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    root = Path(cfg["paths"]["generated_dir"])
    ft_m, base_m = root / "personalized_model/prompts.txt", root / "base_model/prompts.txt"

    ft, base = read_manifest(ft_m), read_manifest(base_m)
    ref = Path(args.reference_dir) if args.reference_dir else Path(cfg["paths"]["train_images_dir"])
    gender = cfg["train"]["gender"]

    report: Dict = {
        "clip_personalized": clip_score(ft, device),
        "identity_cosine": face_cosine(ft, ref, device),
    }
    q_name, q_val = no_ref_quality(ft, device)
    report["quality_metric"] = q_name
    report["quality_personalized"] = q_val
    bg, fg = filter_gender(base, gender), filter_gender(ft, gender)
    report["clip_gender_base"] = clip_score(bg, device)
    report["clip_gender_personalized"] = clip_score(fg, device)
    report["clip_gender_delta"] = report["clip_gender_personalized"] - report["clip_gender_base"]

    out = Path(cfg["paths"]["outputs_dir"])
    out.mkdir(parents=True, exist_ok=True)
    rep_path = out / "metrics_report.yaml"
    rep_path.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")
    for k, v in report.items():
        if k != "metrics_note":
            print(f"{k}: {v:.6f}" if isinstance(v, float) else f"{k}: {v}")
    print(rep_path)


if __name__ == "__main__":
    main()
