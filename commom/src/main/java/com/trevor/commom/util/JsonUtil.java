package com.trevor.commom.util;

import com.alibaba.fastjson.JSON;

/**
 * @author trevor
 * @date 06/27/19 18:29
 */
public class JsonUtil {

    public static <T> String  toJsonString (T t) {
        return JSON.toJSONString(t);
    }
}
