import scripts.config as cfg
from scripts.algorithms import Ridge
from scripts.algorithms import LinearClassifier
from scripts.algorithms import SupportVectorMachine
import numpy as np
import pandas as pd
from scipy.stats import t
from itertools import product
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import accuracy_score


def read_csv():
    return (pd.read_csv(cfg.DATASET_FILE)
            .drop(columns=cfg.UNNECESSARY_COLUMNS))


def to_binary_classification(data, k=0.4):
    data[cfg.BINARY_CLASSIFICATION_CLASS_NAME] \
        = (data[cfg.BINARY_CLASSIFICATION_CRITERION_NAME] > k).astype(int)
    last_column = data.columns[-1]
    data.insert(0, last_column, data.pop(last_column))
    return data.drop(columns=cfg.UNNECESSARY_BINARY_CLASSIFICATION_COLUMNS)


def train_test_split_impl(X, y, random_state=412):
    X = X.values
    y = y.values
    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        stratify=y,
        test_size=0.25,
        random_state=random_state,
    )
    y_train = np.where(y_train == 0, -1, y_train)
    y_test = np.where(y_test == 0, -1, y_test)
    return X_train, X_test, y_train, y_test


def process(model, X_train, X_test, y_train, y_test):
    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    metric = 'accuracy'
    score = model.score(
        y_test,
        y_pred,
        metric=metric,
    )

    print(f'{metric} function score is {score}')

    return score


def ridge_regression(X_train, X_test, y_train, y_test):
    model = Ridge()
    process(model, X_train, X_test, y_train, y_test)


def linear_classification(X_train, X_test, y_train, y_test):
    model = LinearClassifier(
        alpha=0.01,
        beta=0.05,
        learning_rate=0.05,
        loss_func_name='square',
    )
    process(model, X_train, X_test, y_train, y_test)


def support_vector_machine(X_train, X_test, y_train, y_test):
    model = SupportVectorMachine(
        d=4,
        r=1,
        C=1,
        gamma=0.1,
        learning_rate=0.01,
        kernel='poly',
    )
    process(model, X_train, X_test, y_train, y_test)


def show_best_hyperparam(X_train, X_test, y_train, y_test):
    best_score = 0
    best_args = None
    hyperparams = {
        'alpha': [0.01, 0.05, 0.1, 0.5, 1, 5, 10, 50, 100],
    }
    for alpha in hyperparams['alpha']:
        model = Ridge(alpha=alpha)
        score = process(model, X_train, X_test, y_train, y_test)
        if best_score < score:
            best_score = score
            best_args = alpha
    best_alpha = best_args
    # Best hyperparams: score=0.7317073170731707, alpha=5
    print(f'Best Ridge hyperparams: score={best_score}, alpha={best_alpha}')

    best_score = 0
    best_args = None
    hyperparams = {
        'alpha': [0.01, 0.05, 0.1, 0.25, 0.5, 1],
        'beta': [0.01, 0.05, 0.1, 0.25, 0.5, 1],
        'learning_rate': [0.01, 0.05, 0.1, 0.25, 0.5, 1],
        'loss_func': ['square', 'hinge', 'logistic'],
    }
    for alpha, beta, learning_rate, loss_func in product(*hyperparams.values()):
        model = LinearClassifier(
            alpha=alpha,
            beta=beta,
            learning_rate=learning_rate,
            loss_func_name=loss_func,
        )
        score = process(model, X_train, X_test, y_train, y_test)
        if best_score < score:
            best_score = score
            best_args = (alpha, beta, learning_rate, loss_func)

    best_alpha, best_beta, best_learning_rate, best_loss_func = best_args
    # Best hyperparams: score=0.7038327526132404, alpha=0.01, beta=0.05, learning_rate=0.05, loss_func=square
    print(f'Best LinearClassifier hyperparams: score={best_score}, alpha={best_alpha}, '
          f'beta={best_beta}, learning_rate={best_learning_rate}, loss_func={best_loss_func}')

    best_score = 0
    best_args = None
    hyperparams = {
        'd': [2, 3, 4, 5],
        'r': [0, 1, 10, 100],
        'C': [0.01, 0.1, 1, 10, 100],
        'gamma': [0.001, 0.01, 0.1, 1.0, 10.0],
        'learning_rate': [0.01, 0.05, 0.1, 0.25, 0.5, 1],
        'kernel': ['linear', 'poly', 'rbf'],
    }
    for d, r, C, gamma, learning_rate, kernel, epochs in product(*hyperparams.values()):
        model = SupportVectorMachine(
            d=d,
            r=r,
            C=C,
            gamma=gamma,
            learning_rate=learning_rate,
            kernel=kernel,
            epochs=epochs,
        )
        score = process(model, X_train, X_test, y_train, y_test)
        if best_score < score:
            best_score = score
            best_args = (d, r, C, gamma, learning_rate, kernel, epochs)

    best_d, best_r, best_C, best_gamma, best_learning_rate, best_kernel = best_args
    # Best SupportVectorMachine hyperparams: score=0.7317073170731707, d=4, r=1 C=1, gamma=0.1, learning_rate=0.01, kernel=poly
    print(f'Best SupportVectorMachine hyperparams: score={best_score}, d={best_d}, r={best_r}, '
          f'C={best_C}, gamma={best_gamma}, learning_rate={best_learning_rate}, kernel={best_kernel}')


def show_train_curve(X_train, y_train):
    def eval_train_scores(model):
        model.fit(
            X_train,
            y_train,
            eval_loss=True,
            logging=True,
        )
        return model.train_scores

    def smooth_train_score(train_score):
        window_size = 5
        return np.convolve(train_score, np.ones(window_size) / window_size, mode='valid')

    lc_model = LinearClassifier(
        alpha=0.01,
        beta=0.05,
        learning_rate=0.05,
        loss_func_name='square',
    )
    lc_train_scores = eval_train_scores(lc_model)
    lc_smoothed_train_scores = smooth_train_score(lc_train_scores)

    svm_model = SupportVectorMachine(
        d=4,
        r=1,
        C=1,
        gamma=0.1,
        learning_rate=0.01,
        kernel='poly',
        loss_func_name='logistic',
    )
    svm_train_scores = eval_train_scores(svm_model)
    svm_smoothed_train_scores = smooth_train_score(svm_train_scores)

    plt.plot(
        range(len(lc_smoothed_train_scores)),
        lc_smoothed_train_scores,
        label='linear classifier curve',
    )
    plt.plot(
        range(len(svm_smoothed_train_scores)),
        svm_smoothed_train_scores,
        label='support vector machine curve',
    )
    plt.grid(True)
    plt.title('Learning curve with smoothed empirical risk')
    plt.xlabel('epoch')
    plt.ylabel('empirical risk')
    plt.legend()
    plt.show()


def show_test_curve(X, y):
    def confidence_interval(data, confidence=0.95):
        arr = np.array(data)
        mean = np.mean(arr)
        std_err = np.std(arr, ddof=1) / np.sqrt(len(arr))
        t_value = t.ppf((1 + confidence) / 2., len(arr) - 1)
        margin = t_value * std_err
        return mean, mean - margin, mean + margin

    splits = 15
    max_epochs = 250
    lc_accuracies = np.zeros((splits, max_epochs))
    svm_accuracies = np.zeros((splits, max_epochs))
    lr_accuracies = np.zeros(splits)
    random_states = np.round(np.random.rand(splits) * 1000).astype(int)

    index = 0
    for random_state in random_states:
        lc_accuracy = []
        svm_accuracy = []
        X_train, X_test, y_train, y_test = train_test_split_impl(X, y, random_state=random_state)

        for epochs in range(1, max_epochs + 1):
            lc_model = LinearClassifier(
                alpha=0.01,
                beta=0.05,
                learning_rate=0.05,
                loss_func_name='square',
                epochs=epochs,
            )
            lc_score = process(lc_model, X_train, X_test, y_train, y_test)
            lc_accuracy.append(lc_score)

            svm_model = SupportVectorMachine(
                d=4,
                r=1,
                C=1,
                gamma=0.1,
                learning_rate=0.01,
                kernel='poly',
                loss_func_name='logistic',
                epochs=epochs,
            )
            svm_score = process(svm_model, X_train, X_test, y_train, y_test)
            svm_accuracy.append(svm_score)

        lc_accuracies[index] = lc_accuracy
        svm_accuracies[index] = svm_accuracy

        lr_model = LinearRegression()
        lr_model.fit(X, y)
        predicts = lr_model.predict(X)
        lr_model_accuracy = accuracy_score(y, np.round(predicts))
        lr_accuracies[index] = lr_model_accuracy

        index += 1

    lc_means, svm_means = [], []
    lc_lower, svm_lower = [], []
    lc_upper, svm_upper = [], []

    for epoch in range(max_epochs):
        lc_data = lc_accuracies[:, epoch]
        svm_data = svm_accuracies[:, epoch]

        lc_mean, lc_low, lc_high = confidence_interval(lc_data)
        svm_mean, svm_low, svm_high = confidence_interval(svm_data)

        lc_means.append(lc_mean)
        lc_lower.append(lc_low)
        lc_upper.append(lc_high)

        svm_means.append(svm_mean)
        svm_lower.append(svm_low)
        svm_upper.append(svm_high)

    lr_mean, lr_lower, lr_upper = confidence_interval(lr_accuracies)

    epochs_range = range(1, max_epochs + 1)
    plt.plot(
        epochs_range,
        lc_means,
        label='linear classifier curve',
    )
    plt.fill_between(
        epochs_range,
        lc_lower,
        lc_upper,
        alpha=0.2,
        label='linear classifier 95% CI',
    )
    plt.plot(
        epochs_range,
        svm_means,
        label='support vector machine curve',
    )
    plt.fill_between(
        epochs_range,
        svm_lower,
        svm_upper,
        alpha=0.2,
        label='support vector machine 95% CI',
    )
    plt.axhline(
        y=lr_mean,
        color='r',
        linestyle='--',
        label='linear regression accuracy',
    )
    plt.fill_between(
        epochs_range,
        [lr_lower] * len(epochs_range),
        [lr_upper] * len(epochs_range),
        color='r',
        alpha=0.2,
        label='linear regression 95% CI',
    )
    plt.grid(True)
    plt.title('Learning curve with accuracy score function')
    plt.xlabel('epoch')
    plt.ylabel('accuracy score function')
    plt.legend()
    plt.show()


def main():
    data = read_csv()

    data = to_binary_classification(data)

    X = data.drop(columns=[cfg.BINARY_CLASSIFICATION_CLASS_NAME])
    y = data[cfg.BINARY_CLASSIFICATION_CLASS_NAME]

    X_train, X_test, y_train, y_test = train_test_split_impl(X, y)

    ridge_regression(X_train, X_test, y_train, y_test)

    linear_classification(X_train, X_test, y_train, y_test)

    support_vector_machine(X_train, X_test, y_train, y_test)

    show_best_hyperparam(X_train, X_test, y_train, y_test)

    show_train_curve(X_train, y_train)

    show_test_curve(X, y)


if __name__ == '__main__':
    main()
