package com.trevor.common.domain.mysql;

import lombok.Data;

/**
 * 开房记录表(开房基本信息)
 * @author trevor
 * @date 2019/3/4 14:24
 */
@Data
public class Room {

    /**
     * 主键id,房间编号,从10000开始
     */
    private Long id;

    /**
     * 开房时间
     */
    private Long entryTime;

    /**
     * 开房人的id（用户id）
     */
    private Long roomAuth;

    /**
     * 是否激活,0为未激活,1为激活，2为房间使用完成后关闭，3为房间未使用关闭
     */
    private Integer status;

    /**
     * 房间类型 1为13人牛牛，2为10人牛牛，3为6人牛牛 ，4为金花
     */
    private Integer roomType;


    /**
     * 进行到了多少句
     */
    private Integer runingNum;

    /**
     * 总局数
     */
    private Integer totalNum;


    /**
     * 生成一个房间的基本信息
     * @param roomType
     * @return
     */
    public void generateRoomBase(Integer roomType ,Long roomAuth ,Long currentTime ,Integer totalNum){
        this.setRoomType(roomType);
        this.setRoomAuth(roomAuth);
        this.setEntryTime(currentTime);
        this.runingNum = 0;
        this.totalNum = totalNum;
        this.status = 0;
    }

}
