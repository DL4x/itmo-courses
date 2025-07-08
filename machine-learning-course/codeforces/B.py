import numpy as np

matrix = []
d_matrix = []


class Functions:
    @staticmethod
    def tnh(x):
        return np.tanh(x)

    @staticmethod
    def d_tnh(x, d):
        return (1 - np.tanh(x) ** 2) * d

    @staticmethod
    def rlu(x, alpha):
        return np.where(0 <= x, x, x / alpha)

    @staticmethod
    def d_rlu(x, alpha, d):
        return np.where(x < 0, d / alpha, d)

    @staticmethod
    def mul(x, y):
        return x @ y

    @staticmethod
    def d_mul(x, y, d):
        return d @ y.T, x.T @ d

    @staticmethod
    def sum(xs):
        return np.sum(xs, axis=0)

    @staticmethod
    def d_sum(l, d):
        return [d.copy() for _ in range(l)]

    @staticmethod
    def had(xs):
        return np.prod(xs, axis=0)

    @staticmethod
    def d_had(xs, d):
        result = []
        for i in range(len(xs)):
            result.append(np.prod(xs[:i] + xs[i + 1:], axis=0) * d)
        return np.array(result)


def main():
    N, M, K = map(int, input().split())

    vars_list = []
    for i in range(M):
        var, r, c = input().split()
        vars_list.append((r, c))

    funcs_list = [input() for _ in range(M, N)]

    for i in range(M):
        r, c = map(int, vars_list[i])
        var = [
            list(map(int, input().split()))
            for _ in range(r)
        ]
        matrix.append(np.array(var, dtype=np.float64))

    for i in range(M, N):
        result = None
        func, *args = funcs_list[i - M].split()

        if func == 'tnh':
            x, = args
            result = Functions.tnh(matrix[int(x) - 1])
        elif func == 'rlu':
            inv_alpha, x = args
            result = Functions.rlu(matrix[int(x) - 1], int(inv_alpha))
        elif func == 'mul':
            a, b = args
            result = Functions.mul(matrix[int(a) - 1], matrix[int(b) - 1])
        elif func == 'sum':
            l, *us = args
            result = Functions.sum([matrix[int(u) - 1] for u in us])
        elif func == 'had':
            l, *us = args
            result = Functions.had([matrix[int(u) - 1] for u in us])

        matrix.append(result)

    for i in range(N):
        d_matrix.append(np.zeros_like(matrix[i]))

    for i in range(N - K, N):
        d = [
            list(map(int, input().split()))
            for _ in range(matrix[i].shape[0])
        ]
        d_matrix[i] = np.array(d)

    for i in reversed(range(M, N)):
        func, *args = funcs_list[i - M].split()

        if func == 'tnh':
            x, = args
            d_matrix[int(x) - 1] += Functions.d_tnh(matrix[int(x) - 1], d_matrix[i])
        elif func == 'rlu':
            inv_alpha, x = args
            d_matrix[int(x) - 1] += Functions.d_rlu(matrix[int(x) - 1], int(inv_alpha), d_matrix[i])
        elif func == 'mul':
            a, b = args
            d_a, d_b = Functions.d_mul(matrix[int(a) - 1], matrix[int(b) - 1], d_matrix[i])
            d_matrix[int(a) - 1] += d_a
            d_matrix[int(b) - 1] += d_b
        elif func == 'sum':
            l, *us = args
            us = [int(u) - 1 for u in us]
            d_us = Functions.d_sum(int(l), d_matrix[i])
            for index, d in enumerate(us):
                d_matrix[d] += d_us[index]
        elif func == 'had':
            l, *us = args
            us = [int(u) - 1 for u in us]
            d_us = Functions.d_had([matrix[u] for u in us], d_matrix[i])
            for index, d in enumerate(us):
                d_matrix[d] += d_us[index]

    for i in range(N - K, N):
        result = matrix[i]
        for x in result:
            print(*x)

    for i in range(M):
        result = d_matrix[i]
        for x in result:
            print(*x)


if __name__ == '__main__':
    main()
