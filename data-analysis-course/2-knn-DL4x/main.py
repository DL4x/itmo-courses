import scripts.config as cfg
import pandas as pd
from scripts.knn import Classifier
import matplotlib.pyplot as plt


def read_csv():
    return (pd.read_csv(cfg.DATASET_FILE)
            .drop(columns=cfg.UNNECESSARY_COLUMNS))


def filter_data(data, k=2):
    counts = data[cfg.TARGET_CLASS].value_counts()
    return data[data[cfg.TARGET_CLASS].isin(counts[counts >= k].index)]


def show_best_hyperparam(data):
    param_grid = {
        'fixed': [
            True,
            False,
        ],
        'metric': [
            'cosine',
            'euclidean',
            'chebyshev',
            'minkowski',
        ],
        'p': list(range(2, 11)),
        'kernel': [
            'uniform',
            'triangular',
            'epanechnikov',
            'gaussian',
            'common',
        ],
        'k_neighbors': list(range(1, 20)),
        'radius': [0.025, 0.05, 0.1, 0.15],
    }

    classifier = Classifier(
        data,
        cfg.TARGET_CLASS,
    )

    best_params = classifier.best_params(param_grid)

    score, params = best_params

    # Score: 0.34923840609275125
    # Hyperparams: {
    # 'k_neighbors': 7,
    # 'kernel': 'uniform',
    # 'metric': 'minkowski',
    # 'p': 10
    # }
    print(f'Best params:\n '
          f'Score: {score},\n '
          f'Hyperparams: {params}.')


def show_accuracy_rates(data, rng=range(1, 101)):
    train_error_rates = []
    test_error_rates = []
    for k in rng:
        print(f'Testing k_neighbors={k}')

        classifier = Classifier(
            data,
            cfg.TARGET_CLASS,
            fixed=True,
            k_neighbors=k,
            metric='minkowski',
            kernel='uniform',
            p=10,
        )

        classifier.process(scoring=True)

        train_error_rates.append(classifier.training_score())
        test_error_rates.append(classifier.test_score())

    plt.plot(rng, train_error_rates, color='b', label='Train accuracy')
    plt.plot(rng, test_error_rates, color='y', label='Test accuracy')
    plt.grid(True)
    plt.legend()
    plt.xlabel('Number of neighbors')
    plt.ylabel('Accuracy score')
    plt.show()


def show_lowess_efficiency(data):
    classifier_without_lowess = Classifier(
        data,
        cfg.TARGET_CLASS,
        fixed=True,
        k_neighbors=7,
        radius=0.1,
        metric='minkowski',
        kernel='uniform',
        p=10,
        priori_weights=None,
    )

    classifier_without_lowess.process(logging=False, scoring=True)

    score_without_lowess = classifier_without_lowess.test_score()

    classifier_with_lowess = Classifier(
        data,
        cfg.TARGET_CLASS,
        fixed=True,
        k_neighbors=7,
        radius=0.1,
        metric='minkowski',
        kernel='uniform',
        p=10,
        priori_weights_algo='lowess',
    )

    classifier_with_lowess.process(logging=False, scoring=True)

    score_with_lowess = classifier_with_lowess.test_score()

    # Without using LOWESS: 0.3588850174216028
    # With using LOWESS: 0.37282229965156793

    # Without using LOWESS: 0.3623693379790941
    # With using LOWESS: 0.37282229965156793
    print(f'Without using LOWESS: {score_without_lowess}\n'
          f'With using LOWESS: {score_with_lowess}')


def main():
    data = read_csv()

    flt_data = filter_data(data, k=2)

    classifier = Classifier(
        flt_data,
        cfg.TARGET_CLASS,
        fixed=True,
        k_neighbors=7,
        metric='minkowski',
        kernel='uniform',
        p=10,
        priori_weights=None,
    )

    classifier.process(logging=True)

    show_best_hyperparam(flt_data)

    show_accuracy_rates(flt_data)

    show_lowess_efficiency(flt_data)


if __name__ == '__main__':
    main()
