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
    private static final Logger LOGGER = Logger.getLogger(FileStateManager.class.getName());
    private static final String FOLDER_NAME = "Kushtanov";
    private static final String FILE_NAME = "state.config";
    private final String folderPath = System.getProperty("user.home") +
            File.separator + FOLDER_NAME;
    private final String filePath = folderPath +
            File.separator + FILE_NAME;

    /**
     * Возвращает Map со всеми параметрами окна, хранящихся в файле
     */
    public Map<String, String> getAllProperties() {
        Map<String, String> result = new HashMap<>();
        Properties properties = readConfig();
        for (String key : properties.stringPropertyNames()) {
            result.put(key, properties.get(key).toString());
        }
        return result;
    }

    /**
     * Сохраняет параметры из Map в state.cfg в домашнем каталоге пользователя
     */
    public void save(Map<String, String> properties) {
        File folder = new File(folderPath);
        File file = new File(filePath);

        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.severe("Ошибка при создании директории: " + folder.getAbsolutePath());
            return;
        }

        try {
            if (file.createNewFile()) {
                LOGGER.info("Конфиг файл успешно создался: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка при создании конфига", e);
            return;
        }

        Properties savedProps = new Properties();
        properties.forEach(savedProps::setProperty);


        try (FileWriter fileWriter = new FileWriter(filePath)) {
            savedProps.store(fileWriter, "Window State Properties");
            LOGGER.info("Cвойства успешно сохранены");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Не удалось записать параметры в файл", e);
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
                LOGGER.log(Level.SEVERE,
                        "Не удалось получить доступ к файлу с конфигом", e);
            }
        }
        return properties;
    }
}
