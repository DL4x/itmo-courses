import numpy as np


def main():
    N, K = map(int, input().split())

    C = []
    line = input().split()
    counts_left = np.zeros(K)
    counts_right = np.zeros(K)

    for x in line:
        c = int(x)
        C.append(c)
        counts_right[c - 1] += 1

    s_left = 0
    s_right = np.sum(counts_right ** 2)
    for i in range(1, N):
        c = C[i - 1] - 1
        s_left_new = s_left - counts_left[c] ** 2
        s_right_new = s_right - counts_right[c] ** 2

        counts_left[c] += 1
        counts_right[c] -= 1

        s_left = s_left_new + counts_left[c] ** 2
        s_right = s_right_new + counts_right[c] ** 2

        print(i / N * (1 - s_left / i ** 2) + \
              (N - i) / N * (1 - s_right / (N - i) ** 2))


if __name__ == '__main__':
    main()
