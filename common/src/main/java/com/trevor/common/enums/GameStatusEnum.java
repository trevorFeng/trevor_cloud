package com.trevor.common.enums;

public enum GameStatusEnum {

    /**
     * 可以准备
     */
    BEFORE_FAPAI_4("1" ,"进入房间-准备倒计时前-准备倒计时结束发4张牌前"),

    BEFORE_SELECT_ZHUANGJIA("2" ,"发4张牌-抢庄倒计时-抢庄倒计时确定庄家前"),

    BEFORE_LAST_POKE("3" ,"确定庄家-闲家下注倒计时-闲家下注倒计时结束再发一张牌前"),

    BEFORE_CALRESULT("4","再发一张牌-摊牌倒计时-摊牌倒计时结束删除redis键前"),

    BEFORE_DELETE_KEYS("5" ,"删除redis键-下一局开始前");


    private String code ;

    private String desc;

    GameStatusEnum(String code , String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

}
