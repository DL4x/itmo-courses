import numpy as np


def main():
    K = int(input())
    N = int(input())

    X = [[] for _ in range(K)]

    for _ in range(N):
        desc = input().split()
        x, y = map(int, desc)
        X[x - 1].append(y)

    var = 0
    for xi in X:
        if not xi:
            continue
        p = len(xi) / N
        var += np.var(xi) * p

    print(var)


if __name__ == '__main__':
    main()
