package gui;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * Является промежуточным звеном между JInternalFrame и классами окон,
 * добавляет общую логику установки и возврата параметров
 */
public abstract class JInternalFrameExtended extends JInternalFrame {
    private static final Integer DEFAULT_MAXSIZE = 1;

    public JInternalFrameExtended(String title,
                                  boolean resizable,
                                  boolean closable,
                                  boolean maximizable,
                                  boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
    }

    /**
     * Загружает состояние окна
     */
    protected void setParameters(Map<String, Integer> parametres) {
        try {
            setIcon(parametres.getOrDefault("maximum", DEFAULT_MAXSIZE) == 0);
            setSize(parametres.get("width"),
                    parametres.get("height"));
            setLocation(parametres.get("x"),
                    parametres.get("y"));

        } catch (PropertyVetoException e) {
            System.out.println("Не удается изменить размер окна\n" + e);
        } catch (NullPointerException e) {
            System.out.println("Одно или все поля параметров отсутствуют\n" + e);
        }
    }

    /**
     * Возвращает параметры окна в формате Map
     */
    protected Map<String, Integer> getParameters() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("width", getWidth());
        result.put("height", getHeight());
        result.put("x", getX());
        result.put("y", getY());
        result.put("maximum", isIcon() ? 0 : 1);
        return result;
    }
}
