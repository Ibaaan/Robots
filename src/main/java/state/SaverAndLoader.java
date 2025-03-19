package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Читает данные из state.cfg и сохраняет данные в state.cfg
 */
public class SaverAndLoader {
    private Properties properties = new Properties();
    private final String filePath =
            System.getProperty("user.home") +
                    File.separator + "Kushtanov" +
                    File.separator + "state.config";
    private final File file = new File(filePath);

    /**
     * Инициализация properties из state.cfg
     */
    public SaverAndLoader() {
        if (file.exists() & file.canRead()) {
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException ex) {
                System.out.println("Не удалось получить доступ к файлу с конфигом\n" +
                        ex);
            }
        }
    }

    /**
     * Инициализирует WindowManager
     */
    public WindowManager initWindowManager() {
        return new WindowManager(
                getWindowNames(),
                getAllParameters());
    }

    /**
     * Возвращает Map со всеми параметрами окна, хранящихся в файле
     */
    private Map<String, Integer> getAllParameters() {
        Map<String, Integer> result = new HashMap<>();

        for (Object key : properties.keySet()) {
            try {
                result.put(
                        (String) key,
                        Integer.parseInt((String) properties.get(key))
                );
            } catch (NumberFormatException e) {
                System.out.println("Значение по ключу " + key.toString() +
                        " не является числом\n" + e);
            }
        }
        return result;
    }

    /**
     * Возвращает набор имен окон, хранящихся в файле
     */
    private Set<String> getWindowNames() {
        HashSet<String> result = new HashSet<>();
        if (properties == null) {
            return result;
        }

        for (Object key : properties.keySet()) {
            result.add(((String) key).split("\\.")[0]);
        }
        return result;
    }

    /**
     * Сохраняет параметры из Map в state.cfg в домашнем каталоге пользователя
     */
    public void save(Map<String, Integer> params) {
        File folder = new File(System.getProperty("user.home") +
                File.separator + "Kushtanov");

        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Файл с конфигом не создался\n" + e);
        }
        System.out.println("Параметры успешно сохранены");
        Properties savedProps = new Properties();
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            savedProps.setProperty(entry.getKey(), entry.getValue().toString());
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {

            savedProps.store(fileWriter, "");

        } catch (IOException e) {
            System.out.println("Не удалось записать параметры в файл\n" + e);
        }
    }
}
