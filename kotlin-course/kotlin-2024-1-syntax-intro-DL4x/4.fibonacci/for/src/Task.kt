fun fibonacciFor(n: Int): Int {
    var f1 = 0
    var f2 = 1
    for (i in 1..n) {
        val next = f1 + f2
        f1 = f2
        f2 = next
    }
    return f1
}
