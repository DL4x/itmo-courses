import numpy as np


def _normal(u):
    return np.abs(u) < 1


def uniform_kernel(u):
    return 1 / 2 * _normal(u)


def triangular_kernel(u):
    return (1 - np.abs(u)) * _normal(u)


def epanechnikov_kernel(u):
    return 3 / 4 * (1 - u ** 2) * _normal(u)


def gaussian_kernel(u):
    return 1 / np.sqrt(2 * np.pi) * np.exp(-(u ** 2) / 2)


def common_kernel(u, a=1, b=1):
    return (1 - np.abs(u) ** a) ** b
