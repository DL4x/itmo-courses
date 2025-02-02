import numpy as np


def main():
    Kx, Ky = map(int, input().split())
    N = int(input())

    D = dict()
    for _ in range(N):
        desc = input().split()
        x, y = map(int, desc)
        if D.get(x) is None:
            D[x] = dict()
        if D[x].get(y) is None:
            D[x][y] = 0
        D[x][y] += 1

    h = 0
    for x, yx in D.items():
        yx_freq = yx.values()
        px = sum(yx_freq) / N
        for y in yx_freq:
            p = y / N
            h -= p * np.log(p / px)

    print(h)


if __name__ == '__main__':
    main()
