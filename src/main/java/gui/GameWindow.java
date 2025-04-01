package gui;

import game.GameController;
import game.GameModel;
import game.GameVisualizer;
import state.SaveLoadState;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameWindow extends JInternalFrame implements SaveLoadState {
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
    public void loadState(Map<String, Integer> parametres) {
        new WindowController().setParameters(this, parametres);
    }

    @Override
    public Map<String, Integer> saveState() {
        return new WindowController().getParameters(this);
    }

    @Override
    public String getFName() {
        return "game";
    }
}
