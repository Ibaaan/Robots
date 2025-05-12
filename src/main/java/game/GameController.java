package game;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Провоцирует изменение модели
 */
public class GameController {
    private final GameModel model;
    private final Timer timer;

    public GameController(GameModel model) {
        this.model = model;
        timer = new Timer("events generator", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updateRobotPosition();
            }
        }, 0, 10);
    }

    /**
     * Устанавливает новую конечную точку
     *
     * @param point Точка(x, y)
     */
    public void updateTargetPosition(Point point) {
        model.setTargetPosition(point.x, point.y);
    }

    /**
     * Останавливает работу таймера
     */
    public void stop() {
        timer.cancel();
    }
}
