import numpy as np
# noinspection PyUnresolvedReferences
from sklearn.utils.extmath import softmax


def activation_function(X, func='identity'):
    if func == 'identity':
        return X
    elif func == 'relu':
        return np.maximum(0, X)
    elif func == 'tanh':
        return np.tanh(X)
    elif func == 'sigmoid':
        return 1 / (1 + np.exp(-X))
    else:
        raise ValueError(f'Invalid activation function: {func}')


def activation_derivative(X, func='identity'):
    if func == 'identity':
        return np.ones_like(X)
    elif func == 'relu':
        return (X > 0).astype(float)
    elif func == 'tanh':
        return 1 - np.tanh(X) ** 2
    elif func == 'sigmoid':
        sign = activation_function(X, 'sigmoid')
        return sign * (1 - sign)
    else:
        raise ValueError(f'Invalid activation function: {func}')


def rbf_kernel(x1, x2, gamma):
    x1_sq = np.sum(x1 ** 2, axis=1).reshape(-1, 1)
    x2_sq = np.sum(x2 ** 2, axis=1).reshape(1, -1)
    dist = x1_sq + x2_sq - 2 * x1 @ x2.T
    return np.exp(-gamma * dist)
