package com.trevor.common.enums;

public enum GameStatusEnum {

    /**
     * 可以准备
     */
    BEFORE_READY("1" ,"进入房间-准备倒计时前"),

    BEFORE_FAPAI_4("2" ,"倒计时-发4张牌前"),

    BEFORE_QIANGZHUANG_COUNTDOWN("3" ,"发4张牌-抢庄倒计时前"),

    BEFORE_SELECT_ZHUANGJIA("4" ,"抢庄倒计时-确定庄家前"),

    BEFORE_XIANJIA_XIAZHU("5" ,"确定庄家-闲家下注倒计时前"),

    BEFORE_LAST_POKE("6" ,"闲家下注倒计时-再发一张牌前"),

    BEFORE_TABPAI_COUNTDOWN("7" ,"再发一张牌-摊牌倒计时前"),

    BEFORE_CALRESULT("8","摊牌倒计时-给玩家发返回结果前"),

    BEFORE_RETURN_RESULT("9" ,"给玩家返回结果-下一句开始前");


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
