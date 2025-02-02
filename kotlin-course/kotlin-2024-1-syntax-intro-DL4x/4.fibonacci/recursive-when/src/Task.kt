fun fibonacciWhen(n: Int): Int {
    return when (n) {
        0, 1 -> n
        else -> fibonacciWhen(n - 1) + fibonacciWhen(n - 2)
    }
}
