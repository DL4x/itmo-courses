import random
from pathlib import Path
import sys

import numpy as np
import pandas as pd
import torch
from PIL import Image
from matplotlib import pyplot as plt
from torch.utils.data import DataLoader
from torchvision.utils import make_grid
from tqdm import tqdm

from config import load_config
from dataset import CelebAFacesDataset
from models import Critic, Generator

ROOT_DIR = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT_DIR))

CONFIG = "configs/wgan_gp_celeba.yaml"
CONDITIONAL = True


def set_seed(seed: int) -> None:
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)


def gradient_penalty(
    critic,
    real: torch.Tensor,
    fake: torch.Tensor,
    device: torch.device,
    labels: torch.Tensor | None = None,
) -> torch.Tensor:
    batch_size = real.size(0)
    alpha = torch.rand(batch_size, 1, 1, 1, device=device)
    inter = alpha * real + (1 - alpha) * fake
    inter.requires_grad_(True)

    scores = critic(inter, labels) if labels is not None else critic(inter)
    grad = torch.autograd.grad(
        outputs=scores,
        inputs=inter,
        grad_outputs=torch.ones_like(scores),
        create_graph=True,
        retain_graph=True,
        only_inputs=True,
    )[0]
    grad = grad.view(batch_size, -1)
    gp = ((grad.norm(2, dim=1) - 1) ** 2).mean()
    return gp


@torch.no_grad()
def save_sample_grid(
    generator,
    latent_dim: int,
    out_path: str | Path,
    device: torch.device,
    n: int = 64,
    labels: torch.Tensor | None = None,
) -> None:
    generator.eval()
    z = torch.randn(n, latent_dim, device=device)
    fake = generator(z, labels) if labels is not None else generator(z)
    fake = (fake.clamp(-1, 1) + 1) / 2
    grid = make_grid(fake, nrow=int(np.sqrt(n)))
    arr = (grid.permute(1, 2, 0).cpu().numpy() * 255).astype(np.uint8)
    Image.fromarray(arr).save(out_path)


def save_losses_csv(history: list[dict], path: str | Path) -> None:
    pd.DataFrame(history).to_csv(path, index=False)


def plot_losses(history_csv: str | Path, out_png: str | Path) -> None:
    df = pd.read_csv(history_csv)
    df = df.iloc[125:]

    plt.figure(figsize=(10, 5))
    plt.plot(df["step"], df["critic_loss"], label="critic_loss")
    plt.plot(df["step"], df["gen_loss"], label="gen_loss")
    plt.xlabel("step")
    plt.ylabel("loss")
    plt.title("WGAN-GP Learning Curves")
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(out_png, dpi=150)
    plt.close()


def main() -> None:
    cfg = load_config(CONFIG)
    set_seed(cfg.seed)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    output_dir = Path(cfg.output_dir) / ("conditional" if CONDITIONAL else "unconditional")
    samples_dir = output_dir / "samples"
    ckpt_dir = output_dir / "checkpoints"
    samples_dir.mkdir(parents=True, exist_ok=True)
    ckpt_dir.mkdir(parents=True, exist_ok=True)

    dataset = CelebAFacesDataset(
        image_dir=cfg.data_root,
        attrs_csv=cfg.attrs_csv,
        image_size=cfg.image_size,
        max_images=cfg.max_images,
        conditional=CONDITIONAL,
        label_attr="Male",
    )
    loader = DataLoader(
        dataset,
        batch_size=cfg.batch_size,
        shuffle=True,
        num_workers=cfg.num_workers,
        pin_memory=torch.cuda.is_available(),
        drop_last=True,
    )

    gen = Generator(
        latent_dim=cfg.latent_dim,
        base_channels=cfg.base_channels,
        conditional=CONDITIONAL,
    ).to(device)
    critic = Critic(
        base_channels=cfg.base_channels,
        conditional=CONDITIONAL,
        image_size=cfg.image_size,
    ).to(device)

    opt_g = torch.optim.Adam(gen.parameters(), lr=cfg.lr, betas=cfg.betas)
    opt_c = torch.optim.Adam(critic.parameters(), lr=cfg.lr, betas=cfg.betas)

    step = 0
    history: list[dict] = []
    fixed_labels = None
    if CONDITIONAL:
        fixed_labels = torch.tensor(([0] * 32) + ([1] * 32), dtype=torch.long, device=device)

    for epoch in range(1, cfg.epochs + 1):
        pbar = tqdm(loader, desc=f"Epoch {epoch}/{cfg.epochs}")
        for batch in pbar:
            if CONDITIONAL:
                real, labels = batch
                labels = labels.to(device)
            else:
                real = batch
                labels = None
            real = real.to(device)

            critic_loss_val = 0.0
            for _ in range(cfg.n_critic):
                z = torch.randn(real.size(0), cfg.latent_dim, device=device)
                fake = gen(z, labels).detach() if CONDITIONAL else gen(z).detach()

                real_score = critic(real, labels) if CONDITIONAL else critic(real)
                fake_score = critic(fake, labels) if CONDITIONAL else critic(fake)
                gp = gradient_penalty(critic, real, fake, device, labels if CONDITIONAL else None)
                critic_loss = fake_score.mean() - real_score.mean() + cfg.lambda_gp * gp

                opt_c.zero_grad(set_to_none=True)
                critic_loss.backward()
                opt_c.step()
                critic_loss_val = critic_loss.item()

            z = torch.randn(real.size(0), cfg.latent_dim, device=device)
            fake = gen(z, labels) if CONDITIONAL else gen(z)
            gen_score = critic(fake, labels) if CONDITIONAL else critic(fake)
            gen_loss = -gen_score.mean()

            opt_g.zero_grad(set_to_none=True)
            gen_loss.backward()
            opt_g.step()

            step += 1
            history.append(
                {"epoch": epoch, "step": step, "critic_loss": critic_loss_val, "gen_loss": gen_loss.item()}
            )
            pbar.set_postfix({"c_loss": f"{critic_loss_val:.3f}", "g_loss": f"{gen_loss.item():.3f}"})

        if epoch % cfg.sample_every == 0:
            save_sample_grid(
                gen,
                cfg.latent_dim,
                samples_dir / f"epoch_{epoch:03d}.png",
                device,
                n=64,
                labels=fixed_labels if CONDITIONAL else None,
            )

        if epoch % cfg.save_every == 0:
            torch.save(gen.state_dict(), ckpt_dir / f"generator_epoch_{epoch:03d}.pt")
            torch.save(critic.state_dict(), ckpt_dir / f"critic_epoch_{epoch:03d}.pt")

    losses_csv = output_dir / "losses.csv"
    save_losses_csv(history, losses_csv)
    plot_losses(losses_csv, output_dir / "learning_curves.png")
    torch.save(gen.state_dict(), ckpt_dir / "generator_last.pt")
    torch.save(critic.state_dict(), ckpt_dir / "critic_last.pt")
    print(f"Training complete. Outputs at: {output_dir}")


if __name__ == "__main__":
    main()
