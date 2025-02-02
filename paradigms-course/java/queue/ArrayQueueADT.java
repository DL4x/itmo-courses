package queue;

import java.util.Arrays;

public class ArrayQueueADT {

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
    private int tail;
    private int head;
    private int size;
    private Object[] elements = new Object[2];

    /*
     * Pred:
     * element != null
     * Post:
     * n' = n + 1
     * &&
     * tail' = tail + 1
     * &&
     * queue'[tail'] = element
     * &&
     * Immutable(n)
     */
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        assert element != null;
        ensureCapacity(queue, queue.size);
        queue.elements[queue.tail] = element;
        queue.size++;
        queue.tail = (queue.tail + 1) % queue.elements.length;
    }

    private static void ensureCapacity(final ArrayQueueADT queue, int size) {
        if (queue.elements.length == size) {
            Object[] newElements = new Object[2 * queue.elements.length];
            for (int i = 0; i < queue.elements.length; i++) {
                newElements[i] = queue.elements[(queue.head + i) % queue.elements.length];
            }
            queue.tail = queue.elements.length;
            queue.head = 0;
            queue.elements = newElements;
        }
    }

    /*
     * Pred:
     * n > 0
     * Post:
     * R = queue[head]
     * &&
     * n' = n
     * &&
     * Immutable(n)
     */
    public static Object element(final ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[queue.head];
    }

    /*
     * Pred:
     * n > 0
     * Post:
     * R = queue[head]
     * &&
     * n' = n - 1
     * &&
     * head' = head + 1
     * &&
     * Immutable(n')
     */
    public static Object dequeue(final ArrayQueueADT queue) {
        assert queue.size > 0;
        final Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.size--;
        queue.head = (queue.head + 1) % queue.elements.length;
        return result;
    }

    /*
     * Pred:
     * true
     * Post:
     * R = n
     * &&
     * n' = n
     * &&
     * Immutable(n)
     */
    public static int size(final ArrayQueueADT queue) {
        return queue.size;
    }

    /*
     * Pred:
     * true
     * Post:
     * R = n == 0
     * &&
     * n' = n
     * &&
     * Immutable(n)
     */
    public static boolean isEmpty(final ArrayQueueADT queue) {
        return queue.size == 0;
    }

    /*
     * Pred:
     * true
     * Post:
     * n' = 0
     * &&
     * tail' = 0
     * &&
     * head' = 0
     */
    public static void clear(final ArrayQueueADT queue) {
        queue.tail = 0;
        queue.head = 0;
        queue.size = 0;
        Arrays.fill(queue.elements, null);
    }

    /*
     * Pred:
     * true
     * Post:
     * R = queue representation as a string of the form: '[' queue[head] ', ' ... ', ' queue[tail] ']'
     * &&
     * n' == n
     * &&
     * Immutable(n)
     */
    public static String toStr(final ArrayQueueADT queue) {
        StringBuilder queueToString = new StringBuilder();
        queueToString.append('[');
        for (int i = 0; i < queue.size; i++) {
            queueToString.append(queue.elements[(queue.head + i) % queue.elements.length]);
            if (i != queue.size - 1) {
                queueToString.append(", ");
            }
        }
        queueToString.append(']');
        return queueToString.toString();
    }
}
