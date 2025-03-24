package windowfilter;

import state.SaveLoadState;

import java.util.List;

/**
 * Фильтрует окна
 */
public interface FilterWindow {
    /**
     * Фильтрует окна
     */
    List<SaveLoadState> filter(List<SaveLoadState> windows);
}
