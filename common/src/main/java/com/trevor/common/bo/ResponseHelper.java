package com.trevor.common.bo;

import com.trevor.common.enums.MessageCodeEnum;

/**
 * @author trevor
 * @date 2019/3/7 13:33
 */
public class ResponseHelper {

    public ResponseHelper() {
    }

    /**
     * 成功，带数据的返回
     * @param object
     * @param messageCodeEnum
     * @param <T>
     * @return
     */
    public static <T> JsonEntity<T> createInstance(T object , MessageCodeEnum messageCodeEnum) {
        JsonEntity<T> response = new JsonEntity(object);
        response.setCode(messageCodeEnum.getCode());
        response.setMessage(messageCodeEnum.getMessage());
        return response;
    }

    /**
     * 成功，不带数据的返回
     * @param messageCodeEnum
     * @param <T>
     * @return
     */
    public static <T> JsonEntity<T> createInstanceWithOutData(MessageCodeEnum messageCodeEnum) {
        JsonEntity<T> response = new JsonEntity();
        response.setCode(messageCodeEnum.getCode());
        response.setMessage(messageCodeEnum.getMessage());
        return response;
    }


    /**
     * 错误信息的返回
     * @param messageCodeEnum
     * @param <T>
     * @return
     */
    public static <T> JsonEntity<T> withErrorInstance(MessageCodeEnum messageCodeEnum) {
        JsonEntity<T> response = new JsonEntity();
        response.setCode(messageCodeEnum.getCode());
        response.setMessage(messageCodeEnum.getMessage());
        return response;
    }

    /**
     * 异常信息的返回
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> JsonEntity<T> withExceptionInstance(Integer errorCode ,String errorMessage) {
        JsonEntity<T> response = new JsonEntity();
        response.setCode(errorCode);
        response.setMessage(errorMessage);
        return response;
    }
}

