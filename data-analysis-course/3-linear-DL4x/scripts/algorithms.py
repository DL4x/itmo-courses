import numpy as np
from sklearn.metrics import accuracy_score


def _score(y_test, y_pred, metric='mse'):
    def mse():
        return np.mean((y_pred - y_test) ** 2)

    def accuracy():
        y_pred_binary = np.where(y_pred >= 0, 1, -1)
        return accuracy_score(y_test, y_pred_binary)

    metrics = {
        'mse': mse,
        'accuracy': accuracy,
    }

    return metrics[metric]()


def loss_func(y_true, y_pred, loss='square'):
    if loss == 'square':
        margin = y_true * y_pred
        return (1 - margin) ** 2
    if loss == 'hinge':
        margin = y_true * y_pred
        return np.maximum(0, 1 - margin)
    if loss == 'logistic':
        margin = y_true * y_pred
        return np.log(1 + np.exp(-margin))
    raise ValueError(f'Invalid loss function: {loss}')


def loss_func_grad(y_true, y_pred, loss='square'):
    if loss == 'square':
        return -2 * (1 - y_true * y_pred)
    if loss == 'hinge':
        return np.where(1 - y_true * y_pred > 0, -y_true, 0)
    if loss == 'logistic':
        exp_term = np.exp(-y_true * y_pred)
        return -(y_true * exp_term) / (1 + exp_term)
    raise ValueError(f'Invalid loss function: {loss}')


class Ridge:
    def __init__(self, alpha=0.5):
        self._coef = None
        self._alpha = alpha

    def fit(self, X, y):
        I = np.identity(X.shape[1])
        I[0, 0] = 0

        X1 = X.T @ X + self._alpha * I
        X1_inv = np.linalg.inv(X1)

        X2 = X1_inv @ X.T

        self._coef = X2 @ y

    def predict(self, X):
        return X @ self._coef

    @staticmethod
    def score(y_test, y_pred, metric='mse'):
        return _score(y_test, y_pred, metric)


class LinearClassifier:
    def __init__(
            self,
            alpha=0.2,
            beta=0.5,
            epochs=250,
            learning_rate=0.01,
            loss_func_name='square',
    ):
        self._alpha = alpha
        self._beta = beta
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._loss_func_name = loss_func_name
        self._w = None
        self._b = None
        self.train_scores = []

    def fit(self, X, y, eval_loss=False, logging=False):
        def regularization():
            return self._alpha * np.sum(np.abs(self._w)) + self._beta * np.sum(self._w ** 2) / 2

        def regularization_gradient():
            return self._alpha * np.sign(self._w) + self._beta * self._w

        def loss_function(pred):
            return loss_func(y, pred, self._loss_func_name) + regularization()

        def loss_gradient(pred):
            loss = loss_func_grad(y, pred, self._loss_func_name)
            reg_grad = regularization_gradient()

            dw = (1 / n_samples) * np.dot(X.T, loss) + reg_grad
            db = (1 / n_samples) * np.sum(loss)
            return dw, db

        n_samples, n_features = X.shape
        self._w = np.zeros(n_features)
        self._b = 0

        for epoch in range(self._epochs):
            y_pred = np.dot(X, self._w) + self._b
            diff_w, diff_b = loss_gradient(y_pred)

            if eval_loss:
                current_loss = np.sum(loss_function(y_pred)) / n_samples
                self.train_scores.append(current_loss)
                if logging:
                    print(f'epoch number is {epoch + 1}, loss is {current_loss}')

            self._w -= self._learning_rate * diff_w
            self._b -= self._learning_rate * diff_b

    def predict(self, X):
        return np.dot(X, self._w) + self._b

    @staticmethod
    def score(y_test, y_pred, metric='mse'):
        return _score(y_test, y_pred, metric)


class SupportVectorMachine:
    def __init__(
            self,
            d=3,
            r=0.0,
            C=1.0,
            gamma=1.0,
            epochs=250,
            learning_rate=0.01,
            kernel='linear',
            loss_func_name='square',
    ):
        self._d = d
        self._r = r
        self._C = C
        self._gamma = gamma
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._kernel = kernel
        self._loss_func_name = loss_func_name
        self._w = None
        self._b = None
        self._X_train = None
        self._y_train = None
        self.train_scores = []

    def _kernel_func(self, x1, x2):
        if self._kernel == 'linear':
            return x1 @ x2.T
        if self._kernel == 'poly':
            return (self._gamma * (x1 @ x2.T) + self._r) ** self._d
        if self._kernel == 'rbf':
            x1_sq = np.sum(x1 ** 2, axis=1).reshape(-1, 1)
            x2_sq = np.sum(x2 ** 2, axis=1).reshape(1, -1)
            dist = x1_sq + x2_sq - 2 * x1 @ x2.T
            return np.exp(-self._gamma * dist)
        raise ValueError(f'Invalid kernel: {self._kernel}')

    def fit(self, X, y, eval_loss=False, logging=False):
        def restore_conditions(dev, max_iter=5):
            for _ in range(max_iter):
                if np.abs(dev) < 1e-8:
                    break

                if dev > 0:
                    index = np.where(y > 0)[0]
                    delta = dev / len(index)
                else:
                    index = np.where(y < 0)[0]
                    delta = -dev / len(index)
                self._w[index] = np.clip(self._w[index] - delta, 0, self._C)

                dev = np.sum(self._w * y)

        def calculate_bias():
            sv_indices = np.where((self._w > 1e-8) & (self._w < self._C - 1e-8))[0]
            if len(sv_indices) > 0:
                k = sv_indices[0]
                self._b = y[k] - np.sum((self._w * y) * K[:, k])
            else:
                sv_indices = np.where(self._w > 1e-8)[0]
                if len(sv_indices) > 0:
                    self._b = np.mean(y[sv_indices] - np.sum((self._w * y)[:, None] * K[:, sv_indices], axis=0))
                else:
                    self._b = 0.0

        self._X_train = X
        self._y_train = y

        n = X.shape[0]
        y = y.astype(float)

        self._w = np.zeros(n)
        K = self._kernel_func(X, X)

        for epoch in range(self._epochs):
            grad = 1 - y * (K @ (self._w * y))
            self._w += self._learning_rate * grad

            self._w = np.clip(self._w, 0, self._C)
            deviation = np.sum(self._w * y)
            if np.abs(deviation) > 1e-8:
                restore_conditions(deviation)

            if eval_loss:
                calculate_bias()
                y_pred = self.predict(X)
                current_loss = np.sum(loss_func(y, y_pred, self._loss_func_name)) / n
                self.train_scores.append(current_loss)
                if logging:
                    print(f'epoch number is {epoch}, loss is {current_loss}')

        calculate_bias()

    def predict(self, X):
        kernel_X = self._kernel_func(X, self._X_train)
        return (kernel_X @ (self._w * self._y_train)) + self._b

    @staticmethod
    def score(y_test, y_pred, metric='mse'):
        return _score(y_test, y_pred, metric)
