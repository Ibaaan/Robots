package log;

import util.LogList;
import util.WeakArrayList;

import java.util.Collections;
import java.util.List;


public class LogWindowSource {

    private final LogList messages;
    private final List<LogChangeListener> listeners;
    private volatile List<LogChangeListener> activeListeners;

    public LogWindowSource(int iQueueLength) {
        messages = new LogList(iQueueLength);
        listeners = new WeakArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        messages.add(entry);
        List<LogChangeListener> activeListeners = this.activeListeners;
        if (activeListeners == null) {
            synchronized (listeners) {
                if (this.activeListeners == null) {
                    activeListeners = new WeakArrayList<>(listeners);
                    this.activeListeners = activeListeners;
                }
            }
        }
        assert activeListeners != null;
        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= messages.size()) {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, messages.size());
        return messages.subList(startFrom, indexTo);
    }

    public Iterable<LogEntry> all() {
        return messages::iterator;
    }
}
