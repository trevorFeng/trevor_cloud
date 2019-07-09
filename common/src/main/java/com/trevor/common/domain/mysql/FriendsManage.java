package com.trevor.common.domain.mysql;

import lombok.Data;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 14:08
 **/
@Data
public class FriendsManage {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 管理的好友id
     */
    private Long manageFriendId;

    /**
     * 1未通过 ，0为未通过
     */
    private Integer allowFlag;
}
