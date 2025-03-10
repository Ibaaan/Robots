package gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Работает с состоянием окон
 */
public class WindowManager {
    /**
     * <p>Параметры всех сохраненных окон приложения</p>
     * <p>Формат имени - "windowName.parameterName"</p>
     */
    private final Map<String, Integer> windowsParameters;

    /**
     * Набор всех имен окон
     */
    private final Set<String> windowsNames;

    /**
     * @param windowsNames      Набор всех имен окон
     * @param windowsParameters Параметры всех сохраненных окон приложения
     */
    public WindowManager(Set<String> windowsNames, Map<String, Integer> windowsParameters) {
        this.windowsNames = windowsNames;
        this.windowsParameters = windowsParameters;
    }

    /**
     * Устанавливает параметры окна
     */
    public void setWindowParameters(SaveLoadState frame) {
        frame.loadState(getParameters(frame.getFName()));
    }

    /**
     * Фильтрует параметры по названию окна и возвращает их
     * в качестве Map, если название не найдено возвращает null
     */
    private Map<String, Integer> getParameters(String name) {
        if (windowsNames.isEmpty() | !windowsNames.contains(name)) {
            return null;
        }

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
     * Сохраняет параметры при закрытии окна
     */
    public void saveParameters(SaveLoadState frame) {
        String frameName = frame.getFName();
        Map<String, Integer> params = frame.saveState();
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            windowsParameters.put(
                    frameName + "." + entry.getKey(),
                    entry.getValue()
            );
        }
    }

    /**
     * Геттер для параметров окон
     */
    public Map<String, Integer> getWindowsParameters() {
        return windowsParameters;
    }
}
