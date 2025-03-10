package gui;

import log.Logger;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.*;

public class MainApplicationFrame extends JFrame implements SaveLoadState {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Locale locale;
    private final WindowManager windowManager;
    private final List<JInternalFrame> jInternalFrameList;

    public MainApplicationFrame() {

        SaverAndLoader saverAndLoader = new SaverAndLoader();
        locale = Locale.of("ru", "RUS");
        windowManager = saverAndLoader.iniWindowManager();

        windowManager.setWindowParameters(this);



        setContentPane(desktopPane);
        jInternalFrameList = getAllJInternalFrames();
        initAllJInternalFrames();

        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = closeMainApplicationFrame(e);
                if (option == 0) {
                    setVisible(false);
                    windowManager.saveParameters((SaveLoadState) e.getWindow());
                    closeAllJInternalFrames();
                    saverAndLoader.save(windowManager.getWindowsParameters());
                    dispose();
                    System.exit(0);
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Инициализирует все JInternalFrame
     */
    private void initAllJInternalFrames() {
        for (JInternalFrame frame : jInternalFrameList) {
            initJInternalFrame(frame);
        }
    }

    /**
     * Возвращает все JInternalFrame
     */
    private List<JInternalFrame> getAllJInternalFrames() {
        return List.of(
                new GameWindow(),
                createLogWindow()
        );
    }

    /**
     * Закрывает все JInternalFrame
     */
    private void closeAllJInternalFrames() {
        for (JInternalFrame frame : jInternalFrameList) {
            try {
                frame.setClosed(true);
            } catch (PropertyVetoException e) {
                System.out.println(frame.getClass() + " не хочет закрываться" +
                        "\n" + e);
            }
        }
    }

    /**
     * Инициализирует JInternalFrame
     */
    private void initJInternalFrame(JInternalFrame frame) {
        addWindow(frame);
        try {
            addSavingListener(frame);
            windowManager.setWindowParameters((SaveLoadState) frame);
        } catch (ClassCastException e) {
            System.out.println(
                    frame.getClass() + " should implement SaveLoadState\n" + e);
        }

    }


    /**
     * Создает панель подтверждения выхода
     */
    private int closeMainApplicationFrame(WindowEvent e) {
        ResourceBundle rb = ResourceBundle.getBundle(
                "localization/JOptionPane", locale);
        Object[] options = {rb.getString("Yes"), rb.getString("No")};
        return JOptionPane.showOptionDialog(
                e.getWindow(),
                rb.getString("ExitConfirm"),
                "Панельная выходка",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);


    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }


    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void addSavingListener(JInternalFrame frame) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                windowManager.saveParameters((SaveLoadState) frame);
            }
        });
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
    public String getFName() {
        return "main";
    }

    @Override
    public void loadState(Map<String, Integer> parametres) {

        try {
            setSize(parametres.get("width"),
                    parametres.get("height"));
            setLocation(parametres.get("x"),
                    parametres.get("y"));
        } catch (Exception e) {
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset, screenSize.width - inset * 2,
                    screenSize.height - inset * 2);
        }

    }

    @Override
    public Map<String, Integer> saveState() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("width", getBounds().width);
        result.put("height", getBounds().height);
        result.put("x", getBounds().x);
        result.put("y", getBounds().y);
        return result;
    }
}
