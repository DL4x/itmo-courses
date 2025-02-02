import numpy as np


def main():
    N = int(input())

    X1 = []
    X2 = []
    for _ in range(N):
        desc = input().split()
        x1, x2 = map(int, desc)
        X1.append(x1)
        X2.append(x2)

    rank = lambda X: dict(zip(sorted(X), range(len(X))))
    rank_X1 = rank(X1)
    rank_X2 = rank(X2)

    D = [rank_X1[x1] - rank_X2[x2] for x1, x2 in zip(X1, X2)]

    s = sum([d ** 2 for d in D])

    print(1 - 6 * s / (N * (N ** 2 - 1)))


if __name__ == '__main__':
    main()
