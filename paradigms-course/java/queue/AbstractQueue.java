package queue;

public abstract class AbstractQueue implements Queue {

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
    protected int size;

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
    @Override
    public void enqueue(final Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

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
    @Override
    public Object element() {
        assert size > 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

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
    @Override
    public Object dequeue() {
        assert size > 0;
        size--;
        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

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
    @Override
    public int size() {
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
    @Override
    public boolean isEmpty() {
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
    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    protected abstract void clearImpl();

    /*
     * Pred:
     * element != null
     * Post:
     * R = the number of i that satisfy the condition: forall i in [1, n]: queue[i] == element
     * &&
     * n' = n
     * &&
     * Immutable(n)
     */
    @Override
    public int count(final Object element) {
        assert element != null;
        int count = 0;
        return countImpl(element, count);
    }

    protected abstract int countImpl(Object element, int count);
}
