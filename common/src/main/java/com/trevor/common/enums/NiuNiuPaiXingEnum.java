package com.trevor.common.enums;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-05 22:21
 **/
public enum NiuNiuPaiXingEnum {

    /**
     *
     */
    NIU_16(16 ,"五小牛"),

    NIU_15(15 ,"炸弹牛"),

    NIU_14(14 ,"葫芦牛"),

    NIU_13(13 ,"同花牛"),

    NIU_12(12 ,"五花牛"),

    NIU_11(11 ,"顺子牛"),

    NIU_10(10 ,"牛牛"),

    NIU_9(9 ,"牛九"),

    NIU_8(8 ,"牛八"),

    NIU_7(7 ,"牛七"),

    NIU_6(6 ,"牛六"),

    NIU_5(5 ,"牛五"),

    NIU_4(4 ,"牛四"),

    NIU_3(3 ,"牛三"),

    NIU_2(2 ,"牛二"),

    NIU_1(1 ,"牛一"),

    NIU_0(0 ,"没牛");

    Integer paiXingCode;

    String desc;

    NiuNiuPaiXingEnum(Integer paiXingCode , String desc){
        this.paiXingCode = paiXingCode;
        this.desc = desc;
    }

    public Integer getPaiXingCode(){
        return paiXingCode;
    }

}
