package log;

import util.LogsList;
import util.WeakArrayList;

import java.util.List;

/**
 * _____________
 * ИСПРАВЛЕО !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * _____________
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений
 * ограниченного размера)
 */
public class LogWindowSource {

    private final List<LogEntry> m_messages;
    private final List<LogChangeListener> m_listeners;
    private volatile List<LogChangeListener> m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        m_messages = new LogsList<>(iQueueLength);
        m_listeners = new WeakArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        List<LogChangeListener> activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    activeListeners = new WeakArrayList<>(m_listeners);
                    m_activeListeners = activeListeners;
                }
            }
        }
        assert activeListeners != null;
        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        return m_messages.subList(startFrom, startFrom + count);
    }

    public Iterable<LogEntry> all() {
        return m_messages;
    }
}
