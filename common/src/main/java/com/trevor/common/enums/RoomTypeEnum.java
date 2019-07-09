package com.trevor.common.enums;

import java.util.Objects;

public enum RoomTypeEnum {

    /**
     * 13人牛牛
     */
    NIU_NIU_13(1 ,13 ,"13人牛牛"),

    /**
     * 10人牛牛
     */
    NIU_NIU_10(2 , 10 ,"10人牛牛");

    /**
     * 房间类型
     */
    private Integer roomType;

    /**
     * 房间人数
     */
    private Integer roomNum;

    /**
     * 房间描述
     */
    private String roomDesc;

    RoomTypeEnum(Integer roomType , Integer roomNum ,String roomDesc){
        this.roomType = roomType;
        this.roomNum = roomNum;
        this.roomDesc = roomDesc;
    }

    public static Integer getRoomNumByType(Integer roomType){
        for (RoomTypeEnum roomTypeEnum : RoomTypeEnum.values()) {
            if (Objects.equals(roomTypeEnum.getRoomType() ,roomType)) {
                return roomTypeEnum.getRoomNum();
            }
        }
        return null;
    }

    public Integer getRoomType() {
        return roomType;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public String getRoomDesc() {
        return roomDesc;
    }
}
