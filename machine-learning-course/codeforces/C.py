import numpy as np
from numpy.linalg import lstsq as least_squares


def main():
    n, m = map(int, input().split())
    k = n - m + 1

    matrix = []
    for i in range(n):
        row = map(int, input().split())
        matrix.append(list(row))
    matrix = np.array(matrix)

    result = []
    for i in range(m):
        row = map(int, input().split())
        result.append(list(row))
    result = np.array(result)

    system = []
    for i in range(m):
        for j in range(m):
            window = matrix[i:i + k, j:j + k]
            system.append(window.flatten())
    system = np.array(system)

    solution = least_squares(
        system,
        result.flatten(),
        rcond=None,
    )

    kernel = np.array(solution[0])
    for i in range(k):
        print(*kernel[i * k:(i + 1) * k])


if __name__ == '__main__':
    main()
