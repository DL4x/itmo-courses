import numpy as np
from numpy.linalg import lstsq as least_squares


class Model:
    def __init__(self, N, M):
        self.N = N
        self.M = M
        self._w = None

    def _create(self, X):
        matrix = np.zeros((
            self.N,
            2 * len(self.M) + 1,
        ))

        for i in range(X.shape[0]):
            index = 0
            matrix[i, index] = 1
            for m in self.M:
                x = 2 * np.pi * X[i] / m
                index += 1
                matrix[i, index] = np.sin(x)
                index += 1
                matrix[i, index] = np.cos(x)

        return matrix

    def fit(self, X, y):
        matrix = self._create(X)
        self._w = least_squares(
            matrix,
            y,
            rcond=None,
        )[0]

    def predict(self, X):
        matrix = self._create(X)
        return matrix @ self._w


def main():
    N = 168
    M = (12, 24, 168, 672)

    X_train = np.array(range(1, N + 1))
    X_test = np.array(range(N + 1, 2 * N + 1))
    y_train = np.array([int(input()) for _ in range(N)])

    model = Model(N, M)
    model.fit(X_train, y_train)
    y_pred = model.predict(X_test)

    print('\n'.join(map(str, y_pred)))


if __name__ == '__main__':
    main()
