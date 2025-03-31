package state;

import java.util.HashMap;
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
        Map<String, Integer> windowsProperties =
                fileStateManager.getAllProperties();
        for (SaveLoadState window : windows) {
            Map<String, Integer> params = filterProperties(window.getFName(), windowsProperties);
            if (!params.isEmpty()) {
                window.loadState(params);
            }
        }
    }

    /**
     * Фильтрует свойства по названию окна и возвращает их
     * в качестве Map, если название не найдено возвращает null
     */
    // TODO
    private Map<String, Integer> filterProperties(String name,
                                                  Map<String, Integer> windowsParameters) {
        Map<String, Integer> result = new HashMap<>();
        for (String windowProperty : windowsParameters.keySet()) {
            String windowName = windowProperty.split("\\.")[0];
            String propertyName = windowProperty.split("\\.")[1];
            if (name.equals(windowName)) {
                result.put(propertyName, windowsParameters.get(windowProperty));
            }
        }

        return result;
    }

    /**
     * Формирует словарь из свойств окон
     */
    public Map<String, Integer> formStringProperties(List<SaveLoadState> windows) {
        Map<String, Integer> windowsProperties = new HashMap<>();

        for (SaveLoadState window : windows) {
            String frameName = window.getFName();
            Map<String, Integer> props = window.saveState();
            for (Map.Entry<String, Integer> entry : props.entrySet()) {
                windowsProperties.put(
                        frameName + "." + entry.getKey(),
                        entry.getValue()
                );
            }
        }

        return windowsProperties;
    }

    /**
     * Сохраняет свойства окон
     */
    public void saveWindows(List<SaveLoadState> windows) {
        fileStateManager.save(formStringProperties(windows));
    }
}
