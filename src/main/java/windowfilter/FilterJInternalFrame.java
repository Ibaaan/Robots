package windowfilter;

import state.SaveLoadState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Фильтрует окна наследующие JInternalFrame
 */
public class FilterJInternalFrame implements FilterWindow {
    @Override
    public List<SaveLoadState> filter(List<SaveLoadState> windows) {
        List<SaveLoadState> result = new ArrayList<>();
        for (SaveLoadState window : windows) {
            if (window instanceof JInternalFrame) {
                result.add(window);
            }
        }
        return result;
    }
}
