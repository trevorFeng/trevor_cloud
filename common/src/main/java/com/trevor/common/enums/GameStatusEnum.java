package com.trevor.common.enums;

public enum GameStatusEnum {

    READY("1" ,"准备"),

    READY_COUNT_DOWN_START("2" ,"准备倒计时开始"),

    READY_COUNT_DOWN_END("3" ,"准备倒计时结束"),

    FA_FOUR_PAI("4" ,"发4张牌"),

    QIANG_ZHUANG_COUNT_DOWN_START("5" ,"抢庄倒计时开始"),

    QIANG_ZHUANG_COUNT_DOWN_END("6" ,"抢庄倒计时结束"),

    QIANG_ZHUANG_ZHUAN_QUAN("7" ,"选择庄家，转圈得玩家id集合"),

    XIA_ZHU_COUNT_DOWN_START("8" ,"闲家下注倒计时开始"),

    XIA_ZHU_COUNT_DOWN_END("9" ,"闲家下注倒计时结束"),

    DEFAULT_XIA_ZHU("10" ,"默认下注"),

    FA_ONE_PAI("10" ,"发一张牌"),

    TAN_PAI_COUNT_DOWN_START("11" ,"摊牌倒计时开始"),

    TAN_PAI_COUNT_DOWN_END("12" ,"摊牌倒计时结束"),

    JIE_SUAN("13" ,"本局结算"),

    STOP_OR_CONTINUE("14" ,"下一局或者结束");

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
