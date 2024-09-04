package info.kgeorgiy.ja.shulpin.iterative;

public class AtomicCounter {
    private static volatile int count;

    public AtomicCounter() {}

    public AtomicCounter(final int initial) {
        count = initial;
    }

    public synchronized int value() {
        return count;
    }

    public synchronized int increment() {
        return ++count;
    }

    public synchronized int decrement() {
        return --count;
    }

    public synchronized void waitUntil(final int value) throws InterruptedException {
        while (count != value) {
            wait();
        }
    }
}
