package state;

import gui.GameWindow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class WindowManagerTest {
    private final WindowManager windowManager;

    public WindowManagerTest() {
        Map<String, Integer> test = new HashMap<>();
        test.put("game.height", 100);
        test.put("game.width", 100);
        Set<String> testSet = new HashSet<>();
        testSet.add("game");
        windowManager = new WindowManager(testSet, test);
    }

    @Test
    void recoverWindow() {
        GameWindow gameWindow = new GameWindow();

        windowManager.recoverWindow(gameWindow);
        Assertions.assertEquals(gameWindow.getHeight(), 100);
        Assertions.assertEquals(gameWindow.getWidth(), 100);

        WindowManager newWindowManager = new WindowManager(
                new HashSet<>(), new HashMap<>());
        newWindowManager.recoverWindow(gameWindow);
        Assertions.assertEquals(gameWindow.getHeight(), 400);
        Assertions.assertEquals(gameWindow.getWidth(), 400);

        System.out.println(gameWindow.getLocation());
        System.out.println(System.getProperty("user.home"));
    }

}