import java.util.concurrent.atomic.*

/**
 * @author Shulpin Egor
 */
class FAABasedQueue<E> : Queue<E> {
    private val infiniteArray = InfiniteArray()
    private val enqIdx = AtomicLong(0)
    private val deqIdx = AtomicLong(0)

    override fun enqueue(element: E) {
        while (true) {
            val curTail = infiniteArray.tail.get()
            val i = enqIdx.getAndIncrement()
            val s = infiniteArray.findSegment(
                start = curTail,
                id = i / SEGMENT_SIZE,
            )
            infiniteArray.moveTailForward(s)
            if (s.cells.compareAndSet(i.toInt() % SEGMENT_SIZE, null, element)) return
        }
    }

    private fun shouldTryToDequeue(): Boolean {
        while (true) {
            val curEnqIdx = enqIdx.get()
            val curDeqIdx = deqIdx.get()
            if (curEnqIdx != enqIdx.get()) continue
            return curDeqIdx < curEnqIdx
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            if (!shouldTryToDequeue()) return null
            val curHead = infiniteArray.head.get()
            val i = deqIdx.getAndIncrement()
            val s = infiniteArray.findSegment(
                start = curHead,
                id = i / SEGMENT_SIZE,
            )
            infiniteArray.moveHeadForward(s)
            if (s.cells.compareAndSet(i.toInt() % SEGMENT_SIZE, null, POISONED)) continue
            return s.cells.getAndSet(i.toInt() % SEGMENT_SIZE, null) as E
        }
    }
}

private val POISONED = Any()

private class Segment(val id: Long) {
    val next = AtomicReference<Segment?>(null)
    val cells = AtomicReferenceArray<Any?>(SEGMENT_SIZE)
}

// DO NOT CHANGE THIS CONSTANT
private const val SEGMENT_SIZE = 2

private class InfiniteArray {
    val head = AtomicReference<Segment>()
    val tail = AtomicReference<Segment>()

    init {
        val start = Segment(0)
        head.set(start)
        tail.set(start)
    }

    fun findSegment(start: Segment, id: Long): Segment {
        var curSegment = start
        while (curSegment.id != id) {
            val nextNode = curSegment.next
            val nextSegment = Segment(curSegment.id + 1)
            if (nextNode.compareAndSet(null, nextSegment)) {
                curSegment = nextSegment
                continue
            }
            val otherNextSegment = nextNode.get()
            if (otherNextSegment != null) curSegment = otherNextSegment
        }
        return curSegment
    }

    private fun moveForward(node: AtomicReference<Segment>, s: Segment) {
        val nodeSegment = node.get()
        if (s.id > nodeSegment.id) node.compareAndSet(nodeSegment, s)
    }

    fun moveHeadForward(s: Segment) = moveForward(head, s)

    fun moveTailForward(s: Segment) = moveForward(tail, s)
}
