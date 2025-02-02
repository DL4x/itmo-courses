package queue;

import java.util.Arrays;

public class ArrayQueueModule {

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
    private static int tail;
    private static int head;
    private static int size;
    private static Object[] elements = new Object[2];

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
    public static void enqueue(final Object element) {
        assert element != null;
        ensureCapacity(size);
        elements[tail] = element;
        size++;
        tail = (tail + 1) % elements.length;
    }

    private static void ensureCapacity(int size) {
        if (elements.length == size) {
            Object[] newElements = new Object[2 * elements.length];
            for (int i = 0; i < elements.length; i++) {
                newElements[i] = elements[(head + i) % elements.length];
            }
            tail = elements.length;
            head = 0;
            elements = newElements;
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
    public static Object element() {
        assert size > 0;
        return elements[head];
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
    public static Object dequeue() {
        assert size > 0;
        final Object result = elements[head];
        elements[head] = null;
        size--;
        head = (head + 1) % elements.length;
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
    public static int size() {
        return size;
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
    public static boolean isEmpty() {
        return size == 0;
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
    public static void clear() {
        tail = 0;
        head = 0;
        size = 0;
        Arrays.fill(elements, null);
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
    public static String toStr() {
        StringBuilder queueToString = new StringBuilder();
        queueToString.append('[');
        for (int i = 0; i < size; i++) {
            queueToString.append(elements[(head + i) % elements.length]);
            if (i != size - 1) {
                queueToString.append(", ");
            }
        }
        queueToString.append(']');
        return queueToString.toString();
    }
}
