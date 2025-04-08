package util;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Список, содержащий слабые ссылки на свои элементы
 *
 * @param <T> Элемент
 */
public class WeakArrayList<T> extends AbstractList<T> {
    private final List<WeakReference<T>> items;
    private boolean needsCleanup = false;

    public WeakArrayList() {
        items = new ArrayList<>();
    }

    public WeakArrayList(List<T> otherList) {
        items = new ArrayList<>(otherList.size());
        for (T t : otherList) {
            items.add(new WeakReference<>(t));
        }
    }

    @Override
    public T get(int index) {
        cleanUp();
        return items.get(index).get();
    }

    @Override
    public int size() {
        cleanUp();
        return items.size();
    }

    @Override
    public boolean add(T t) {
        needsCleanup = true;
        return items.add(new WeakReference<>(t));
    }

    /**
     * Очищает items от null элементов
     */
    private void cleanUp() {
        if (needsCleanup) {
            items.removeIf(ref -> ref.get() == null);
            needsCleanup = false;
        }
    }
}
