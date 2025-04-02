package state;

import java.util.List;
import java.util.Map;

/**
 * Восстанавливает свойства окон, формирует словарь для сохранения в файл
 */
public class WindowStateManager {
    private final FileStateManager fileStateManager;

    public WindowStateManager() {
        this.fileStateManager = new FileStateManager();
    }

    /**
     * Восстанавливает свойства окон
     */
    public void recoverWindows(List<SaveLoadState> windows) {
        WindowPropertyMap windowsProperties = new WindowPropertyMap(fileStateManager.getAllProperties());

        for (SaveLoadState window : windows) {
            Map<String, Integer> props =
                    windowsProperties.filterByPrefix(window.getFName());

            if (!props.isEmpty()) {
                window.loadState(props);
            }
        }
    }

    /**
     * Сохраняет свойства окон
     */
    public void saveWindows(List<SaveLoadState> windows) {
        WindowPropertyMap windowPropertyMap = new WindowPropertyMap();
        for (SaveLoadState window : windows) {
            windowPropertyMap.addWithPrefix(window.getFName(),
                    window.saveState());
        }
        fileStateManager.save(windowPropertyMap);
    }
}
