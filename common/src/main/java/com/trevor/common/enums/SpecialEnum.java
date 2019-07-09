package com.trevor.common.enums;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 13:57
 **/

public enum  SpecialEnum {
    /**
     * 仅限好友
     */
    JUST_FRIENDS(1 ,"仅限好友"),

    /**
     * 允许观战
     */
    CAN_SEE(2 ,"允许观战"),

    /**
     * 癞子牛牛
     */
    LAIZI_NIUNIU(3 ,"癞子牛牛"),

    /**
     * 允许搓牌
     */
    CAN_CUOPAI(4, "允许搓牌");

    /**
     * 房间类型
     */
    private Integer code;

    /**
     * 房间描述
     */
    private String desc;

    SpecialEnum(Integer code , String desc){
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
