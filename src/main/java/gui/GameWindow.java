package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JInternalFrame implements SaveLoadState {
    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 400;
    private static final Integer DEFAULT_Y = 0;
    private static final Integer DEFAULT_X = 0;
    private static final Integer DEFAULT_MAXIMUM = 1;
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Override
    public void loadState(Map<String, Integer> parametres) {
        if (parametres == null) {
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else {
            setSize(parametres.getOrDefault("width", DEFAULT_WIDTH),
                    parametres.getOrDefault("height", DEFAULT_HEIGHT));
            setLocation(parametres.getOrDefault("x", DEFAULT_X),
                    parametres.getOrDefault("y", DEFAULT_Y));

            try {
                setIcon(parametres.getOrDefault("maximum", DEFAULT_MAXIMUM) == 0);
            } catch (PropertyVetoException e) {
                System.out.println("Can't change size of window\n" + e);
            }
        }
    }

    @Override
    public Map<String, Integer> saveState() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("width", getWidth());
        result.put("height", getHeight());
        result.put("x", getX());
        result.put("y", getY());
        result.put("maximum", isIcon() ? 0 : 1);
        return result;
    }

    @Override
    public String getFName() {
        return "game";
    }
}
