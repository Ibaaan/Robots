package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Читает данные из state.cfg и сохраняет данные в state.cfg
 */
public class SaverAndLoader {
    private Properties prop = null;
    private final String filePath =
            System.getProperty("user.home") + File.separator + "state.config";
    private final File file = new File(filePath);

    /**
     * Инициализация properties из state.cfg
     */
    public SaverAndLoader() {
        if (file.exists() & file.canRead()) {
            prop = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Инициализирует WindowManager
     */
    public WindowManager iniWindowManager() {
        return new WindowManager(
                getWindowNames(),
                getAllParameters());
    }

    /**
     * Возвращает Map со всеми параметрами окна, хранящихся в файле
     */
    private Map<String, Integer> getAllParameters() {
        HashMap<String, Integer> result = new HashMap<>();
        if (prop == null) {
            return result;
        }
        for (Object key : prop.keySet()) {
            try {
                result.put(
                        (String) key,
                        Integer.parseInt((String) prop.get(key))
                );
            } catch (Exception e) {
                System.out.println("Wrong format of contained data\n" + e);
            }
        }
        return result;
    }

    /**
     * Возвращает набор имен окон, хранящихся в файле
     */
    private Set<String> getWindowNames() {
        HashSet<String> result = new HashSet<>();
        if (prop == null) {
            return result;
        }

        for (Object key : prop.keySet()) {
            try {
                result.add(((String) key).split("\\.")[0]);
            } catch (Exception e) {
                System.out.println("Wrong format of contained data\n" + e);
            }
        }
        return result;
    }

    /**
     * Сохраняет параметры из Map в state.cfg в домашнем каталоге пользователя
     */
    public void save(Map<String, Integer> params) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(params);
        Properties savedProps = new Properties();
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            savedProps.setProperty(entry.getKey(), entry.getValue().toString());
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {

            savedProps.store(fileWriter, "INFORMATION!!!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
