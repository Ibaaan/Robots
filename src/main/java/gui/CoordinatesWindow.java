package gui;

import state.SaveLoadState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Окно с координатами робота
 */
public class CoordinatesWindow extends JInternalFrame implements SaveLoadState, PropertyChangeListener {
    private final DataModel model;
    private final JLabel content;
    private String robotCoordinates;

    public CoordinatesWindow(DataModel model) {
        super("Координатное окно", true, true, true, true);
        this.model = model;
        content = new JLabel(robotCoordinates);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new GridBagLayout());
        add(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(content);

        model.addTextChangeListener(this);

        setSize(400, 100);
        setLocation(50, 50);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Override
    public String getFName() {
        return "coordinates";
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
    public void propertyChange(PropertyChangeEvent evt) {
        robotCoordinates = "X:" + model.getRobotX() + ", Y:" + model.getRobotY();
        content.setText(robotCoordinates);
        repaint();
    }
}
