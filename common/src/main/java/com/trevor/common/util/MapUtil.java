package com.trevor.common.util;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapUtil {

    /**
     * Map中根据key批量删除键值对
     * @param map
     * @param excludeKeys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map removeEntries(Map<K, V> map, List<K> excludeKeys) {
        Iterator<K> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            K k = iterator.next();
            // 如果k刚好在要排除的key的范围中
            if (excludeKeys.contains(k)) {
                iterator.remove();
                map.remove(k);
            }
        }
        return map;
    }

}
