class IntMatrix(val rows: Int, val columns: Int) {
    init {
        require(rows >= 0 && columns >= 0) {
            "Matrix must have non-negative number of rows and columns"
        }
    }

    private val values = IntArray(rows * columns)

    private fun getIndex(i: Int, j: Int): Int {
        require(i in 0..<rows && j in 0..<columns) {
            "Indexes must be within bounds of matrix size"
        }
        return i * columns + j
    }

    operator fun get(i: Int, j: Int): Int {
        return values[getIndex(i, j)]
    }

    operator fun set(i: Int, j: Int, value: Int) {
        values[getIndex(i, j)] = value
    }
}

fun main() {
    val matrix = IntMatrix(3, 4)
    println(matrix.rows)
    println(matrix.columns)
    println(matrix[0, 0])
    matrix[2, 3] = 42
    println(matrix[2, 3])
}
