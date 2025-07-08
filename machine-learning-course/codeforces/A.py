from itertools import product

import numpy as np

DN = 1
LIMIT = 512


def main():
    M = int(input())
    y = [int(input()) for _ in range(2 ** M)]

    result = []
    product_args = [-1, 1]

    limit = LIMIT <= np.sum(y)

    if limit:
        product_args = [1, -1]
        y = np.logical_not(y).astype(int)

    for i, xs in enumerate(product(product_args, repeat=M)):
        if not y[i]:
            continue

        xs = list(reversed(xs))
        b = -xs.count(1) + 0.5
        xs.append(-b if limit else b)

        result.append(np.array(xs))

    layer_len = len(result)

    print(2 if layer_len else 1)
    print(f'{layer_len} {DN}' if layer_len else DN)
    for ws in result:
        print(*ws)
    last_layer = np.ones(layer_len if layer_len else M)
    print(*last_layer, -last_layer.sum() + 0.5 if limit else -0.5)


if __name__ == '__main__':
    main()
