package gui;

import log.Logger;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;

public class MainApplicationFrame extends JFrame implements SaveLoadState {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Locale locale;
    private final WindowManager windowManager;
    private final SaverAndLoader saverAndLoader;

    public MainApplicationFrame() {

        saverAndLoader = new SaverAndLoader();
        locale = Locale.of("ru", "RUS");
        windowManager = saverAndLoader.iniWindowManager();
        List<SaveLoadState> windows = initWindows();
        setParameters(windows);

        setContentPane(desktopPane);

        addJInternalFramesToMainFrame(filterJInternalFramesFromFrames(windows));

        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = addPaneWhenCloseMainFrame(e);
                if (option == 0) {
                    setVisible(false);
                    saveWindowParams(windows);
                    dispose();
                    System.exit(0);
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Устанавливает параметры окнам реализующим SaveLoadState
     */
    private void setParameters(List<SaveLoadState> windows) {
        for (SaveLoadState window : windows) {
            windowManager.setWindowParameters(window);
        }
    }

    /**
     * Сохраняет все окна реализующие SaveLoadState
     */
    private void saveWindowParams(List<SaveLoadState> windows) {
        for (SaveLoadState window : windows) {
            windowManager.saveParameters(window);
        }
        saverAndLoader.save(windowManager.getWindowsParameters());
    }

    /**
     * Возвращает все JInternalFrame из списка всех окон
     */
    private List<JInternalFrame> filterJInternalFramesFromFrames(List<SaveLoadState> frames) {
        List<JInternalFrame> result = new ArrayList<>();
        for (SaveLoadState obj : frames) {
            if (obj instanceof JInternalFrame) {
                result.add((JInternalFrame) obj);
            }
        }
        return result;
    }

    /**
     * Добавляет JInternalFrame окна к MainFrame
     */
    private void addJInternalFramesToMainFrame(List<JInternalFrame> jInternalFrameList) {
        for (JInternalFrame frame : jInternalFrameList) {
            addWindow(frame);
        }
    }

    /**
     * Инициализируйте все окна, которые реализуют интерфейс SaveLoadState
     */
    private List<SaveLoadState> initWindows() {
        List<SaveLoadState> result = new ArrayList<>();
        Reflections reflections = new Reflections("gui");
        Set<Class<? extends SaveLoadState>> classes =
                reflections.getSubTypesOf(SaveLoadState.class);

        System.out.println(Arrays.toString(classes.toArray()));

        for (Class<? extends SaveLoadState> clazz : classes) {
            try {
                if (clazz != this.getClass()) {
                    SaveLoadState saveLoadStateImpl =
                            clazz.getDeclaredConstructor().newInstance();
                    result.add(saveLoadStateImpl);
                } else {
                    result.add(this);
                }
            } catch (InstantiationException |
                     InvocationTargetException |
                     IllegalAccessException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Создает панель подтверждения выхода
     */
    private int addPaneWhenCloseMainFrame(WindowEvent e) {
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
