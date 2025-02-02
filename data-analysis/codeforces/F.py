import numpy as np


def eval_weights(CM):
    s = np.sum(CM)
    return np.array([np.sum(CM[i]) / s for i in range(CM.shape[0])])


def eval_TP(CM):
    return np.array([CM[i][i] for i in range(CM.shape[0])])


def eval_FP(CM):
    return np.array([
        np.sum([
            CM[j][i] for j in range(CM.shape[0]) if i != j
        ]) for i in range(CM.shape[0])
    ])


def eval_FN(CM):
    return np.array([
        np.sum([
            CM[i][j] for j in range(CM.shape[0]) if i != j
        ]) for i in range(CM.shape[0])
    ])


def precision_impl(tp, fp):
    return 0 if tp + fp == 0 else tp / (fp + tp)


def recall_impl(tp, fn):
    return 0 if tp + fn == 0 else tp / (fn + tp)


def f1_formula(precision, recall):
    return 0 if precision + recall == 0 \
        else (2 * precision * recall) / (precision + recall)


def micro_f1_score(_, TP, FP, FN, weights):
    TP_sum = np.sum([TP[i] * weights[i] for i in range(TP.shape[0])])
    FP_sum = np.sum([FP[i] * weights[i] for i in range(FP.shape[0])])
    FN_sum = np.sum([FN[i] * weights[i] for i in range(FN.shape[0])])

    precision = precision_impl(TP_sum, FP_sum)
    recall = recall_impl(TP_sum, FN_sum)

    return f1_formula(precision, recall)


def macro_f1_score(CM, TP, FP, FN, weights):
    precision = 0
    recall = 0

    for i in range(CM.shape[0]):
        precision += precision_impl(TP[i], FP[i]) * weights[i]
        recall += recall_impl(TP[i], FN[i]) * weights[i]

    return f1_formula(precision, recall)


def f1_score(CM, TP, FP, FN, weights):
    def f1_score_impl(i):
        precision = precision_impl(TP[i], FP[i])
        recall = recall_impl(TP[i], FN[i])
        measure = f1_formula(precision, recall)
        return measure * weights[i]

    return np.sum([f1_score_impl(i) for i in range(CM.shape[0])])


def main():
    K = int(input())

    CM = np.array([[int(x) for x in input().split()] for _ in range(K)])

    TP = eval_TP(CM)
    FP = eval_FP(CM)
    FN = eval_FN(CM)

    weights = eval_weights(CM)

    print(weights)

    print(micro_f1_score(CM, TP, FP, FN, weights))

    print(macro_f1_score(CM, TP, FP, FN, weights))

    print(f1_score(CM, TP, FP, FN, weights))


if __name__ == '__main__':
    main()
