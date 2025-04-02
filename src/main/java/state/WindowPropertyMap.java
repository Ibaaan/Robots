package state;

import java.util.*;

/**
 * Хранит свойства окон в виде ("windowName.propertyName" - propertyValue)
 */
public class WindowPropertyMap extends AbstractMap<String, Integer> {
    private final List<Entry<String, Integer>> table;

    public WindowPropertyMap(Map<String, Integer> map) {
        table = new ArrayList<>();
        putAll(map);
    }

    public WindowPropertyMap() {
        table = new ArrayList<>();
    }

    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return Set.copyOf(table);
    }

    /**
     * Фильтрует элементы по префиксу
     */
    public Map<String, Integer> filterByPrefix(String givenPrefix) {
        Map<String, Integer> result = new WindowPropertyMap();
        for (String key : keySet()) {
            String prefix = key.split("\\.")[0];
            String suffix = key.split("\\.")[1];
            if (prefix.equals(givenPrefix)) {
                result.put(suffix, get(key));
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
            put(prefix + "." + entry.getKey(),
                    entry.getValue());
        }
    }


    @Override
    public Integer put(String key, Integer value) {
        table.add(new Entry1(key, value));
        return value;
    }

    private static class Entry1 implements Map.Entry<String, Integer> {
        private final String key;
        private Integer value;

        public Entry1(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public Integer setValue(Integer value) {
            Integer oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return "Entry1{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}
