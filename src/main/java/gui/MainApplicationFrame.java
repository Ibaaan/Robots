package gui;

import game.GameModel;
import game.RobotImpl;
import game.RobotModel;
import l10n.LocalizationManager;
import log.Logger;
import state.HasState;
import state.WindowStateManager;
import util.RobotLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainApplicationFrame extends JFrame implements HasState, PropertyChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager;
    private final List<HasState> windows;

    public MainApplicationFrame() {
        windowStateManager = new WindowStateManager();
        GameModel model = new GameModel();
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        addWindow(new GameWindow(model));
        addWindow(new LogWindow());
        addWindow(new CoordinatesWindow(model));
        setContentPane(desktopPane);

        windows = getSaveLoadStateWindows();
        windowStateManager.recoverWindows(windows);

        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosingEvent(e);
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        LocalizationManager.getInstance().addPropertyChangeListener(this);
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
        LocalizationManager localizationManager = LocalizationManager.getInstance();
        Object[] options = {localizationManager.getLocalizedMessage("Yes"),
                localizationManager.getLocalizedMessage("No")};
        int option = JOptionPane.showOptionDialog(
                e.getWindow(),
                localizationManager.getLocalizedMessage("ExitConfirm"),
                localizationManager.getLocalizedMessage("ExitTitle"),
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
        JMenu lookAndFeelMenu = new JMenu(
                LocalizationManager.getInstance().getLocalizedMessage("DisplayMode"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().
                setAccessibleDescription(LocalizationManager.getInstance()
                        .getLocalizedMessage("DisplayModeDescription"));

        lookAndFeelMenu.add(createSystemLookAndFeelItem());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelItem());

        return lookAndFeelMenu;
    }

    private JMenuItem createCrossPlatformLookAndFeelItem() {
        JMenuItem crossPlatformLookAndFeelItem = new JMenuItem(
                LocalizationManager.getInstance()
                        .getLocalizedMessage("CrossPlatformLookAndFeelItem"),
                KeyEvent.VK_S);
        crossPlatformLookAndFeelItem.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossPlatformLookAndFeelItem;
    }

    private JMenuItem createSystemLookAndFeelItem() {
        JMenuItem systemLookAndFeelItem = new JMenuItem(
                LocalizationManager.getInstance().getLocalizedMessage("SystemLookAndFeelItem"),
                KeyEvent.VK_S);
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
        JMenu exitMenu = new JMenu(
                LocalizationManager.getInstance().getLocalizedMessage("ExitMenu"));
        exitMenu.setMnemonic(KeyEvent.VK_A);

        exitMenu.add(createExitItem());
        return exitMenu;
    }

    /**
     * Создает опцию в меню "Выход"
     */
    private JMenuItem createExitItem() {
        JMenuItem exitItem = new JMenuItem(
                LocalizationManager.getInstance().getLocalizedMessage("ExitMenu")
                , KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        return exitItem;
    }

    /**
     * Создаёт меню - "Тесты"
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(
                LocalizationManager.getInstance().getLocalizedMessage("TestMenu")
        );
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                LocalizationManager.getInstance()
                        .getLocalizedMessage("TestMenuDescription")
        );

        String testLogMessage1 = LocalizationManager.getInstance()
                .getLocalizedMessage("TestLogMessage1");
        String testLogMessage2 = LocalizationManager.getInstance()
                .getLocalizedMessage("TestLogMessage2");


        testMenu.add(createAddLogMessageItem(testLogMessage1));
        testMenu.add(createAddLogMessageItem(testLogMessage2));
        return testMenu;

    }

    private JMenuItem createAddLogMessageItem(String logText) {
        String addedText = LocalizationManager.getInstance()
                .getLocalizedMessage("LogMessagePattern", (Object) logText);
        JMenuItem addLogMessageItem = new JMenuItem(logText, KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) ->
                Logger.debug(addedText));

        return addLogMessageItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createExitMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createLocalizationMenu());
        menuBar.add(createRobotChangerMenu());
        return menuBar;
    }

    private JMenu createRobotChangerMenu() {
        JMenu robotChangeMenu = new JMenu(LocalizationManager.getInstance().getLocalizedMessage("RobotChangeMenu"));

        robotChangeMenu.add(createRobotSetter("DefaultRobot", false));
        robotChangeMenu.add(createRobotSetter("NewRobot", true));
        return robotChangeMenu;
    }

    private JMenuItem createRobotSetter(String keyForOptionName, boolean isLoadingRobot) {
        String menuItemName = LocalizationManager.getInstance().getLocalizedMessage(keyForOptionName);
        JMenuItem robotItem = new JMenuItem(menuItemName);

        robotItem.addActionListener((e -> changeRobot(isLoadingRobot)));
        return robotItem;
    }

    private void changeRobot(boolean isLoadingRobot) {
        RobotModel robotModel;

        if (isLoadingRobot) {
            robotModel = RobotLoader.getNewRobotOrDefault(new RobotImpl(), this);
        } else {
            robotModel = new RobotImpl();
        }

        updateModelUsingWindows(robotModel);
    }

    /**
     * Пересоздает окна использующих модель с новым роботом
     */
    private void updateModelUsingWindows(RobotModel robotModel) {
        GameModel model = new GameModel(robotModel);
        windowStateManager.saveWindows(getSaveLoadStateWindows());

        for (Component component : getContentPane().getComponents()) {
            if (component instanceof GameWindow ||
                    component instanceof CoordinatesWindow) {
                remove(component);
                ((JInternalFrame) component).dispose();
            }
        }

        addWindow(new GameWindow(model));
        addWindow(new CoordinatesWindow(model));

        windowStateManager.recoverWindows(getSaveLoadStateWindows());
    }

    private JMenu createLocalizationMenu() {
        JMenu langChangeMenu = new JMenu(LocalizationManager.getInstance().getLocalizedMessage("LangChange"));

        langChangeMenu.add(createLangItem("ru"));
        langChangeMenu.add(createLangItem("en"));
        return langChangeMenu;
    }

    private JMenuItem createLangItem(String langAcronym) {
        String langName = LocalizationManager.getInstance().getMessage("LangName", langAcronym);
        JMenuItem langItem = new JMenuItem(
                langName);
        langItem.addActionListener((event) ->
                LocalizationManager.getInstance().changeLanguageTo(langAcronym));

        return langItem;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setJMenuBar(createMenuBar());
    }
}
