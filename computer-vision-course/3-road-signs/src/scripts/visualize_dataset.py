def main():
    import matplotlib
    matplotlib.use('TkAgg')
    import matplotlib.pyplot as plt
    from matplotlib.patches import Polygon
    from PIL import Image

    img = Image.open("datasets/sign_dataset_yolo_seg/val/images/202.jpg")
    w, h = img.size

    with open("datasets/sign_dataset_yolo_seg/val/labels/202.txt") as f:
        for line in f:
            parts = line.split()
            xs = [float(parts[i]) * w for i in range(1, len(parts), 2)]
            ys = [float(parts[i]) * h for i in range(2, len(parts), 2)]
            plt.gca().add_patch(Polygon(list(zip(xs, ys)), fill=False, edgecolor="lime", linewidth=2))

    plt.imshow(img)
    plt.axis("off")
    plt.show()


if __name__ == '__main__':
    main()
