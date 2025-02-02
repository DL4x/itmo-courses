package queue;

public interface Queue {
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
    void enqueue(Object element);

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
    Object element();

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
    Object dequeue();

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
    int size();

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
    boolean isEmpty();

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
    void clear();

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
    int count(Object element);
}
