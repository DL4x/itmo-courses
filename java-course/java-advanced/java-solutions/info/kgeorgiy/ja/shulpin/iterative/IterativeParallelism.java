package info.kgeorgiy.ja.shulpin.iterative;

import java.util.*;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.function.Predicate;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

public class IterativeParallelism implements NewScalarIP {
    private ParallelMapper parallelMapper;

    public IterativeParallelism() {}

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    private void threadJoining(List<Thread> threads) throws InterruptedException {
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                threads.forEach(Thread::interrupt);
                throw e;
            }
        }
    }

    private static int shift(int step, int tempIndex) {
        if (tempIndex % step != 0) {
            return step - tempIndex % step;
        }
        return 0;
    }

    private <T, S, R> R mapperParallelImpl(
            int threads, List<? extends T> values,
            Function<List<? extends T>, S> threadFunction,
            Function<List<? extends S>, R> resultFunction, int step
    ) throws InterruptedException {
        List<? extends T> stepValues = IntStream
                .iterate(0, i -> i < values.size(), i -> i + step)
                .mapToObj(values::get).toList();
        List<List<? extends T>> subLists = new ArrayList<>();

        int tempIndex = 0;
        int rest = stepValues.size() % threads;
        final int subListSize = stepValues.size() / threads;
        for (int i = 0; i < threads; i++, rest--) {
            final int fromIndex = tempIndex;
            tempIndex = tempIndex + subListSize + (rest <= 0 ? 0 : 1);
            final int toIndex = tempIndex;

            subLists.add(stepValues.subList(fromIndex, toIndex));
        }

        return resultFunction.apply(parallelMapper.map(threadFunction, subLists));
    }

    private <T, S, R> R parallelImpl(
            int threads, List<? extends T> values,
            Function<List<? extends T>, S> threadFunction,
            Function<List<? extends S>, R> resultFunction, int step
    ) throws InterruptedException {
        threads = Math.min(threads, (values.size() + step - 1) / step);

        if (parallelMapper != null) {
            return mapperParallelImpl(threads, values,
                    threadFunction, resultFunction, step);
        }

        List<S> threadsResult = new ArrayList<>();
        List<Thread> threadList = new ArrayList<>();

        IntStream.range(0, threads)
                .forEach(ignored -> threadsResult.add(null));

        int tempIndex = 0;
        int rest = values.size() % threads;
        final int subListSize = values.size() / threads;
        for (int i = 0; i < threads; i++, rest--) {
            final int threadIndex = i;
            final int fromIndex = tempIndex + shift(step, tempIndex);
            tempIndex = tempIndex + subListSize + (rest <= 0 ? 0 : 1);
            final int toIndex = tempIndex;

            Thread thread = new Thread(() ->
                    threadsResult.set(threadIndex, threadFunction
                            .apply(IntStream
                                .iterate(fromIndex,
                                        j -> j < toIndex,
                                        j -> j + step)
                                .mapToObj(values::get)
                                .toList())
                    )
            );

            thread.start();
            threadList.add(thread);
        }

        threadJoining(threadList);

        return resultFunction.apply(threadsResult);
    }

    @Override
    public <T> T maximum(
            int threads, List<? extends T> values,
            Comparator<? super T> comparator, int step
    ) throws InterruptedException {
        Function<List<? extends T>, T> function =
                list -> list.stream().max(comparator)
                        .orElseThrow(NoSuchElementException::new);
        return parallelImpl(threads, values,
                function,
                function,
                step
        );
    }

    @Override
    public <T> T minimum(
            int threads, List<? extends T> values,
            Comparator<? super T> comparator, int step
    ) throws InterruptedException {
        return maximum(threads, values, comparator.reversed(), step);
    }

    @Override
    public <T> boolean all(
            int threads, List<? extends T> values,
            Predicate<? super T> predicate, int step
    ) throws InterruptedException {
        return parallelImpl(threads, values,
                list -> list.stream().allMatch(predicate),
                list -> list.stream().allMatch(i -> i),
                step
        );
    }

    @Override
    public <T> boolean any(
            int threads, List<? extends T> values,
            Predicate<? super T> predicate, int step
    ) throws InterruptedException {
        return !all(threads, values, predicate.negate(), step);
    }

    @Override
    public <T> int count(
            int threads, List<? extends T> values,
            Predicate<? super T> predicate, int step
    ) throws InterruptedException {
        return parallelImpl(threads, values,
                list -> list.stream().filter(predicate).count(),
                list -> list.stream().mapToInt(Math::toIntExact).sum(),
                step
        );
    }
}
