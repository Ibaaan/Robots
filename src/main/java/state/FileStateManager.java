package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Читает данные из state.cfg и сохраняет данные в state.cfg
 */
public class FileStateManager {
    private static final Logger logger = Logger.getLogger(FileStateManager.class.getName());
    private final String filePath = System.getProperty("user.home") +
            File.separator + "Kushtanov" +
            File.separator + "state.config";

    /**
     * Возвращает Map со всеми параметрами окна, хранящихся в файле
     */
    public Map<String, Integer> getAllProperties() {
        Map<String, Integer> result = new HashMap<>();
        Properties properties = readConfig();
        for (String key : properties.stringPropertyNames()) {
            try {
                result.put(key,
                        Integer.parseInt((String) properties.get(key)));
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Значение по ключу " + key +
                        " не является числом", e);
            }
        }
        return result;
    }

    /**
     * Сохраняет параметры из Map в state.cfg в домашнем каталоге пользователя
     */
    public void save(Map<String, Integer> properties) {
        File folder = new File(System.getProperty("user.home") +
                File.separator + "Kushtanov");
        File file = new File(filePath);

        if (!folder.exists() && !folder.mkdirs()) {
            logger.severe("Ошибка при создании директории: " + folder.getAbsolutePath());
            return;
        }

        try {
            if (file.createNewFile()) {
                logger.info("Конфиг файл успешно создался: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при создании конфига", e);
            return;
        }

        Properties savedProps = new Properties();
        properties.forEach((key, value) -> savedProps.setProperty(key, value.toString()));

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            savedProps.store(fileWriter, "Window State Properties");
            logger.info("Cвойства успешно сохранены");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось записать параметры в файл", e);
        }
    }

    /**
     * Считывает параметры из конфига и инициализирует свойства
     */
    private Properties readConfig() {
        Properties properties = new Properties();
        File file = new File(filePath);

        if (file.exists() & file.canRead()) {
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Не удалось получить доступ к файлу с конфигом", e);
            }
        }
        return properties;
    }
}
