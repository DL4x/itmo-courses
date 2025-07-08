from sklearn.cluster import KMeans

from .util import *
from .loss import CrossEntropyLoss
from .loss import SoftArgMaxCrossEntropyLoss


class SimpleMLP:
    def __init__(
            self,
            input_dim,
            output_dim,
            activation='identity',
            epochs=300,
            learning_rate=0.01,
    ):
        self._activation = activation
        self._epochs = epochs
        self._learning_rate = learning_rate

        self._w = np.random.normal(size=(input_dim, output_dim))
        self._b = np.zeros((1, output_dim))

        self._loss_func = CrossEntropyLoss()
        self._loss_history = []

    def _forward(self, X):
        X_new = np.dot(X, self._w) + self._b
        X_out = activation_function(X_new, func=self._activation)
        return softmax(X_out)

    def _backward(self, X, y, output):
        output_error = self._loss_func.gradient(output, y)
        self._w -= self._learning_rate * np.dot(X.T, output_error)
        self._b -= self._learning_rate * np.sum(output_error, axis=0, keepdims=True)

    def train(self, X, y):
        for epoch in range(1, self._epochs + 1):
            output = self._forward(X)
            self._backward(X, y, output)

            loss = self._loss_func.loss(output, y)
            self._loss_history.append(loss)
            if not epoch % 10:
                print(f'Epoch {epoch}, Loss: {loss}')

    def predict(self, X):
        output = self._forward(X)
        return np.argmax(output, axis=1)

    def loss_history(self):
        if self._loss_history is None:
            raise ValueError('loss history is empty')
        return self._loss_history


class ModifiedMLP:
    def __init__(
            self,
            input_dim,
            output_dim,
            activation='relu',
            epochs=400,
            learning_rate=0.01,
            centroid_num=40,
            gamma=1.0,
            beta1=0.9,
            beta2=0.99,
    ):
        self._input_dim = input_dim
        self._activation = activation
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._centroids_num = centroid_num
        self._gamma = gamma
        self._beta1 = beta1
        self._beta2 = beta2

        self._w = np.random.normal(size=(centroid_num, output_dim))
        self._b = np.zeros((1, output_dim))
        self._centroids = None

        self._loss_func = SoftArgMaxCrossEntropyLoss(t=1e-2)
        self._loss_history = []

        self._t = 0
        self._m_w = np.zeros_like(self._w)
        self._v_w = np.zeros_like(self._w)
        self._m_b = np.zeros_like(self._b)
        self._v_b = np.zeros_like(self._b)
        self._m_centroids = np.zeros_like(self._centroids)
        self._v_centroids = np.zeros_like(self._centroids)

    def _init_centroids(self, X, n_init=10):
        kmeans = KMeans(
            n_init=n_init,
            n_clusters=self._centroids_num,
        )
        kmeans.fit(X)
        self._centroids = kmeans.cluster_centers_

    def _forward(self, X):
        rbf_out = rbf_kernel(X, self._centroids, self._gamma)
        X_new = np.dot(rbf_out, self._w) + self._b
        X_out = activation_function(X_new, func=self._activation)
        return rbf_out, softmax(X_out)

    def _adam_optimize(self, param_name, gradient):
        m = getattr(self, f'_m_{param_name}')
        v = getattr(self, f'_v_{param_name}')

        m = self._beta1 * m + (1 - self._beta1) * gradient
        v = self._beta2 * v + (1 - self._beta2) * (gradient ** 2)

        m_hat = m / (1 - self._beta1 ** self._t)
        v_hat = v / (1 - self._beta2 ** self._t)

        param = getattr(self, f'_{param_name}')
        param -= self._learning_rate * m_hat / (np.sqrt(v_hat) + 1e-9)

        setattr(self, f'_m_{param_name}', m)
        setattr(self, f'_v_{param_name}', v)
        setattr(self, f'_{param_name}', param)

    def _backward(self, X, rbf_out, y, output):
        self._t += 1

        output_error = self._loss_func.gradient(output, y)
        grad_w_out = np.dot(rbf_out.T, output_error) / X.shape[0]
        grad_b_out = np.sum(output_error, axis=0, keepdims=True) / X.shape[0]

        self._adam_optimize('w', grad_w_out)
        self._adam_optimize('b', grad_b_out)

        d_rbf = np.dot(output_error, self._w.T)
        diff = X[:, np.newaxis, :] - self._centroids[np.newaxis, :, :]

        rbf_grad = 2 * self._gamma * d_rbf[:, :, np.newaxis] * rbf_out[:, :, np.newaxis] * diff / X.shape[0]
        grad_centroids_out = np.sum(rbf_grad, axis=0)

        self._adam_optimize('centroids', grad_centroids_out)

    def train(self, X, y):
        self._init_centroids(X)

        for epoch in range(1, self._epochs + 1):
            rbf_out, output = self._forward(X)
            self._backward(X, rbf_out, y, output)

            loss = self._loss_func.loss(output, y)
            self._loss_history.append(loss)
            if not epoch % 10:
                print(f'Epoch {epoch}, Loss: {loss}')

    def predict(self, X):
        _, output = self._forward(X)
        return np.argmax(output, axis=1)

    def loss_history(self):
        if self._loss_history is None:
            raise ValueError('loss history is empty')
        return self._loss_history


class MultiSimpleMLP:
    def __init__(
            self,
            input_dim,
            output_dim,
            hidden_dims=None,
            activation='relu',
            epochs=1000,
            learning_rate=2e-3,
    ):
        if hidden_dims is None:
            hidden_dims = [128]

        self._activation = activation
        self._epochs = epochs
        self._learning_rate = learning_rate

        self._loss_func = CrossEntropyLoss()
        self._loss_history = []

        self._w = []
        self._b = []
        layer_dims = [input_dim] + hidden_dims + [output_dim]
        for i in range(len(layer_dims) - 1):
            in_dim = layer_dims[i]
            out_dim = layer_dims[i + 1]
            self._w.append(np.random.normal(size=(in_dim, out_dim)))
            self._b.append(np.zeros((1, out_dim)))

    def _forward(self, X):
        self._act = [X]

        for i in range(len(self._w) - 1):
            X = np.dot(X, self._w[i]) + self._b[i]
            X = activation_function(X, self._activation)
            self._act.append(X)

        X = np.dot(X, self._w[-1]) + self._b[-1]
        X = softmax(X)
        self._act.append(X)
        return X

    def _backward(self, _, y, output):
        grads_w = []
        grads_b = []

        error = self._loss_func.gradient(output, y)
        for i in reversed(range(len(self._w))):
            d_w = np.dot(self._act[i].T, error)
            d_b = np.sum(error, axis=0, keepdims=True)
            grads_w.append(d_w)
            grads_b.append(d_b)

            if i != 0:
                d = activation_derivative(self._act[i], self._activation)
                error = np.dot(error, self._w[i].T) * d

        grads_w.reverse()
        grads_b.reverse()

        for i in range(len(self._w)):
            self._w[i] -= self._learning_rate * grads_w[i]
            self._b[i] -= self._learning_rate * grads_b[i]

    def train(self, X, y):
        for epoch in range(1, self._epochs + 1):
            output = self._forward(X)
            self._backward(X, y, output)

            loss = self._loss_func.loss(output, y)
            self._loss_history.append(loss)
            if not epoch % 10:
                print(f'Epoch {epoch}, Loss: {loss}')

    def predict(self, X):
        output = self._forward(X)
        return np.argmax(output, axis=1)

    def loss_history(self):
        if self._loss_history is None:
            raise ValueError('loss history is empty')
        return self._loss_history


class MultiModifiedMLP:
    def __init__(
            self,
            input_dim,
            output_dim,
            hidden_dims=None,
            activation='relu',
            epochs=400,
            learning_rate=2e-2,
            gamma=1.0,
            beta1=0.9,
            beta2=0.99,
    ):
        if hidden_dims is None:
            hidden_dims = [128, 64]

        self._input_dim = input_dim
        self._activation = activation
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._centroids_num = hidden_dims[0]
        self._gamma = gamma
        self._beta1 = beta1
        self._beta2 = beta2

        self._w = []
        self._b = []
        layer_dims = [self._centroids_num] + hidden_dims[1:] + [output_dim]
        for i in range(len(layer_dims) - 1):
            in_dim = layer_dims[i]
            out_dim = layer_dims[i + 1]
            self._w.append(np.random.normal(size=(in_dim, out_dim)))
            self._b.append(np.zeros((1, out_dim)))

        self._centroids = None

        self._loss_func = CrossEntropyLoss()
        self._loss_history = []

        self._t = 0
        self._m_w = [np.zeros_like(w) for w in self._w]
        self._v_w = [np.zeros_like(w) for w in self._w]
        self._m_b = [np.zeros_like(b) for b in self._b]
        self._v_b = [np.zeros_like(b) for b in self._b]
        self._m_centroids = np.zeros_like(self._centroids)
        self._v_centroids = np.zeros_like(self._centroids)

    def _init_centroids(self, X, n_init=10):
        kmeans = KMeans(
            n_init=n_init,
            n_clusters=self._centroids_num,
        )
        kmeans.fit(X)
        self._centroids = kmeans.cluster_centers_

    def _forward(self, X):
        rbf_out = rbf_kernel(X, self._centroids, self._gamma)
        activations = [rbf_out]

        for w, b in zip(self._w, self._b):
            X_new = np.dot(activations[-1], w) + b
            X_out = activation_function(X_new, func=self._activation)
            activations.append(X_out)

        return activations, softmax(activations[-1])

    def _adam_optimize(self, param_name, gradient, idx):
        m = getattr(self, f'_m_{param_name}')[idx]
        v = getattr(self, f'_v_{param_name}')[idx]

        m_new = self._beta1 * m + (1 - self._beta1) * gradient
        v_new = self._beta2 * v + (1 - self._beta2) * (gradient ** 2)

        getattr(self, f'_m_{param_name}')[idx] = m_new
        getattr(self, f'_v_{param_name}')[idx] = v_new

        m_hat = m_new / (1 - self._beta1 ** self._t)
        v_hat = v_new / (1 - self._beta2 ** self._t)

        param = getattr(self, f'_{param_name}')
        param[idx] -= self._learning_rate * m_hat / (np.sqrt(v_hat) + 1e-9)
        setattr(self, f'_{param_name}', param)

    def _backward(self, X, rbf_out, y, output):
        self._t += 1
        output_error = self._loss_func.gradient(output, y)
        errors = [output_error]

        for i in range(len(self._w) - 1, 0, -1):
            d = activation_derivative(rbf_out[i], func=self._activation)
            error = np.dot(errors[0], self._w[i].T) * d
            errors.insert(0, error)

        for i in range(len(self._w)):
            grad_w = np.dot(rbf_out[i].T, errors[i]) / X.shape[0]
            grad_b = np.sum(errors[i], axis=0, keepdims=True) / X.shape[0]
            self._adam_optimize('w', grad_w, i)
            self._adam_optimize('b', grad_b, i)

        rbf_out = rbf_out[0]
        d_rbf = np.dot(errors[0], self._w[0].T)
        diff = X[:, np.newaxis, :] - self._centroids[np.newaxis, :, :]
        rbf_grad = 2 * self._gamma * d_rbf[:, :, np.newaxis] * rbf_out[:, :, np.newaxis] * diff
        grad_centroids_out = np.sum(rbf_grad, axis=0) / X.shape[0]

        self._m_centroids = self._beta1 * self._m_centroids + (1 - self._beta1) * grad_centroids_out
        self._v_centroids = self._beta2 * self._v_centroids + (1 - self._beta2) * (grad_centroids_out ** 2)

        m_hat = self._m_centroids / (1 - self._beta1 ** self._t)
        v_hat = self._v_centroids / (1 - self._beta2 ** self._t)

        self._centroids -= self._learning_rate * m_hat / (np.sqrt(v_hat) + 1e-9)

    def train(self, X, y):
        self._init_centroids(X)

        for epoch in range(1, self._epochs + 1):
            rbf_out, output = self._forward(X)
            self._backward(X, rbf_out, y, output)

            loss = self._loss_func.loss(output, y)
            self._loss_history.append(loss)
            if not epoch % 10:
                print(f'Epoch {epoch}, Loss: {loss}')

    def predict(self, X):
        _, output = self._forward(X)
        return np.argmax(output, axis=1)

    def loss_history(self):
        if self._loss_history is None:
            raise ValueError('loss history is empty')
        return self._loss_history


class CombinedMLP:
    def __init__(
            self,
            input_dim,
            output_dim,
            hidden_dims=64,
            activation='relu',
            epochs=500,
            learning_rate=0.01,
            centroid_num=40,
            gamma=1.0,
            beta1=0.9,
            beta2=0.99,
    ):
        self._input_dim = input_dim
        self._hidden_dims = hidden_dims
        self._output_dim = output_dim
        self._activation = activation
        self._epochs = epochs
        self._learning_rate = learning_rate
        self._centroids_num = centroid_num
        self._gamma = gamma
        self._beta1 = beta1
        self._beta2 = beta2

        self._w1 = np.random.normal(size=(self._input_dim, self._hidden_dims))
        self._b1 = np.zeros((1, self._hidden_dims))

        self._centroids = None
        self._w_rbf = np.random.normal(size=(self._centroids_num, self._hidden_dims))

        self._w2 = np.random.normal(size=(self._hidden_dims * 2, self._output_dim))
        self._b2 = np.zeros((1, self._output_dim))

        self._loss_func = CrossEntropyLoss()
        self._loss_history = []

        self._t = 0
        self._m_w1 = np.zeros_like(self._w1)
        self._v_w1 = np.zeros_like(self._w1)
        self._m_b1 = np.zeros_like(self._b1)
        self._v_b1 = np.zeros_like(self._b1)

        self._m_w_rbf = np.zeros_like(self._w_rbf)
        self._v_w_rbf = np.zeros_like(self._w_rbf)

        self._m_w2 = np.zeros_like(self._w2)
        self._v_w2 = np.zeros_like(self._w2)
        self._m_b2 = np.zeros_like(self._b2)
        self._v_b2 = np.zeros_like(self._b2)
        self._m_centroids = np.zeros_like(self._centroids)
        self._v_centroids = np.zeros_like(self._centroids)

    def _init_centroids(self, X, n_init=10):
        kmeans = KMeans(
            n_init=n_init,
            n_clusters=self._centroids_num,
        )
        kmeans.fit(X)
        self._centroids = kmeans.cluster_centers_

    def _forward(self, X):
        features = []

        linear_out = np.dot(X, self._w1) + self._b1
        linear_out = activation_function(linear_out, func=self._activation)
        features.append(linear_out)

        rbf_out = rbf_kernel(X, self._centroids, self._gamma)
        rbf_out = np.dot(rbf_out, self._w_rbf)
        features.append(rbf_out)

        combined = np.concatenate(features, axis=1)
        output = np.dot(combined, self._w2) + self._b2
        output = activation_function(output, func='identity')

        return features, softmax(output)

    def _adam_optimize(self, param_name, gradient):
        m = getattr(self, f'_m_{param_name}')
        v = getattr(self, f'_v_{param_name}')

        m = self._beta1 * m + (1 - self._beta1) * gradient
        v = self._beta2 * v + (1 - self._beta2) * (gradient ** 2)

        m_hat = m / (1 - self._beta1 ** self._t)
        v_hat = v / (1 - self._beta2 ** self._t)

        param = getattr(self, f'_{param_name}')
        param -= self._learning_rate * m_hat / (np.sqrt(v_hat) + 1e-9)

        setattr(self, f'_m_{param_name}', m)
        setattr(self, f'_v_{param_name}', v)
        setattr(self, f'_{param_name}', param)

    def _backward(self, X, features, y, output):
        self._t += 1

        output_error = self._loss_func.gradient(output, y)

        combined = np.concatenate(features, axis=1)
        grad_w2 = np.dot(combined.T, output_error) / X.shape[0]
        grad_b2 = np.sum(output_error, axis=0, keepdims=True) / X.shape[0]

        self._adam_optimize('w2', grad_w2)
        self._adam_optimize('b2', grad_b2)

        hidden_error = np.dot(output_error, self._w2.T)

        d = activation_derivative(features[0], func=self._activation)
        linear_error = hidden_error[:, :self._hidden_dims] * d

        grad_w1 = np.dot(X.T, linear_error) / X.shape[0]
        grad_b1 = np.sum(linear_error, axis=0, keepdims=True) / X.shape[0]

        self._adam_optimize('w1', grad_w1)
        self._adam_optimize('b1', grad_b1)

        rbf_out = rbf_kernel(X, self._centroids, self._gamma)
        rbf_error = hidden_error[:, -self._hidden_dims:]

        grad_w_rbf = np.dot(rbf_out.T, rbf_error) / X.shape[0]
        self._adam_optimize('w_rbf', grad_w_rbf)

        d_rbf = np.dot(rbf_error, self._w_rbf.T)
        diff = X[:, np.newaxis, :] - self._centroids[np.newaxis, :, :]
        rbf_grad = 2 * self._gamma * d_rbf[:, :, np.newaxis] * rbf_out[:, :, np.newaxis] * diff
        grad_centroids_out = np.sum(rbf_grad, axis=0) / X.shape[0]

        self._m_centroids = self._beta1 * self._m_centroids + (1 - self._beta1) * grad_centroids_out
        self._v_centroids = self._beta2 * self._v_centroids + (1 - self._beta2) * (grad_centroids_out ** 2)

        m_hat = self._m_centroids / (1 - self._beta1 ** self._t)
        v_hat = self._v_centroids / (1 - self._beta2 ** self._t)

        self._centroids -= self._learning_rate * m_hat / (np.sqrt(v_hat) + 1e-9)

    def train(self, X, y):
        self._init_centroids(X)

        for epoch in range(1, self._epochs + 1):
            features, output = self._forward(X)
            self._backward(X, features, y, output)

            loss = self._loss_func.loss(output, y)
            self._loss_history.append(loss)
            if not epoch % 10:
                print(f'Epoch {epoch}, Loss: {loss}')

    def predict(self, X):
        _, output = self._forward(X)
        return np.argmax(output, axis=1)

    def loss_history(self):
        if not self._loss_history:
            raise ValueError('loss history is empty')
        return self._loss_history
