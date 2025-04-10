package gui;

import game.GameController;
import game.GameModel;
import game.GameVisualizer;
import state.HasState;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame implements HasState {
    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 400;

    public GameWindow(GameModel model) {
        super("Игровое поле", true, true, true, true);

        GameController controller = new GameController(model);
        GameVisualizer m_visualizer = new GameVisualizer(model, controller);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Override
    public String getWindowName() {
        return "game";
    }
}
