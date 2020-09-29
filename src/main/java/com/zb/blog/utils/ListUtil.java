package com.zb.blog.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static <T> List<T> addElement(T element){
        List<T> result = new ArrayList<>();
        result.add(element);
        return result;
    }
}
