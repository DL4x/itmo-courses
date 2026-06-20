import argparse

from src.train import run_training, run_testing

DATASET_ROOT = "confirmed_fronts"
OUTPUT_ROOT = "runs"


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--model",
        type=str,
        choices=[
            "resnet_scratch",
            "resnet18_pretrained",
            "mobilenet_pretrained",
        ],
        default=None,
    )

    args = parser.parse_args()

    # output_dir = f"{OUTPUT_ROOT}/{args.model}"
    # run_training(
    #     data_root=DATASET_ROOT,
    #     model_name=args.model,
    #     output_dir=output_dir,
    #     epochs=20,
    #     batch_size=32,
    #     lr=1e-3,
    # )

    model_path = f"{OUTPUT_ROOT}/{args.model}/{args.model}.pt"
    run_testing(
        data_root=DATASET_ROOT,
        checkpoint_path=model_path,
        model_name=args.model,
        min_test_samples=50,
    )


if __name__ == "__main__":
    main()
