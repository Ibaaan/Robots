package game;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private final GameModel model;

    public GameController(GameModel model) {
        this.model = model;
        Timer m_timer = new Timer("events generator", true);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.onModelUpdateEvent();
            }
        }, 0, 10);
    }

    public void setTargetPosition(Point point) {
        model.setTargetPosition(point);
    }
}
