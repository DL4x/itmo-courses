import numpy as np


def dist_impl(X):
    d = 0
    count = 0
    prefix = 0
    for x in X:
        d += x * count - prefix
        count += 1
        prefix += x
    return 2 * d


def inner_dist(D):
    d = 0
    for X in D.values():
        X.sort()
        d += dist_impl(X)
    return d


def total_dist(X):
    return dist_impl(X)


def main():
    K = int(input())
    N = int(input())

    X = []
    D = dict()
    for _ in range(N):
        desc = input().split()
        x, y = map(int, desc)
        X.append(x)
        if D.get(y) is None:
            D[y] = []
        D[y].append(x)

    X.sort()

    inner = inner_dist(D)
    total = total_dist(X)

    print(inner)
    print(total - inner)


if __name__ == '__main__':
    main()
