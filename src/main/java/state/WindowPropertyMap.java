package state;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Хранит свойства окон в виде ("windowName.propertyName" - propertyValue)
 */
public class WindowPropertyMap extends AbstractMap<String, Integer> {
    private final Map<String, Integer> properties;

    public WindowPropertyMap(Map<String, Integer> map) {
        this.properties = new HashMap<>(map);
    }

    public WindowPropertyMap() {
        this.properties = new HashMap<>();
    }

    /**
     * Фильтрует элементы по префиксу
     */
    public Map<String, Integer> filterByPrefix(String givenPrefix) {
        Map<String, Integer> result = new WindowPropertyMap();
        for (String key : properties.keySet()) {
            String[] parts = key.split("\\.", 2); // Split only once
            if (parts.length == 2 && parts[0].equals(givenPrefix)) {
                result.put(parts[1], properties.get(key));
            }
        }
        return result;
    }

    /**
     * Добавляет все элементы данной map,
     * приписывая к их ключам данный prefix
     */
    public void addWithPrefix(String prefix, Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            properties.put(prefix + "." + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return properties.entrySet();
    }

    @Override
    public Integer put(String key, Integer value) {
        return properties.put(key, value);
    }

    @Override
    public String toString() {
        return "WindowPropertyMap{" +
                "properties=" + properties +
                '}';
    }
}