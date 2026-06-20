from pathlib import Path

import pandas as pd
from PIL import Image
import torch
from torch.utils.data import Dataset
from torchvision import transforms


class CelebAFacesDataset(Dataset):
    def __init__(
        self,
        image_dir: str | Path,
        attrs_csv: str | Path,
        image_size: int = 64,
        max_images: int | None = None,
        conditional: bool = False,
        label_attr: str = "Male",
    ) -> None:
        self.image_dir = Path(image_dir)
        self.conditional = conditional
        self.label_attr = label_attr

        attrs = pd.read_csv(attrs_csv)
        attrs["image_id"] = attrs["image_id"].astype(str)
        attrs[label_attr] = attrs[label_attr].map({-1: 0, 1: 1})

        image_paths = {p.name: p for p in self.image_dir.glob("*.jpg")}
        attrs = attrs[attrs["image_id"].isin(image_paths.keys())].copy()

        if max_images is not None:
            attrs = attrs.iloc[:max_images]

        self.items = []
        for _, row in attrs.iterrows():
            self.items.append((image_paths[row["image_id"]], int(row[label_attr])))

        self.transform = transforms.Compose(
            [
                transforms.Resize((image_size, image_size)),
                transforms.ToTensor(),
                transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5]),
            ]
        )

    def __len__(self) -> int:
        return len(self.items)

    def __getitem__(self, idx: int):
        image_path, label = self.items[idx]
        image = Image.open(image_path).convert("RGB")
        image = self.transform(image)
        if self.conditional:
            return image, torch.tensor(label, dtype=torch.long)
        return image
