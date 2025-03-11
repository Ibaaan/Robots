package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private Locale locale;

    public MainApplicationFrame() {
        locale = Locale.of("ru",
                "RUS");
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset =
                50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);


        addWindow(new GameWindow());

        setJMenuBar(generateMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeMainApplicationFrame(e);
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void closeMainApplicationFrame(WindowEvent e) {
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
        if (option == 0) {
            dispose();
            System.exit(0);
        }

    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
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

    private JMenuBar generateMenuBar() {
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
}
