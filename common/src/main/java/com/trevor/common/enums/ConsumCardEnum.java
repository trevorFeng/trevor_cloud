package com.trevor.common.enums;

/**
 * @author trevor
 * @date 2019/3/8 16:02
 */
public enum ConsumCardEnum {

    /**
     * 12局，房卡x3
     */
    GAME_NUM_12_CARD_3(1,3 ,"12局，房卡x3"),

    /**
     * 24局，房卡x6
     */
    GAME_NUM_24_CARD_6(2,6 ,"24局，房卡x6");

    ConsumCardEnum(Integer code , Integer consumCardNum , String desc) {
        this.code = code;
        this.consumCardNum = consumCardNum;
        this.desc = desc;
    }

    /**
     * 代表码
     */
    private Integer code;

    /**
     * 房卡消费数量
     */
    private Integer consumCardNum;

    /**
     * m描述
     */
    private String desc;

    public Integer getCode() {
        return code;
    }

    public Integer getConsumCardNum() {
        return consumCardNum;
    }

    public String getDesc() {
        return desc;
    }}
