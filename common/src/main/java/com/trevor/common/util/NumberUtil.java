package com.trevor.common.util;

public class NumberUtil {

    public static Integer stringFormatInteger(String numebr){
        return Integer.valueOf(numebr);
    }

    public static String formatString(Object obj) {
        return String.valueOf(obj);
    }

    public static String stringSplitGetLast(String str ,String delimiter){
        if (str == null || str.isEmpty()) {
            return null;
        }
        String[] split = str.split(delimiter);
        return split[split.length - 1];
    }
}
