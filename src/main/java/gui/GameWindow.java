package gui;

import state.SaveLoadState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class GameWindow extends JInternalFrame implements SaveLoadState, PropertyChangeListener {
    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 400;
    private final GameVisualizer m_visualizer;

    public GameWindow(DataModel model) {
        super("Игровое поле", true, true, true, true);

        m_visualizer = new GameVisualizer(model);
        model.addTextChangeListener(this);

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        m_visualizer.onRedrawEvent();
    }
}
