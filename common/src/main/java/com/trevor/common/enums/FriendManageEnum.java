package com.trevor.common.enums;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 14:15
 **/

public enum  FriendManageEnum {

    YES(1 ,"是"),

    NO(2 ,"否");

    private Integer code;

    private String desc;

    FriendManageEnum (Integer code ,String desc){
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
