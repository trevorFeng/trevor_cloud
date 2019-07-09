package com.trevor.common.domain.mysql;

import lombok.Data;

/**
 * 一句话描述该类作用:【房卡消费记录】
 *
 * @author: trevor
 * @create: 2019-03-05 0:20
 **/
@Data
public class CardConsumRecord {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 开房id
     */
    private Long roomId;

    /**
     * 开房人的id（用户id）
     */
    private Long roomAuth;

    /**
     * 消费房卡数量
     */
    private Integer consumNum;

    /**
     * 生成一个房间房卡消费的基本信息
     * @param roomRecordId
     * @param roomAuth
     * @param consumNum
     */
    public void generateCardConsumRecordBase(Long roomRecordId , Long roomAuth ,Integer consumNum){
        this.setRoomId(roomRecordId);
        this.setRoomAuth(roomAuth);
        this.setConsumNum(consumNum);
    }

}
