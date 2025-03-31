package state;

import gui.GameWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Тесты для WindowManager
 */
class WindowManagerTest {
    private final WindowManager windowManager;

    public WindowManagerTest() {

        windowManager = new WindowManager();
    }

    /**
     * Тест на восстановление параметров окна
     */
    @Test
    void recoverWindow() {
        Map<String, Integer> test = new HashMap<>();
        test.put("game.height", 100);
        test.put("game.width", 100);
        GameWindow gameWindow = new GameWindow(null);

        windowManager.recoverWindows(List.of(gameWindow), test);
        Assertions.assertEquals(gameWindow.getHeight(), 100);
        Assertions.assertEquals(gameWindow.getWidth(), 100);

        WindowManager newWindowManager = new WindowManager();
        newWindowManager.recoverWindows(List.of(gameWindow), new HashMap<>());
        Assertions.assertEquals(gameWindow.getHeight(), 400);
        Assertions.assertEquals(gameWindow.getWidth(), 400);
    }

}