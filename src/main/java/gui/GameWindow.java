package gui;

import game.GameController;
import game.GameModel;
import game.GameVisualizer;
import l10n.LocalizationManager;
import state.HasState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GameWindow extends JInternalFrame implements HasState, PropertyChangeListener {
    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 400;
    private final GameController controller;

    public GameWindow(GameModel model) {
        super(LocalizationManager.getInstance().getLocalizedMessage("GameWindowTitle"),
                true, true, true, true);

        controller = new GameController(model);
        GameVisualizer m_visualizer = new GameVisualizer(model, controller);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        LocalizationManager.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        controller.stop();
        super.dispose();
    }

    @Override
    public String getWindowName() {
        return "game";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setTitle(LocalizationManager.getInstance().getLocalizedMessage("GameWindowTitle"));
    }
}
