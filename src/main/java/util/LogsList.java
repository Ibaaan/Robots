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
        deque = new ArrayDeque<>(length);
    }

    @Override
    public int size() {
        synchronized (deque) {
            return deque.size();
        }
    }

    @Override
    public boolean add(T t) {
        synchronized (deque) {
            while (deque.size() >= length) {
                deque.pollFirst();
            }
            return deque.add(t);
        }
    }

    @Override
    public T get(int index) {
        synchronized (deque) {
            return deque.stream().toList().get(index);
        }
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        synchronized (deque) {
            return deque.stream().toList().subList(fromIndex, toIndex);
        }
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> syncIterator;
        synchronized (deque) {
            syncIterator = deque.iterator();
        }
        return syncIterator;
    }
}
