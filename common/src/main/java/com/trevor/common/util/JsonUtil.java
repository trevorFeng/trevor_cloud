package com.trevor.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @author trevor
 * @date 06/27/19 18:29
 */
public class JsonUtil {

    public static <T> String  toJsonString (T t) {
        return JSON.toJSONString(t);
    }

    public static <T> List<T> parseJavaList(String str , Class<T> c){
        List<T> t1 = JSON.parseArray(str,c);
        return t1;
    }

    public static <T> T parseJavaObject(String str , Class<T> c){
        T t1 = JSON.parseObject(str,c);
        return t1;
    }


 }
