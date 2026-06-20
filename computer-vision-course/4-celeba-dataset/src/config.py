from dataclasses import dataclass
from pathlib import Path
from typing import Any, Dict

import yaml


@dataclass
class TrainConfig:
    data_root: str
    attrs_csv: str
    output_dir: str
    image_size: int = 64
    batch_size: int = 64
    num_workers: int = 4
    latent_dim: int = 128
    base_channels: int = 64
    epochs: int = 30
    lr: float = 2e-4
    betas: tuple[float, float] = (0.0, 0.9)
    n_critic: int = 5
    lambda_gp: float = 10.0
    sample_every: int = 1
    save_every: int = 5
    max_images: int | None = None
    seed: int = 42


def load_config(path: str | Path) -> TrainConfig:
    with Path(path).open("r", encoding="utf-8") as f:
        raw: Dict[str, Any] = yaml.safe_load(f)

    if "betas" in raw:
        raw["betas"] = tuple(raw["betas"])
    return TrainConfig(**raw)
