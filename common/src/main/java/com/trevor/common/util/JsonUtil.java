package com.trevor.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @author trevor
 * @date 06/27/19 18:29
 */
public class JsonUtil {

    public static <T> String  toJsonString (T t) {
        return JSON.toJSONString(t);
    }


    public static <T> T parse(String str ,T t){
        T t1 = JSON.parseObject(str,new TypeReference<T>() {});
        return t1;
    }
 }