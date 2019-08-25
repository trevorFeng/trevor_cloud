package com.trevor.common.enums;

public enum GameStatusEnum {

    READY("1" ,"准备"),

    READY_COUNT_DOWN("2" ,"准备倒计时开始"),

    FA_FOUR_PAI("3" ,"发4张牌开始"),

    QIANG_ZHUANG_COUNT_DOWN("4" ,"抢庄倒计时"),

    QIANG_ZHUANG_ZHUAN_QUAN("5" ,"抢庄转圈"),

    XIA_ZHU_COUNT_DOWN("6" ,"闲家下注倒计时"),

    FA_ONE_PAI("7" ,"发一张牌"),

    TAN_PAI_COUNT_DOWN("8" ,"摊牌倒计时"),

    JIE_SUAN("9" ,"本局结算"),

    STOP_OR_CONTINUE("10" ,"下一局或者结束");

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
