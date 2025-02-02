import java.util.concurrent.atomic.AtomicReference

/**
 * @author Shulpin Egor
 */
class TreiberStack<E> : Stack<E> {
    // Initially, the stack is empty.
    private val top = AtomicReference<Node<E>?>(null)

    override fun push(element: E) {
        while (true) {
            val currentTop = top.get()
            val newTop = Node(element, currentTop)
            if (top.compareAndSet(currentTop, newTop)) {
                return
            }
        }
    }

    override fun pop(): E? {
        while (true) {
            val currentTop = top.get()
                ?: return null
            val newTop = currentTop.next
            if (top.compareAndSet(currentTop, newTop)) {
                return currentTop.element
            }
        }
    }

    private class Node<E>(
        val element: E,
        val next: Node<E>?
    )
}
