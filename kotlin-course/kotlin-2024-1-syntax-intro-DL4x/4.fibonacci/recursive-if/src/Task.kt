fun fibonacciIf(n: Int): Int {
    if (n <= 1) {
        return n
    }
    return fibonacciIf(n - 1) + fibonacciIf(n - 2)
}
