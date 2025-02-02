package queue;

public class LinkedQueue extends AbstractQueue {

    /*
     * Model:
     * n - queue size
     * &&
     * forall i in [1, n]: queue[i] != null
     * &&
     * tail - index of the last element in queue (queue[tail] - last out element)
     * &&
     * head - index of the first element in queue (queue[head] - first out element)
     * Immutable(n):
     * forall i in [1, n]: queue'[i] == queue[i]
     */
    private Node tail;
    private Node head;

    private static class Node {
        private final Object element;
        private Node next;

        public Node(Object element, Node next) {
            this.element = element;
            this.next = next;
        }
    }

    @Override
    protected void enqueueImpl(final Object element) {
        Node prevTail = tail;
        tail = new Node(element, null);
        if (size == 0) {
            head = tail;
        } else {
            prevTail.next = tail;
        }
    }

    @Override
    protected Object elementImpl() {
        return head.element;
    }

    @Override
    protected Object dequeueImpl() {
        final Object result = head.element;
        head = head.next;
        return result;
    }

    @Override
    protected void clearImpl() {
        tail = null;
        head = null;
    }

    @Override
    protected int countImpl(final Object element, int count) {
        Node queueElement = head;
        for (int i = 0; i < size; i++) {
            if (queueElement.element.equals(element)) {
                count++;
            }
            queueElement = queueElement.next;
        }
        return count;
    }
}
