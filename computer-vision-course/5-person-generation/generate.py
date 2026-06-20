import argparse
from pathlib import Path

import torch
import yaml
from diffusers import StableDiffusionPipeline


def load_config(path: str) -> dict:
    with open(path, "r", encoding="utf-8") as f:
        return yaml.safe_load(f)


def parse_dtype(s: str) -> torch.dtype:
    m = {"float16": torch.float16, "fp16": torch.float16, "float32": torch.float32, "fp32": torch.float32}
    if s not in m:
        raise ValueError(s)
    return m[s]


def build_plan(cfg: dict) -> list[tuple[str, str]]:
    g, t = cfg["generation"], cfg["train"]
    token, gender, suf = t["token"], t["gender"], g["quality_suffix"]
    plan = []
    for i, p in enumerate(g["portraits"]):
        plan.append((f"portrait_{i}", f"{p.format(token=token, gender=gender)}, {suf}"))
    for i, p in enumerate(g["token_scenes"]):
        plan.append((f"token_{i}", f"{p.format(token=token, gender=gender)}, {suf}"))
    for i, p in enumerate(g["gender_scenes"]):
        plan.append((f"gender_{i}", f"{p.format(token=token, gender=gender)}, {suf}"))
    return plan


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--config",
        default="config.yaml",
    )
    parser.add_argument(
        "--use_base",
        action="store_true",
    )
    parser.add_argument(
        "--lora_dir",
        default=None,
    )
    args = parser.parse_args()
    cfg = load_config(args.config)

    device = "cuda" if torch.cuda.is_available() else "cpu"
    dtype = parse_dtype(cfg["model"]["torch_dtype"])
    model_cfg = cfg["model"]
    rev = model_cfg.get("revision")
    if rev in (None, "", "null"):
        rev = None
    pipe = StableDiffusionPipeline.from_pretrained(
        model_cfg["base_model"],
        revision=rev,
        torch_dtype=dtype,
        safety_checker=None,
    ).to(device)

    if not args.use_base:
        lora_path = Path(args.lora_dir or cfg["paths"]["lora_dir"]).resolve()
        wname = cfg["paths"].get("lora_weight_name")
        if wname:
            pipe.load_lora_weights(str(lora_path), weight_name=wname)
        else:
            pipe.load_lora_weights(str(lora_path))

    out = Path(cfg["paths"]["generated_dir"]) / ("base_model" if args.use_base else "personalized_model")
    out.mkdir(parents=True, exist_ok=True)
    gen_cfg = cfg["generation"]
    base_seed = int(gen_cfg.get("seed", cfg["seed"]))
    per_image = bool(gen_cfg.get("per_image_seed", True))
    steps = int(gen_cfg["num_inference_steps"])
    guidance = float(gen_cfg["guidance_scale"])
    npp = int(gen_cfg["num_images_per_prompt"])

    manifest = []
    idx = 0
    shared_gen = None
    for tag, prompt in build_plan(cfg):
        for _ in range(npp):
            if per_image:
                gen = torch.Generator(device=device).manual_seed(base_seed + idx)
            else:
                if shared_gen is None:
                    shared_gen = torch.Generator(device=device).manual_seed(base_seed)
                gen = shared_gen
            img = pipe(prompt=prompt, num_inference_steps=steps, guidance_scale=guidance, generator=gen).images[0]
            fn = f"{idx:02d}_{tag}.png"
            img.save(out / fn)
            manifest.append((fn, prompt))
            idx += 1

    with open(out / "prompts.txt", "w", encoding="utf-8") as f:
        f.write("# portraits | token_scenes | gender_scenes\n")
        for fn, p in manifest:
            f.write(f"{fn}\t{p}\n")
    print(out)


if __name__ == "__main__":
    main()
