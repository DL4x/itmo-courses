from pathlib import Path

from PIL import Image
import torch
from torch_fidelity import calculate_metrics
from tqdm import tqdm

from models import Generator

REAL_DIR = "datasets/celeba/faces_mtcnn"
OUTPUT_DIR = "outputs/conditional/metrics"
MODEL = "outputs/conditional/checkpoints/generator_last.pt"
CONDITIONAL = True


@torch.no_grad()
def generate_fake_images(
    generator: Generator,
    out_dir: Path,
    n_images: int,
    batch_size: int,
    latent_dim: int,
    device: torch.device,
    conditional: bool,
) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)
    generated = 0
    pbar = tqdm(total=n_images, desc="Generating fakes")
    while generated < n_images:
        cur_bs = min(batch_size, n_images - generated)
        z = torch.randn(cur_bs, latent_dim, device=device)
        labels = None
        if conditional:
            labels = torch.randint(0, 2, (cur_bs,), device=device)
        fake = generator(z, labels) if conditional else generator(z)
        fake = ((fake.clamp(-1, 1) + 1) / 2 * 255).byte().permute(0, 2, 3, 1).cpu().numpy()

        for i in range(cur_bs):
            Image.fromarray(fake[i]).save(out_dir / f"fake_{generated + i:06d}.jpg", quality=95)
        generated += cur_bs
        pbar.update(cur_bs)
    pbar.close()


def main() -> None:
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    output_dir = Path(OUTPUT_DIR)
    fake_dir = output_dir / "fake_for_metrics"
    output_dir.mkdir(parents=True, exist_ok=True)

    gen = Generator(
        latent_dim=128,
        base_channels=64,
        conditional=CONDITIONAL,
    ).to(device)
    gen.load_state_dict(torch.load(MODEL, map_location=device))
    gen.eval()

    generate_fake_images(
        generator=gen,
        out_dir=fake_dir,
        n_images=10000,
        batch_size=128,
        latent_dim=128,
        device=device,
        conditional=CONDITIONAL,
    )

    print("Starting FID/IS calculation...")
    metrics = calculate_metrics(
        input1=str(REAL_DIR),
        input2=str(fake_dir),
        cuda=torch.cuda.is_available(),
        isc=True,
        fid=True,
        kid=False,
        verbose=False,
    )

    fid = metrics["frechet_inception_distance"]
    is_mean = metrics["inception_score_mean"]
    is_std = metrics["inception_score_std"]
    print(f"FID: {fid:.4f}")
    print(f"IS: {is_mean:.4f} +/- {is_std:.4f}")

    report = output_dir / "metrics.txt"
    report.write_text(f"FID: {fid:.6f}\nIS_mean: {is_mean:.6f}\nIS_std: {is_std:.6f}\n", encoding="utf-8")
    print(f"Saved report to {report}")


if __name__ == "__main__":
    main()
