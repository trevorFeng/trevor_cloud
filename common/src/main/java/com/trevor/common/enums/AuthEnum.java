package com.trevor.common.enums;

/**
 * @author trevor
 * @date 03/21/19 16:59
 */
public enum  AuthEnum {

    /**
     * 已授权
     */
    IS_AUTH("1" ,"已授权"),

    /**
     * 未授权
     */
    NOT_AUTH("0" ,"未授权");

    AuthEnum(String code , String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 代表码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }}
