package state;

import l10n.LocalizationManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Восстанавливает свойства окон, формирует словарь для сохранения в файл
 */
public class WindowStateManager {
    private static final Logger LOGGER = Logger.getLogger(WindowStateManager.class.getName());
    private static final Integer DEFAULT_MAX_SIZE = 1;
    private final FileStateManager fileStateManager;
    private final LocalizationManager localizationManager = LocalizationManager.getInstance();

    public WindowStateManager() {
        this.fileStateManager = new FileStateManager();
    }

    /**
     * Восстанавливает свойства окон
     */
    public void recoverWindows(List<HasState> windows) {
        WindowPropertyMap windowsProperties = new WindowPropertyMap();

        Map<String, String> properties = fileStateManager.getAllProperties();
        for (String key : properties.keySet()) {
            if (key.equals(LocalizationManager.NAME)) {
                localizationManager.changeLanguageTo(properties.get(key));
                continue;
            }
            try {
                windowsProperties.put(key,
                        Integer.parseInt(properties.get(key)));
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Значение по ключу " + key +
                        " не является числом", e);
            }
        }

        for (HasState window : windows) {
            Map<String, Integer> props =
                    windowsProperties.filterByPrefix(window.getWindowName());

            if (!props.isEmpty()) {
                setParameters((Component) window, props);
            }
        }
    }

    /**
     * Сохраняет свойства окон
     */
    public void saveWindows(List<HasState> windows) {
        WindowPropertyMap windowPropertyMap = new WindowPropertyMap();
        for (HasState window : windows) {
            windowPropertyMap.addWithPrefix(
                    window.getWindowName(),
                    getParameters((Component) window));
        }
        fileStateManager.save(formGeneralPropertyMap(windowPropertyMap));
    }

    /**
     * Объединят свойства окон с другими свойствами
     *
     * @param windowPropertyMap Свойства окон
     * @return Map<String, String> со всеми свойствами
     */
    private Map<String, String> formGeneralPropertyMap(WindowPropertyMap windowPropertyMap) {
        Map<String, String> result = windowPropertyMap.convertValuesToString();
        result.put(LocalizationManager.NAME, localizationManager.getLanguage());
        return result;
    }

    /**
     * Загружает состояние окна
     */
    private void setParameters(Component window, Map<String, Integer> parametres) {
        setSizeLocation(window, parametres);
        setIconify(window, parametres);
    }

    /**
     * Устанавливает свернутость окна
     */
    private void setIconify(Component window, Map<String, Integer> parametres) {
        if (window instanceof JInternalFrame internalFrame) {
            try {
                internalFrame.setIcon(parametres.getOrDefault("maximum",
                        DEFAULT_MAX_SIZE) == 0);
            } catch (PropertyVetoException e) {
                LOGGER.log(Level.WARNING, "WindowController:" + window.getClass() +
                        "Не удается изменить размер окна", e);
            }
        } else if (window instanceof JFrame frame) {
            frame.setExtendedState(parametres.getOrDefault("maximum", JFrame.ICONIFIED));
        }
    }

    /**
     * Устанавливает размер и расположение окна
     */
    private void setSizeLocation(Component window, Map<String, Integer> parametres) {
        try {
            window.setSize(parametres.get("width"),
                    parametres.get("height"));
            window.setLocation(parametres.get("x"),
                    parametres.get("y"));
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "WindowController:" + window.getClass() +
                    "Одно или все поля параметров отсутствуют", e);
        }
    }

    /**
     * Возвращает параметры окна в формате Map
     */
    private Map<String, Integer> getParameters(Component window) {
        Map<String, Integer> parameters = new HashMap<>();
        putBounds(window, parameters);
        putIcon(window, parameters);
        return parameters;
    }

    /**
     * Добавляет свернутость окна
     */
    private void putIcon(Component window, Map<String, Integer> parameters) {
        if (window instanceof JInternalFrame internalFrame) {
            parameters.put("maximum", internalFrame.isIcon() ? 0 : 1);
        } else if (window instanceof JFrame frame) {
            parameters.put("maximum", frame.getExtendedState());
        }
    }

    /**
     * Добавляет ширину высоту и расположение(x, y) окна в Map
     */
    private void putBounds(Component window, Map<String, Integer> parameters) {
        parameters.put("width", window.getBounds().width);
        parameters.put("height", window.getBounds().height);
        parameters.put("x", window.getBounds().x);
        parameters.put("y", window.getBounds().y);
    }
}
