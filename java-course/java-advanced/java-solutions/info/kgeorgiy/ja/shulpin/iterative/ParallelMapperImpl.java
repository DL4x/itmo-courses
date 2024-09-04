package info.kgeorgiy.ja.shulpin.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threadsList;
    private final Queue<Runnable> runnablesQueue;

    public ParallelMapperImpl(int threads) {
        threadsList = new ArrayList<>();
        runnablesQueue = new ArrayDeque<>();

        for (int i = 0; i < threads; i++) {
            threadsList.add(new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        final Runnable runnable;
                        synchronized (runnablesQueue) {
                            while (runnablesQueue.isEmpty()) {
                                runnablesQueue.wait();
                            }
                            runnable = runnablesQueue.poll();
                        }
                        try {
                            runnable.run();
                        } catch (final RuntimeException ignored) {
                        }
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));
        }

        threadsList.forEach(Thread::start);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f,
                              List<? extends T> items) throws InterruptedException {
        List<R> result = new ArrayList<>();
        IntStream.range(0, items.size())
                .forEach(ignored -> result.add(null));

        AtomicCounter counter = new AtomicCounter(items.size());

        for (int i = 0; i < items.size(); i++) {
            final int threadPosition = i;

            synchronized (runnablesQueue) {
                runnablesQueue.offer(() -> {
                    result.set(threadPosition,
                            f.apply(items.get(threadPosition)));

                    synchronized (counter) {
                        if (counter.decrement() == 0) {
                            counter.notifyAll();
                        }
                    }
                });
                runnablesQueue.notifyAll();
            }
        }

        counter.waitUntil(0);

        return result;
    }

    @Override
    public void close() {
        threadsList.forEach(Thread::interrupt);
        for (final Thread thread : threadsList) {
            try {
                thread.join();
            } catch (final InterruptedException ignored) {
            }
        }
    }
}
