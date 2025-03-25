package gui;

import log.Logger;
import state.SaveLoadState;
import state.SaverAndLoader;
import state.WindowManager;
import windowfilter.FilterJInternalFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.*;

import static javax.swing.JOptionPane.YES_OPTION;

public class MainApplicationFrame extends JFrame implements SaveLoadState {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Locale locale;
    private final WindowManager windowManager;
    private final SaverAndLoader saverAndLoader;

    public MainApplicationFrame() {


        saverAndLoader = new SaverAndLoader();

        locale = Locale.of("ru", "RUS");
        windowManager = new WindowManager();
        List<SaveLoadState> windows = findAndCreateWindows();
        windowManager.recoverWindows(windows, saverAndLoader.getAllParameters());
        addWindows(windows);

        setContentPane(desktopPane);
        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = addPaneWhenCloseMainFrame(e);
                if (option == YES_OPTION) {
                    setVisible(false);
                    saverAndLoader.save(windowManager.getParameters(windows));
                    dispose();
                    System.exit(0);
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }


    /**
     * Добавляет все окна к главному окну
     */
    private void addWindows(List<SaveLoadState> windows) {
        for (SaveLoadState window : new FilterJInternalFrame().filter(windows)) {
            addWindow((JInternalFrame) window);
        }
    }

    /**
     * Поиск и создание окон, которые реализуют интерфейс SaveLoadState
     */
    private List<SaveLoadState> findAndCreateWindows() {
        List<SaveLoadState> classes = new ArrayList<>();
        String path = "gui";


        URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        if (resource == null) {
            System.out.println("findClassesInPackage:Resources == null");
            return classes;
        }
        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            System.out.println("findClassesInPackage:directory == null");
            return classes;
        }
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
        if (files == null) {
            System.out.println("findClassesInPackage:files == null");
            return classes;
        }
        try {
            for (File file : files) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName("gui" + '.' + className);
                if (SaveLoadState.class.isAssignableFrom(clazz)) {
                    if (!MainApplicationFrame.class.isAssignableFrom(clazz)) {
                        SaveLoadState newWindow = (SaveLoadState) clazz
                                .getDeclaredConstructor()
                                .newInstance();
                        classes.add(newWindow);
                    } else {
                        classes.add(this);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Класс не может быть найден\n" + e);
        } catch (InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException
                 | NoSuchMethodException e) {
            System.out.println("Класс не может быть инициализирован\n" + e);
        }

        return classes;
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
