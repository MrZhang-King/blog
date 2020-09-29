package com.zb.blog.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    public static <K, V> Map<K,V> addElement(K key, V value){
        Map<K,V> result = new HashMap<>();
        result.put(key,value);
        return result;
    }
}
