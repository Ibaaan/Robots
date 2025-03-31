package state;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * Управляет состоянием окон:
 * устанавливает состояние окну,
 * формирует словарь параметров для окна
 */
public class WindowStateUtils {
    private static final Integer DEFAULT_MAX_SIZE = 1;

    /**
     * Загружает состояние окна
     */
    public static void setParameters(Component window, Map<String, Integer> parametres) {
        setSizeLocation(window, parametres);
        setIconify(window, parametres);
    }

    /**
     * Устанавливает свернутость окна
     */
    private static void setIconify(Component window, Map<String, Integer> parametres) {
        if (window instanceof JInternalFrame) {
            try {
                ((JInternalFrame) window).setIcon(parametres.getOrDefault("maximum",
                        DEFAULT_MAX_SIZE) == 0);
            } catch (PropertyVetoException e) {
                System.out.println("WindowController:" +
                        window.getClass() +
                        "Не удается изменить размер окна\n" + e);
            }
        } else if (window instanceof JFrame) {
            ((JFrame) window).setExtendedState(parametres.getOrDefault("maximum", JFrame.ICONIFIED));
        }
    }

    /**
     * Устанавливает размер и расположение окна
     */
    private static void setSizeLocation(Component window, Map<String, Integer> parametres) {
        try {
            window.setSize(parametres.get("width"),
                    parametres.get("height"));
            window.setLocation(parametres.get("x"),
                    parametres.get("y"));
        } catch (NullPointerException e) {
            System.out.println("WindowController:" +
                    window.getClass() +
                    "Одно или все поля параметров отсутствуют\n" + e);
        }
    }

    /**
     * Возвращает параметры окна в формате Map
     */
    public static Map<String, Integer> getParameters(Component window) {
        Map<String, Integer> parameters = new HashMap<>();
        putBounds(window, parameters);
        putIcon(window, parameters);
        return parameters;
    }

    /**
     * Добавляет свернутость окна
     */
    private static void putIcon(Component window, Map<String, Integer> parameters) {
        if (window instanceof JInternalFrame) {
            parameters.put("maximum", ((JInternalFrame) window).isIcon() ? 0 : 1);
        } else if (window instanceof JFrame) {
            parameters.put("maximum", ((JFrame) window).getExtendedState());
        }
    }

    /**
     * Добавляет ширину высоту и расположение(x, y) окна в Map
     */
    private static void putBounds(Component window, Map<String, Integer> parameters) {
        parameters.put("width", window.getBounds().width);
        parameters.put("height", window.getBounds().height);
        parameters.put("x", window.getBounds().x);
        parameters.put("y", window.getBounds().y);
    }
}
