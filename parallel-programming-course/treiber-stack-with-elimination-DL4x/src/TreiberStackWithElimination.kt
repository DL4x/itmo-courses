import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Shulpin Egor
 */
open class TreiberStackWithElimination<E> : Stack<E> {
    private val stack = TreiberStack<E>()

    private val eliminationArray = AtomicReferenceArray<Any?>(ELIMINATION_ARRAY_SIZE)

    override fun push(element: E) {
        if (tryPushElimination(element)) return
        stack.push(element)
    }

    protected open fun tryPushElimination(element: E): Boolean {
        val randomIndex = randomCellIndex()
        if (!eliminationArray.compareAndSet(randomIndex, CELL_STATE_EMPTY, element)) {
            return false
        }
        repeat(ELIMINATION_WAIT_CYCLES) {}
        return eliminationArray.getAndSet(randomIndex, CELL_STATE_EMPTY) == CELL_STATE_RETRIEVED
    }

    override fun pop(): E? = tryPopElimination() ?: stack.pop()

    @Suppress("UNCHECKED_CAST")
    private fun tryPopElimination(): E? {
        val randomIndex = randomCellIndex()
        val randomElement = eliminationArray.get(randomIndex)
        if (randomElement == CELL_STATE_EMPTY) {
            return null
        }
        if (randomElement == CELL_STATE_RETRIEVED) {
            return null
        }
        if (eliminationArray.compareAndSet(randomIndex, randomElement, CELL_STATE_RETRIEVED)) {
            return randomElement as E?
        }
        return null
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(eliminationArray.length())

    companion object {
        private const val ELIMINATION_ARRAY_SIZE = 2 // Do not change!
        private const val ELIMINATION_WAIT_CYCLES = 1 // Do not change!

        // Initially, all cells are in EMPTY state.
        private val CELL_STATE_EMPTY = null

        // `tryPopElimination()` moves the cell state
        // to `RETRIEVED` if the cell contains element.
        private val CELL_STATE_RETRIEVED = Any()
    }
}
