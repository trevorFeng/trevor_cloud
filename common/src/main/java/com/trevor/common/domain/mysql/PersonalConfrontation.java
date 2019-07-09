package com.trevor.common.domain.mysql;

import lombok.Data;

/**
 * 一句话描述该类作用:【玩家在一间房的对局情况】
 *
 * @author: trevor
 * @create: 2019-03-04 23:21
 **/
@Data
public class PersonalConfrontation {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 玩家的id
     */
    private Long userId;

    /**
     * 开房记录id
     */
    private Long roomId;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 积分情况
     */
    private Integer integralCondition;
}
