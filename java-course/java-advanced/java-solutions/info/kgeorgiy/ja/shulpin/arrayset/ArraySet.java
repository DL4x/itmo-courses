package info.kgeorgiy.ja.shulpin.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final ArrayList<T> elements;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        this(List.of(), null, true);
    }

    public ArraySet(Comparator<? super T> comparator) {
        this(List.of(), comparator, true);
    }

    public ArraySet(Collection<? extends T> c) {
        this(c, null, false);
    }

    public ArraySet(Collection<? extends T> c, Comparator<? super T> comparator) {
        this(c, comparator, false);
    }

    private ArraySet(Collection<? extends T> c, Comparator<? super T> comparator, boolean isSorted) {
        if (isSorted) {
            this.elements = new ArrayList<>(c);
        } else {
            TreeSet<T> treeSet = new TreeSet<>(comparator);
            treeSet.addAll(c);
            this.elements = new ArrayList<>(treeSet);
        }
        this.comparator = comparator;
    }

    private int indexOf(T t) {
        return Collections.binarySearch(elements, t, comparator);
    }

    private int lowerBound(T t) {
        final int index = indexOf(t);
        return index < 0 ? Math.abs(index) - 1 : index;
    }

    @SuppressWarnings("unchecked")
    private Comparator<? super T> requireNonNullComparator() {
        return comparator == null ? (Comparator<? super T>) Comparator.naturalOrder() : comparator;
    }

    private SortedSet<T> subSetImpl(final int fromIndex, final int toIndex) {
        return new ArraySet<>(elements.subList(fromIndex, toIndex), comparator, true);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public T first() {
        return elements.getFirst();
    }

    @Override
    public T last() {
        return elements.getLast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return indexOf((T) Objects.requireNonNull(o)) >= 0;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (requireNonNullComparator().compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("From element can not be more than to element.");
        }
        return subSetImpl(lowerBound(fromElement), lowerBound(toElement));
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return subSetImpl(0, lowerBound(toElement));
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return subSetImpl(lowerBound(fromElement), size());
    }

    @Override
    public void clear() {
        elements.clear();
    }
}
