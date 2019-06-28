package com.trevor.commom.domain.mysql;

import lombok.Data;

/**
 * @author trevor
 * @date 05/14/19 18:18
 */
@Data
public class RoomPokeInit {

    private Long id;

    /**
     * 房间id
     */
    private Long roomRecordId;

    /**
     * 每一局的玩家的牌
     */
    private String userPokes;

    /**
     * 玩家的分数
     */
    private String userScores;

    /**
     * 默认为0，开到第几局了
     */
    private Integer runingNum = 0;

    /**
     * 总局数
     */
    private Integer totalNum;

    /**
     * 是否激活,0为未激活,1为激活，2为房间使用完成后关闭，3为房间未使用关闭
     */
    private Integer status;

    /**
     * 进入时间
     */
    private Long entryDate;
}
