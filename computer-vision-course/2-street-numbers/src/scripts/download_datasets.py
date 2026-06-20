import os

from roboflow import Roboflow


def main():
    rf = Roboflow(api_key="api-key")

    project = rf.workspace("university-of-toronto-xho85").project("numberdetection-eppfj")
    version = project.version(2)
    dataset = version.download("yolov8")

    print(f"Saved in: {dataset.location}")
    print(f"Dataset files: {os.listdir(dataset.location)}")

    project = rf.workspace("soumyadeep-dutta").project("yolov5-svhn")
    version = project.version(1)
    dataset = version.download("yolov8")

    print(f"Saved in: {dataset.location}")
    print(f"Dataset files: {os.listdir(dataset.location)}")

    project = rf.workspace("onurbachelor").project("svhn-yolo-z3o4t")
    version = project.version(1)
    dataset = version.download("yolov8")

    print(f"Saved in: {dataset.location}")
    print(f"Dataset files: {os.listdir(dataset.location)}")


if __name__ == "__main__":
    main()
