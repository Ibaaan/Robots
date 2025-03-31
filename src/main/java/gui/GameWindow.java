package gui;

import state.SaveLoadState;
import state.WindowStateUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameWindow extends JInternalFrame implements SaveLoadState {
    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 400;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Override
    public void loadState(Map<String, Integer> parametres) {
        WindowStateUtils.setParameters(this, parametres);
    }

    @Override
    public Map<String, Integer> saveState() {
        return WindowStateUtils.getParameters(this);
    }

    @Override
    public String getFName() {
        return "game";
    }
}
