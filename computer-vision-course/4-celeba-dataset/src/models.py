import torch
import torch.nn as nn


def init_weights(module: nn.Module) -> None:
    if isinstance(module, (nn.Conv2d, nn.ConvTranspose2d, nn.BatchNorm2d)):
        nn.init.normal_(module.weight.data, 0.0, 0.02)
        if getattr(module, "bias", None) is not None:
            nn.init.constant_(module.bias.data, 0.0)


class Generator(nn.Module):
    def __init__(
        self,
        latent_dim: int = 128,
        base_channels: int = 64,
        conditional: bool = False,
        num_classes: int = 2,
        embed_dim: int = 16,
    ) -> None:
        super().__init__()
        self.conditional = conditional
        self.latent_dim = latent_dim
        self.embed_dim = embed_dim if conditional else 0

        if conditional:
            self.label_embed = nn.Embedding(num_classes, embed_dim)

        in_dim = latent_dim + self.embed_dim
        self.net = nn.Sequential(
            nn.ConvTranspose2d(in_dim, base_channels * 8, 4, 1, 0, bias=False),
            nn.BatchNorm2d(base_channels * 8),
            nn.ReLU(True),
            nn.ConvTranspose2d(base_channels * 8, base_channels * 4, 4, 2, 1, bias=False),
            nn.BatchNorm2d(base_channels * 4),
            nn.ReLU(True),
            nn.ConvTranspose2d(base_channels * 4, base_channels * 2, 4, 2, 1, bias=False),
            nn.BatchNorm2d(base_channels * 2),
            nn.ReLU(True),
            nn.ConvTranspose2d(base_channels * 2, base_channels, 4, 2, 1, bias=False),
            nn.BatchNorm2d(base_channels),
            nn.ReLU(True),
            nn.ConvTranspose2d(base_channels, 3, 4, 2, 1, bias=False),
            nn.Tanh(),
        )
        self.apply(init_weights)

    def forward(self, z: torch.Tensor, labels: torch.Tensor | None = None) -> torch.Tensor:
        if self.conditional:
            if labels is None:
                raise ValueError("labels are required for conditional generator")
            label_vec = self.label_embed(labels)
            z = torch.cat([z, label_vec], dim=1)
        x = z.view(z.size(0), z.size(1), 1, 1)
        return self.net(x)


class Critic(nn.Module):
    def __init__(
        self,
        base_channels: int = 64,
        conditional: bool = False,
        num_classes: int = 2,
        image_size: int = 64,
    ) -> None:
        super().__init__()
        self.conditional = conditional
        self.image_size = image_size

        self.backbone = nn.Sequential(
            nn.Conv2d(3, base_channels, 4, 2, 1, bias=False),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(base_channels, base_channels * 2, 4, 2, 1, bias=False),
            nn.InstanceNorm2d(base_channels * 2, affine=True),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(base_channels * 2, base_channels * 4, 4, 2, 1, bias=False),
            nn.InstanceNorm2d(base_channels * 4, affine=True),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(base_channels * 4, base_channels * 8, 4, 2, 1, bias=False),
            nn.InstanceNorm2d(base_channels * 8, affine=True),
            nn.LeakyReLU(0.2, inplace=True),
        )

        self.final = nn.Conv2d(base_channels * 8, 1, 4, 1, 0, bias=False)
        if conditional:
            self.label_embed = nn.Embedding(num_classes, base_channels * 8 * 4 * 4)

        self.apply(init_weights)

    def forward(self, x: torch.Tensor, labels: torch.Tensor | None = None) -> torch.Tensor:
        feat = self.backbone(x)
        out = self.final(feat).view(-1)
        if self.conditional:
            if labels is None:
                raise ValueError("labels are required for conditional critic")
            proj = self.label_embed(labels).view(feat.size(0), feat.size(1), feat.size(2), feat.size(3))
            out = out + (feat * proj).sum(dim=(1, 2, 3)) / (feat.shape[1] * feat.shape[2] * feat.shape[3])
        return out
