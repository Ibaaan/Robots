package util;

import game.RobotImpl;
import game.RobotModel;
import l10n.LocalizationManager;
import log.GUILogger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Загружает робота из jar архива
 */
public class RobotLoader {
    private static final Logger LOGGER = Logger.getLogger(RobotLoader.class.getName());

    /**
     * Загружает робота извне, если не получилось возвращает {@code defaultRobot}
     */
    public static RobotModel getNewRobotOrDefault(RobotImpl defaultRobot, Component parent) {
        try {
            Optional<File> optionalJarFile = chooseFileToLoad(parent);

            if (optionalJarFile.isEmpty()) {
                return defaultRobot;
            }

            File jarFile = optionalJarFile.get();
            URL jarUrl = jarFile.toURI().toURL();

            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl})) {
                Class<?> clazz = classLoader.loadClass("RobotV2");

                if (RobotModel.class.isAssignableFrom(clazz)) {
                    LOGGER.log(Level.INFO, "Архив был успешно загружен");
                    GUILogger.debug(LocalizationManager.getInstance().getLocalizedMessage("ClassLoad"));
                    return (RobotModel) clazz.getDeclaredConstructor().newInstance();
                } else {
                    LOGGER.log(Level.WARNING, "Загруженный класс не реализует " +
                            RobotModel.class.getName());
                }
            }
        } catch (ReflectiveOperationException e) {
            GUILogger.error(LocalizationManager.getInstance().getLocalizedMessage("ReflectError"));
            LOGGER.log(Level.WARNING, "Не удалось загрузить класс извне", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Неудачная или прерванная операция ввода-вывода.", e);
        }

        return defaultRobot;
    }

    /**
     * Создает окно с выбором пути до файла и возвращает этот путь
     */
    private static Optional<File> chooseFileToLoad(Component parent) {
        JFileChooser fileChooser = new JFileChooser();

        int returnValue = fileChooser.showOpenDialog(parent);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return Optional.ofNullable(selectedFile);
        } else {
            return Optional.empty();
        }
    }
}
