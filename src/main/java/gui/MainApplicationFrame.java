package gui;

import game.GameModel;
import log.Logger;
import state.HasState;
import state.WindowStateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainApplicationFrame extends JFrame implements HasState {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Locale locale;
    private final WindowStateManager windowStateManager;
    private final List<HasState> windows;

    public MainApplicationFrame() {
        windowStateManager = new WindowStateManager();
        GameModel model = new GameModel();
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        locale = Locale.forLanguageTag("ru-RU");

        addWindow(new GameWindow(model));
        addWindow(new LogWindow());
        addWindow(new CoordinatesWindow(model));

        setContentPane(desktopPane);
        setJMenuBar(createMenuBar());

        windows = getSaveLoadStateWindows();
        windowStateManager.recoverWindows(windows);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosingEvent(e);

            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Возвращает все SaveLoadState окна, включая MainApplicationFrame
     */
    private List<HasState> getSaveLoadStateWindows() {
        List<HasState> windows = Arrays.stream(getContentPane().getComponents())
                .filter(component -> component instanceof HasState)
                .map(component -> (HasState) component)
                .collect(Collectors.toList());
        windows.add(this);
        return windows;
    }

    /**
     * Обработка выхода из приложения,
     * создание диалогового окна
     */
    private void onWindowClosingEvent(WindowEvent e) {
        ResourceBundle rb = ResourceBundle.getBundle(
                "localization/JOptionPane", locale);
        Object[] options = {rb.getString("Yes"), rb.getString("No")};
        int option = JOptionPane.showOptionDialog(
                e.getWindow(),
                rb.getString("ExitConfirm"),
                "Панельная выходка",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (option == JOptionPane.YES_OPTION) {
            setVisible(false);
            windowStateManager.saveWindows(windows);
            dispose();
            System.exit(0);
        }
    }

    protected void addWindow(Component frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создаёт меню - 'Режим отображения'
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().
                setAccessibleDescription("Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeelItem());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelItem());

        return lookAndFeelMenu;
    }

    private JMenuItem createCrossPlatformLookAndFeelItem() {
        JMenuItem crossPlatformLookAndFeelItem = new JMenuItem(
                "Универсальная схема", KeyEvent.VK_S);
        crossPlatformLookAndFeelItem.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossPlatformLookAndFeelItem;
    }

    private JMenuItem createSystemLookAndFeelItem() {
        JMenuItem systemLookAndFeelItem = new JMenuItem(
                "Системная схема", KeyEvent.VK_S);
        systemLookAndFeelItem.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeelItem;
    }

    /**
     * Создаёт меню - "Выход"
     */
    private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu("Выход");
        exitMenu.setMnemonic(KeyEvent.VK_A);

        exitMenu.add(createExitItem());
        return exitMenu;
    }

    /**
     * Создает опцию в меню "Выход"
     */
    private JMenuItem createExitItem() {
        JMenuItem exitItem = new JMenuItem(
                "Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        return exitItem;
    }

    /**
     * Создаёт меню - "Тесты"
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        testMenu.add(createAddLogMessageItem());
        return testMenu;

    }

    private JMenuItem createAddLogMessageItem() {
        JMenuItem addLogMessageItem = new JMenuItem(
                "Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) ->
                Logger.debug("Новая строка"));

        return addLogMessageItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createExitMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }


    @Override
    public String getWindowName() {
        return "main";
    }
}
