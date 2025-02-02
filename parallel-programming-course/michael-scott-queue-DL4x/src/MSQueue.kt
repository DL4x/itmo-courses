import java.util.concurrent.atomic.*

/**
 * @author Shulpin Egor
 */
class MSQueue<E> : Queue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = AtomicReference(dummy)
        tail = AtomicReference(dummy)
    }

    override fun enqueue(element: E) {
        while (true) {
            val node = Node(element)
            val currentTail = tail.get()
            if (currentTail.next.compareAndSet(null, node)) {
                tail.compareAndSet(currentTail, node)
                return
            } else {
                val currentTailNext = currentTail.next.get()
                tail.compareAndSet(currentTail, currentTailNext)
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val currentHead = head.get()
            val currentHeadNext = currentHead.next.get()
                ?: return null
            val currentHeadNextElement = currentHeadNext.element
            if (head.compareAndSet(currentHead, currentHeadNext)) {
                head.get().element = null
                return currentHeadNextElement
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
