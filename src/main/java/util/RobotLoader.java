package util;

import game.RobotImpl;
import game.RobotModel;
import l10n.LocalizationManager;
import log.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

/**
 * Загружает робота из jar архива
 */
public class RobotLoader {
    private static final java.util.logging.Logger LOGGER =
            java.util.logging.Logger.getLogger(RobotLoader.class.getName());

    public static RobotModel getNewRobotOrDefault(RobotImpl defaultRobot) {
        try {
            File jarFile = new File("C:\\Users\\kuuuu\\IdeaProjects\\jarFIle\\out\\artifacts\\jarFIle_jar\\jarFIle.jar");
            URL jarUrl = jarFile.toURI().toURL();

            // Create a URLClassLoader
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl})) {
                Class<?> clazz = classLoader.loadClass("RobotV2");

                if (RobotModel.class.isAssignableFrom(clazz)) {
                    return (RobotModel) clazz.getDeclaredConstructor().newInstance();
                } else {
                    LOGGER.log(Level.WARNING, "Загруженный класс не реализует " +
                            RobotModel.class);
                }
            }
        } catch (ReflectiveOperationException e) {
            Logger.error(LocalizationManager.getInstance().getLocalizedMessage("ReflectError"));
            LOGGER.log(Level.WARNING, "Не удалось загрузить класс извне", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Неудачная или прерванная операция ввода-вывода.", e);
        }

        return defaultRobot;
    }
}
