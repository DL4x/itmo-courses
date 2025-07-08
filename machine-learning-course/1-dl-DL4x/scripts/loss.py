from .util import *


class CrossEntropyLoss:
    def __init__(self, eps=1e-9):
        self.eps = eps

    def _clip_logits(self, logits):
        return np.clip(logits, self.eps, 1 - self.eps)

    def loss(self, logits, y_true):
        pred = self._clip_logits(logits)
        return -np.sum(softmax(y_true) * np.log(pred)) / logits.shape[0]

    def gradient(self, logits, y_true):
        pred = self._clip_logits(logits)
        return pred - y_true


class SoftArgMaxCrossEntropyLoss:
    def __init__(self, t=1.0, eps=1e-9):
        self.t = t
        self.eps = eps

    def softmax(self, x):
        return np.log(np.sum(np.exp(x / self.t)))

    def soft_argmax(self, x):
        exp_logits = np.exp(x / self.t)
        return exp_logits / (np.sum(exp_logits, axis=-1, keepdims=True) + self.eps)

    def loss(self, x, y_true):
        probs = self.softmax(x)
        # probs = self.soft_argmax(x)
        probs = np.clip(probs, self.eps, 1.0 - self.eps)
        cross_entropy = -np.sum(y_true * (x - probs), axis=-1)
        # cross_entropy = -np.sum(y_true * np.log(probs), axis=-1)
        return np.mean(cross_entropy)

    def gradient(self, x, y_true):
        probs = self.soft_argmax(x)
        return (probs - y_true) / (x.shape[0] * self.t)
