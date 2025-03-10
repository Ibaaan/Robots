package gui;

import java.util.Map;

/**
 * Сохраняет и восстанавливает состояние окна
 */
public interface SaveLoadState {
    /**
     * Возвращает имя окна
     */
    String getFName();

    /**
     * Загружает состояние окна
     */
    void loadState(Map<String, Integer> parametres);

    /**
     * Возвращает состояние окна
     */
    Map<String, Integer> saveState();
}
