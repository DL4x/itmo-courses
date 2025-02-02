package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {

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
    private Object[] elements = new Object[2];

    @Override
    protected void enqueueImpl(final Object element) {
        ensureCapacity(size);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    private void ensureCapacity(int size) {
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


    @Override
    protected Object elementImpl() {
        return elements[head];
    }

    @Override
    protected Object dequeueImpl() {
        final Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        return result;
    }

    @Override
    protected void clearImpl() {
        tail = 0;
        head = 0;
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
    public String toStr() {
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

    @Override
    protected int countImpl(final Object element, int count) {
        for (int i = 0; i < size; i++) {
            if (elements[(head + i) % elements.length].equals(element)) {
                count++;
            }
        }
        return count;
    }
}
