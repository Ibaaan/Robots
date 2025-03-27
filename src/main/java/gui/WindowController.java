package gui;

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
public class WindowController {
    private static final Integer DEFAULT_MAXSIZE = 1;

    /**
     * Загружает состояние окна
     */
    public void setParameters(Component window, Map<String, Integer> parametres) {
        setSizeLocation(window, parametres);
        setIconify(window, parametres);
    }

    /**
     * Устанавливает свернутость окна
     */
    private void setIconify(Component window, Map<String, Integer> parametres) {
        if (window instanceof JInternalFrame) {
            setIconifyJInternalFrame((JInternalFrame) window, parametres);
        } else if (window instanceof JFrame) {
            setIconifyJFrame((JFrame) window, parametres);
        }
    }

    /**
     * Устанавливает свернутость JInternalFrame окна
     */
    private void setIconifyJInternalFrame(JInternalFrame window, Map<String, Integer> parametres) {
        try {
            window.setIcon(parametres.getOrDefault("maximum", DEFAULT_MAXSIZE) == 0);
        } catch (PropertyVetoException e) {
            System.out.println("WindowController:" +
                    window.getClass() +
                    "Не удается изменить размер окна\n" + e);
        }
    }

    /**
     * Устанавливает свернутость для JFrame окна
     */
    private void setIconifyJFrame(JFrame window, Map<String, Integer> parametres) {
        window.setExtendedState(parametres.getOrDefault("maximum", JFrame.ICONIFIED));
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
            System.out.println("WindowController:" +
                    window.getClass() +
                    "Одно или все поля параметров отсутствуют\n" + e);
        }
    }

    /**
     * Возвращает параметры окна в формате Map
     */
    public Map<String, Integer> getParameters(Component window) {
        Map<String, Integer> parameters = new HashMap<>();
        putBounds(window, parameters);
        putIcon(window, parameters);
        return parameters;
    }

    /**
     * Добавляет свернутость окна
     */
    private void putIcon(Component window, Map<String, Integer> parameters) {
        if (window instanceof JInternalFrame) {
            putIconJInternalFrame((JInternalFrame) window, parameters);
        } else if (window instanceof JFrame) {
            putIconJFrame((JFrame) window, parameters);
        }
    }

    /**
     * Добавляет свернутость JFrame окна
     */
    private void putIconJFrame(JFrame window, Map<String, Integer> parameters) {
        parameters.put("maximum", window.getExtendedState());
    }

    /**
     * Добавляет свернутость JInternalFrame окна
     */
    private void putIconJInternalFrame(JInternalFrame window, Map<String, Integer> parameters) {
        parameters.put("maximum", window.isIcon() ? 0 : 1);
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
