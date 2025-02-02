import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Shulpin Egor
 */
class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    @Suppress("UNCHECKED_CAST")
    private fun traverseCombinerTasks() {
        for (i in 0 until tasksForCombiner.length()) {
            when (val task = tasksForCombiner.get(i)) {
                null, is Result<*> -> continue
                is Dequeue -> {
                    val result = queue.removeFirstOrNull()
                    tasksForCombiner.set(i, Result(result))
                }

                else -> {
                    queue.addLast(task as E)
                    tasksForCombiner.set(i, Result(task))
                }
            }
        }
        combinerLock.set(false)
    }

    override fun enqueue(element: E) {
        var threadWait = false
        var threadLocalIndex = 0
        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                if (!threadWait) {
                    queue.addLast(element)
                }
                traverseCombinerTasks()
                if (threadWait) {
                    tasksForCombiner.set(threadLocalIndex, null)
                }
                return
            }
            if (threadWait) {
                val threadTask = tasksForCombiner.get(threadLocalIndex)
                if (threadTask is Result<*>) {
                    return tasksForCombiner.set(threadLocalIndex, null)
                }
                continue
            }
            threadLocalIndex = randomCellIndex()
            threadWait = tasksForCombiner.compareAndSet(threadLocalIndex, null, element)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        var threadWait = false
        var threadLocalIndex = 0
        var threadLocalResult: E? = null
        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                if (!threadWait) {
                    threadLocalResult = queue.removeFirstOrNull()
                }
                traverseCombinerTasks()
                if (threadWait) {
                    val threadTask = tasksForCombiner.get(threadLocalIndex)
                    if (threadTask is Result<*>) {
                        tasksForCombiner.set(threadLocalIndex, null)
                        return threadTask.value as E?
                    }
                }
                return threadLocalResult
            }
            if (threadWait) {
                val threadTask = tasksForCombiner.get(threadLocalIndex)
                if (threadTask is Result<*>) {
                    tasksForCombiner.set(threadLocalIndex, null)
                    return threadTask.value as E?
                }
                continue
            }
            threadLocalIndex = randomCellIndex()
            threadWait = tasksForCombiner.compareAndSet(threadLocalIndex, null, Dequeue)
        }
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

private object Dequeue

private class Result<V>(val value: V)
