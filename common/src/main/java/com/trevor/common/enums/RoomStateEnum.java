package com.trevor.common.enums;

/**
 * @author trevor
 * @date 2019/3/13 10:50
 */
public enum RoomStateEnum {

    /**
     * 可用
     */
    CAN_USER(1 ,"可用"),

    /**
     * 已完成
     */
    HAVA_USE(2 ,"已完成"),

    /**
     * 已过期
     */
    OVER_DUE(3,"已过期");

    RoomStateEnum(Integer code , String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }}
