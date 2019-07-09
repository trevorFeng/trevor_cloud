package com.trevor.common.enums;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 17:21
 **/

public enum  FridendManageAllowEnum {
    YES(1 ,"通过"),

    NO(2 ,"未通过");

    private Integer code;

    private String desc;

    FridendManageAllowEnum (Integer code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
