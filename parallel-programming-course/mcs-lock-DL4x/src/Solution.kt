import java.util.concurrent.atomic.*

/**
 * @author Shulpin Egor
 */
class Solution(private val env: Environment) : Lock<Solution.Node> {
    private val tail = AtomicReference<Node>()

    override fun lock(): Node {
        val my = Node()
        my.locked.set(true)
        val pred = tail.getAndSet(my)
        if (pred != null) {
            pred.next.set(my)
            while (my.locked.get()) env.park()
        }
        return my
    }

    override fun unlock(node: Node) {
        val next = node.next
        if (next.get() == null) {
            if (tail.compareAndSet(node, null)) return
            @Suppress("ControlFlowWithEmptyBody")
            while (next.get() == null) {}
        }
        val nextValue = next.get()
        nextValue.locked.set(false)
        env.unpark(nextValue.thread)
    }

    class Node {
        val locked = AtomicReference(false)
        val next = AtomicReference<Node>(null)
        val thread: Thread = Thread.currentThread()
    }
}
