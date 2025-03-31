package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Читает данные из state.cfg и сохраняет данные в state.cfg
 */
public class FileStateManager {

    private final String filePath =
            System.getProperty("user.home") +
                    File.separator + "Kushtanov" +
                    File.separator + "state.config";
    private final File file = new File(filePath);

    /**
     * Возвращает Map со всеми параметрами окна, хранящихся в файле
     */
    public Map<String, Integer> getAllProperties() {
        Map<String, Integer> result = new HashMap<>();
        Properties properties = readConfig();
        for (Object key : properties.keySet()) {
            try {
                result.put(
                        (String) key,
                        Integer.parseInt((String) properties.get(key))
                );
            } catch (NumberFormatException e) {
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

        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
        }
        System.out.println("Параметры успешно сохранены");
        Properties savedProps = new Properties();
        for (Map.Entry<String, Integer> entry : properties.entrySet()) {
            savedProps.setProperty(entry.getKey(), entry.getValue().toString());
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {

            savedProps.store(fileWriter, "");

        } catch (IOException e) {
        }
    }

    /**
     * Считывает параметры из конфига и инициализирует свойства
     */
    private Properties readConfig() {
        Properties properties = new Properties();
        if (file.exists() & file.canRead()) {
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException ex) {
            }
        }
        return properties;
    }
}
