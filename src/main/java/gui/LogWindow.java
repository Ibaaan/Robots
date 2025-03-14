package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, SaveLoadState
{
    private static final Integer DEFAULT_WIDTH = 300;
    private static final Integer DEFAULT_HEIGHT = 800;
    private static final Integer DEFAULT_X = 10;
    private static final Integer DEFAULT_Y = 10;
    private static final Integer DEFAULT_MAXIMUM = 1;
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow()
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = Logger.getDefaultLogSource();
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        Logger.debug("Протокол работает");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }


    @Override
    public String getFName() {
        return "log";
    }

    @Override
    public void loadState(Map<String, Integer> parametres) {
        if (parametres == null) {
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            setLocation(DEFAULT_X, DEFAULT_Y);
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


}
