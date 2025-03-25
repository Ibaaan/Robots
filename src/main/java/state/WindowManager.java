package state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Восстанавливает параметры окон, формирует словарь для сохранения в файл
 */
public class WindowManager {
    /**
     * Восстанавливает параметры окон
     */
    public void recoverWindows(List<SaveLoadState> windows,
                               Map<String, Integer> windowsParameters) {
        for (SaveLoadState window : windows) {
            window.loadState(filterParameters(window.getFName(), windowsParameters));
        }
    }

    /**
     * Фильтрует параметры по названию окна и возвращает их
     * в качестве Map, если название не найдено возвращает null
     */
    private Map<String, Integer> filterParameters(String name,
                                                  Map<String, Integer> windowsParameters) {
        Map<String, Integer> result = new HashMap<>();
        for (String windowParameter : windowsParameters.keySet()) {
            String windowName = windowParameter.split("\\.")[0];
            String parameterName = windowParameter.split("\\.")[1];
            if (name.equals(windowName)) {
                result.put(parameterName, windowsParameters.get(windowParameter));
            }
        }

        return result;
    }

    /**
     * Формирует словарь из параметров окон
     */
    public Map<String, Integer> getParameters(List<SaveLoadState> windows) {
        Map<String, Integer> windowsParameters = new HashMap<>();

        for (SaveLoadState window : windows) {
            String frameName = window.getFName();
            Map<String, Integer> params = window.saveState();
            for (Map.Entry<String, Integer> entry : params.entrySet()) {
                windowsParameters.put(
                        frameName + "." + entry.getKey(),
                        entry.getValue()
                );
            }
        }

        return windowsParameters;
    }
}
