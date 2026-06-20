import argparse
import subprocess
import sys
import urllib.request
from pathlib import Path

import yaml

SCRIPT_URL = ("https://raw.githubusercontent.com/huggingface/diffusers/v0.31.0/examples/dreambooth"
              "/train_dreambooth_lora.py")


def load_config(path: str) -> dict:
    with open(path, "r", encoding="utf-8") as f:
        return yaml.safe_load(f)


def ensure_script(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if not path.exists():
        urllib.request.urlretrieve(SCRIPT_URL, path)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--config",
        default="config.yaml",
    )
    parser.add_argument(
        "--resume",
        default=None,
    )
    args = parser.parse_args()

    cfg = load_config(args.config)
    train, paths, model = cfg["train"], cfg["paths"], cfg["model"]
    script_path = Path("scripts/train_dreambooth_lora_hf.py").resolve()
    ensure_script(script_path)

    out = Path(paths["lora_dir"]).resolve()
    out.mkdir(parents=True, exist_ok=True)
    instance = Path(paths["train_images_dir"]).resolve()

    mixed = "fp16" if model.get("torch_dtype", "float16") in {"float16", "fp16"} else "no"

    cmd = [
        sys.executable,
        "-m",
        "accelerate.commands.launch",
        str(script_path),
        "--pretrained_model_name_or_path",
        model["base_model"],
        "--instance_data_dir",
        str(instance),
        "--instance_prompt",
        str(train["instance_prompt"]),
        "--output_dir",
        str(out),
        "--resolution",
        str(train["image_size"]),
        "--train_batch_size",
        str(train["train_batch_size"]),
        "--gradient_accumulation_steps",
        str(train["gradient_accumulation_steps"]),
        "--learning_rate",
        str(train["learning_rate"]),
        "--lr_scheduler",
        str(train["lr_scheduler"]),
        "--lr_warmup_steps",
        str(train["lr_warmup_steps"]),
        "--max_train_steps",
        str(train["max_train_steps"]),
        "--checkpointing_steps",
        str(train["save_every_steps"]),
        "--seed",
        str(cfg["seed"]),
        "--rank",
        str(8),
        "--with_prior_preservation",
        "--class_data_dir",
        str(Path(paths["class_images_dir"]).resolve()),
        "--class_prompt",
        str(train["class_prompt"]),
        "--num_class_images",
        str(train["num_class_images"]),
        "--prior_loss_weight",
        str(train["prior_loss_weight"]),
        "--dataloader_num_workers",
        str(train.get("num_workers", 0)),
    ]
    if mixed != "no":
        cmd.extend(["--mixed_precision", mixed])
    rev = model.get("revision")
    if rev:
        cmd.extend(["--revision", str(rev)])

    resume = args.resume if args.resume is not None else train.get("resume_from_checkpoint")
    if resume:
        resume = str(resume).strip()
        if resume.lower() in {"", "null", "none", "~"}:
            resume = None
    if resume:
        cmd.extend(["--resume_from_checkpoint", resume])
        print(f"--resume_from_checkpoint {resume}")

    print(" ".join(cmd))
    subprocess.run(cmd, check=True)
    print(f"Done: {out}")


if __name__ == "__main__":
    main()
