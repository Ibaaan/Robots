package gui;

import i18n.LocalizationManager;
import log.LogChangeListener;
import log.LogEntry;
import log.Logger;
import state.HasState;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LogWindow extends JInternalFrame
        implements LogChangeListener, HasState, PropertyChangeListener {
    private static final Integer DEFAULT_WIDTH = 300;
    private static final Integer DEFAULT_HEIGHT = 800;
    private static final Integer DEFAULT_X = 10;
    private static final Integer DEFAULT_Y = 10;
    private final TextArea m_logContent;

    public LogWindow() {
        super(LocalizationManager.getInstance().getLocalizedMessage("LogWindowTitle"),
                true, true, true, true);
        Logger.getDefaultLogSource().registerListener(this);
        m_logContent = new TextArea("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        updateLogContent();
        Logger.debug(LocalizationManager.getInstance()
                .getLocalizedMessage("LogWindowActivated"));
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(DEFAULT_X, DEFAULT_Y);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        LocalizationManager.getInstance().addPropertyChangeListener(this);
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
    public String getWindowName() {
        return "log";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setTitle(LocalizationManager.getInstance().getLocalizedMessage("LogWindowTitle"));
    }
}
