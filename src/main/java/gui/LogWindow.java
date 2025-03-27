package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.Logger;
import state.SaveLoadState;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, SaveLoadState {
    private static final Integer DEFAULT_WIDTH = 300;
    private static final Integer DEFAULT_HEIGHT = 800;
    private static final Integer DEFAULT_X = 10;
    private static final Integer DEFAULT_Y = 10;
    private final TextArea m_logContent;

    public LogWindow() {
        super("Протокол работы", true,
                true, true, true);
        Logger.getDefaultLogSource().registerListener(this);
        m_logContent = new TextArea("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        updateLogContent();
        Logger.debug("Протокол работает");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(DEFAULT_X, DEFAULT_Y);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : Logger.getDefaultLogSource().all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }


    @Override
    public String getFName() {
        return "log";
    }

    @Override
    public void loadState(Map<String, Integer> parametres) {
        new WindowController().setParameters(this, parametres);
    }

    @Override
    public Map<String, Integer> saveState() {
        return new WindowController().getParameters(this);
    }
}
