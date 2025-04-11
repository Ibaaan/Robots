package util;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

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
        deque = new LinkedBlockingDeque<>(length);
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public boolean add(T t) {
        if (deque.size() >= length) {
            deque.pollFirst();
        }
        return deque.add(t);
    }

    @Override
    public T get(int index) {
        return deque.stream().toList().get(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        List<T> subList = new ArrayList<>();
        Iterator<T> iterator = deque.iterator();
        for (int i = 0; i < toIndex; i++) {
            if (iterator.hasNext()) {
                T element = iterator.next();
                if (i >= fromIndex) {
                    subList.add(element);
                }
            }
        }
        return subList;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayList<>(deque).iterator();
    }
}
