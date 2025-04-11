package util;

import java.util.*;

/**
 * Список логов не превышающий заданной длины
 *
 * @param <T> Логи
 */
public class LogsList<T> extends AbstractList<T> {
    private final Deque<T> deque;
    private final int length;

    public LogsList(int length) {
        this.length = length;
        deque = new ArrayDeque<>();
    }

    @Override
    public synchronized int size() {
        return deque.size();
    }

    @Override
    public synchronized boolean add(T t) {
        if (deque.size() >= length) {
            deque.pollFirst();
        }
        return deque.add(t);
    }

    @Override
    public synchronized T get(int index) {
        return deque.stream().toList().get(index);
    }

    @Override
    public synchronized List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex +
                    ", toIndex: " + toIndex);
        }
        return deque.stream().toList().subList(fromIndex, toIndex);
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return new ArrayList<>(deque).iterator();
    }
}
