import config
from scripts import *

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.metrics import accuracy_score
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split


def read_csv() -> pd.DataFrame:
    return (pd.read_csv(config.DATASET_FILE)
            .drop(columns=config.UNNECESSARY_COLUMNS))


def filter_data(data: pd.DataFrame, k=2) -> pd.DataFrame:
    counts = data[config.TARGET_CLASS].value_counts()
    return data[data[config.TARGET_CLASS].isin(counts[counts >= k].index)]


def prepare_data(data: pd.DataFrame, label_encoder):
    X = data.drop(columns=[config.TARGET_CLASS])
    y = data[config.TARGET_CLASS]

    X = X.values
    y = y.values

    y_encoded = label_encoder.fit_transform(y)
    y_one_hot = np.eye(len(np.unique(y_encoded)))[y_encoded]

    return X, y_one_hot


def process(X_train, X_test, y_train, y_test, label_encoder):
    input_dim = X_train.shape[1]
    output_dim = y_train.shape[1]

    model = ModifiedMLP(
        input_dim=input_dim,
        output_dim=output_dim,
    )

    model.train(X_train, y_train)
    predictions = model.predict(X_test)

    encoded_y_test = np.argmax(y_test, axis=1)
    labels_true = label_encoder.inverse_transform(encoded_y_test)

    labels_pred = label_encoder.inverse_transform(predictions)

    print(accuracy_score(labels_true, labels_pred))


def show_loss_plot(X_train, y_train, epochs=500):
    input_dim = X_train.shape[1]
    output_dim = y_train.shape[1]

    models = [
        ModifiedMLP(
            input_dim,
            output_dim,
            epochs=epochs
        ),
        MultiModifiedMLP(
            input_dim,
            output_dim,
            hidden_dims=[128],
            epochs=epochs
        ),
        MultiModifiedMLP(
            input_dim,
            output_dim,
            hidden_dims=[128, 64],
            epochs=epochs
        ),
        MultiModifiedMLP(
            input_dim,
            output_dim,
            hidden_dims=[128, 64, 32, 16],
            epochs=epochs
        )
    ]

    for i, model in enumerate(models, start=1):
        model.train(X_train, y_train)
        loss = model.loss_history()

        plt.plot(range(epochs), loss, label=f'Model-{i} loss')

    plt.grid(True)
    plt.legend()
    plt.xlabel('Epochs')
    plt.ylabel('Loss value')
    plt.show()


def show_params_plot(X_train, X_test, y_train, y_test, label_encoder):
    input_dim = X_train.shape[1]
    output_dim = y_train.shape[1]

    params = np.arange(1, 201)

    models = [
        MultiSimpleMLP,
        MultiModifiedMLP,
    ]

    fig, axes = plt.subplots(2)
    plt.subplots_adjust(hspace=0.6)

    for ax, model in zip(axes.flatten(), models):
        scores = []
        model_name = None
        for num in params:
            m = model(
                input_dim,
                output_dim,
                hidden_dims=[num]
            )

            if model_name is None:
                model_name = type(m).__name__

            m.train(X_train, y_train)
            predictions = m.predict(X_test)

            encoded_y_test = np.argmax(y_test, axis=1)
            labels_true = label_encoder.inverse_transform(encoded_y_test)

            labels_pred = label_encoder.inverse_transform(predictions)
            scores.append(accuracy_score(labels_true, labels_pred))

        ax.plot(params, scores)
        ax.grid(True)
        ax.set_title(model_name)
        ax.set_xlabel('Params count')
        ax.set_ylabel('Accuracy')

    plt.show()


def show_activation_plot(X_train, X_test, y_train, y_test, label_encoder):
    input_dim = X_train.shape[1]
    output_dim = y_train.shape[1]

    activation_funcs = [
        'identity',
        'tanh',
        'relu',
        'sigmoid',
    ]

    models = [
        SimpleMLP,
        ModifiedMLP,
        MultiSimpleMLP,
        MultiModifiedMLP,
    ]

    fig, axes = plt.subplots(2, 2, figsize=(10, 8))
    plt.subplots_adjust(hspace=0.4, wspace=0.3)

    for ax, model in zip(axes.flatten(), models):
        scores = []
        model_name = None
        for func in activation_funcs:
            m = model(
                input_dim,
                output_dim,
                activation=func,
            )

            if model_name is None:
                model_name = type(m).__name__

            m.train(X_train, y_train)
            predictions = m.predict(X_test)

            encoded_y_test = np.argmax(y_test, axis=1)
            labels_true = label_encoder.inverse_transform(encoded_y_test)

            labels_pred = label_encoder.inverse_transform(predictions)
            scores.append(accuracy_score(labels_true, labels_pred))

        ax.bar(activation_funcs, scores, color=['skyblue', 'salmon', 'lightgreen', 'gold'])
        ax.set_title(model_name)
        ax.set_xlabel('Activation function')
        ax.set_ylabel('Accuracy')
        ax.set_ylim(0, 1.0)

    plt.show()


def main():
    label_encoder = LabelEncoder()

    data = read_csv()

    data = filter_data(data)

    X, y = prepare_data(data, label_encoder)

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        stratify=y,
        test_size=0.25,
    )

    process(X_train, X_test, y_train, y_test, label_encoder)

    # show_loss_plot(X_train, y_train)
    #
    # show_params_plot(X_train, X_test, y_train, y_test, label_encoder)
    #
    # show_activation_plot(X_train, X_test, y_train, y_test, label_encoder)


if __name__ == '__main__':
    main()
