# Numpy for math
import numpy as np
import pandas as pd
# For choosing predicted class
from collections import Counter
# Implementing different kernel functions
import scripts.kernels as kernels
# For using model in GridSearchCV
from sklearn.base import BaseEstimator
# For model report
from sklearn.metrics import accuracy_score, classification_report
# For splitting dataset (train and test)
from sklearn.model_selection import train_test_split, cross_val_score, GridSearchCV
# For k-d algorithm implementing
from sklearn.neighbors import KNeighborsClassifier, RadiusNeighborsClassifier, NearestNeighbors


class Classifier(BaseEstimator):
    _kernels = {
        None: lambda arg: arg,
        'uniform': kernels.uniform_kernel,
        'triangular': kernels.triangular_kernel,
        'epanechnikov': kernels.epanechnikov_kernel,
        'gaussian': kernels.gaussian_kernel,
        'common': kernels.common_kernel,
    }

    def __init__(
            self,
            raw_data,
            target_class,
            fixed=True,
            k_neighbors=5,
            radius=0.05,
            metric='minkowski',
            p=2,
            kernel='gaussian',
            test_size=0.25,
            priori_weights=None,
            priori_weights_algo=None,
    ):
        self.params = {
            'raw_data': raw_data,
            'target_class': target_class,
        }

        self._classifier = None
        self._training_score = None
        self._test_score = None
        self._predictions = None
        self._lowess_weights = None

        self.fixed = fixed
        self.k_neighbors = k_neighbors
        self.radius = radius
        self.metric = metric
        self.p = p
        self._kernel = kernel
        self._test_size = test_size
        self._priori_weights = priori_weights
        self._priori_weights_algo = priori_weights_algo

        self._x = raw_data.drop(target_class, axis=1)
        self._y = raw_data[target_class]
        self._y_training = None

    def training_score(self):
        return self._training_score

    def test_score(self):
        return self._test_score

    def predictions(self):
        return self._predictions

    def get_params(self, deep=True):
        return self.params

    def set_params(self, **params):
        for key, value in params.items():
            setattr(self, key, value)
        return self

    @staticmethod
    def _reset_index(*args):
        for arg in args: arg.reset_index(drop=True, inplace=True)

    @staticmethod
    def _split_data(x, y, test_size=0.25, reset=True, stratify=None):
        x_training, x_test, y_training, y_test = train_test_split(
            x, y, test_size=test_size, stratify=stratify
        )

        if reset:
            Classifier._reset_index(x_training, x_test, y_training, y_test)

        return x_training, x_test, y_training, y_test

    @staticmethod
    def _get_classifier(
            fixed,
            k_neighbors,
            radius,
            metric,
            p,
    ):
        if fixed:
            return KNeighborsClassifier(
                n_neighbors=k_neighbors,
                metric=metric,
                p=p,
            )
        else:
            return RadiusNeighborsClassifier(
                radius=radius,
                metric=metric,
                p=p,
            )

    def _lowess_priori_weights(self, x_training, y_training):
        y_smoothed = np.copy(y_training)

        model = NearestNeighbors(
            n_neighbors=self.k_neighbors,
            metric=self.metric
        )
        model.fit(x_training)

        for i in range(len(x_training)):
            xi = pd.DataFrame(x_training.iloc[i]).T
            indexes = model.kneighbors(xi, return_distance=False)[0]
            y_neighbors = y_training.iloc[indexes]
            y_local = y_neighbors.mode().values[0]
            y_smoothed[i] = y_local

        smoothed_values = pd.Series(
            (y_training == y_smoothed).astype(int),
            index=y_training.index,
        )

        class_probs = smoothed_values.groupby(y_training).mean()

        lowess_weights = y_training.map(class_probs)

        lowess_weights_values = np.array([[x] for x in lowess_weights.values])

        return lowess_weights_values

    def fit(self, x, y):
        self._classifier = self._get_classifier(
            self.fixed,
            self.k_neighbors,
            self.radius,
            self.metric,
            self.p,
        )
        self._y_training = y
        self._classifier.fit(x, y)
        return self

    def predict(self, x_test):
        self._reset_index(x_test, self._y_training)
        if isinstance(self._classifier, KNeighborsClassifier):
            dist, indexes = self._classifier.kneighbors(x_test)
            w = self._kernels[self._kernel](dist)
        else:
            dist, indexes = self._classifier.radius_neighbors(x_test)
            w = [self._kernels[self._kernel](d) for d in dist]

        predictions = []
        for i in range(len(x_test)):
            neighbor_weights = w[i]
            neighbor_indexes = indexes[i]

            if len(neighbor_indexes) == 0:
                predictions.append('')
                continue

            neighbor_labels = self._y_training[neighbor_indexes]

            weighted_votes = Counter()
            for brand, weight, index in zip(neighbor_labels, neighbor_weights, neighbor_indexes):
                adjusted_weight = weight
                if self._priori_weights is not None:
                    adjusted_weight *= self._priori_weights[index]
                weighted_votes[brand] += adjusted_weight

            predicted_class = weighted_votes.most_common(1)[0][0]
            predictions.append(predicted_class)

        return predictions

    def process(self, logging=False, scoring=False):
        x_training, x_test, y_training, y_test = self._split_data(
            self._x, self._y, stratify=self._y
        )

        if self._priori_weights_algo == 'lowess':
            lowess_weights = self._lowess_priori_weights(
                x_training,
                y_training,
            )
            self._priori_weights = lowess_weights

        self.fit(x_training, y_training)

        if scoring:
            scores = cross_val_score(
                self._classifier,
                x_training,
                y_training,
                cv=2,
                scoring='accuracy',
            )
            self._training_score = scores.mean()

        predictions = self.predict(x_test)

        self._predictions = np.array(predictions)

        if scoring:
            self._test_score = accuracy_score(y_test, self._predictions)

        if logging:
            print(classification_report(
                y_test,
                self._predictions,
                zero_division=0.0,
            ))

    def best_params(self, param_grid):
        x_training, _, y_training, _ = self._split_data(
            self._x,
            self._y,
            reset=False,
            stratify=self._y
        )

        test_classifier = Classifier(
            self.params['raw_data'],
            self.params['target_class'],
        )

        grid_search = GridSearchCV(
            test_classifier,
            param_grid,
            cv=5,
            scoring='accuracy',
        )

        grid_search.fit(x_training, y_training)

        return grid_search.best_score_, grid_search.best_params_
