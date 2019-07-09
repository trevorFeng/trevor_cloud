package com.trevor.common.domain.mysql;

import lombok.Data;


/**
 * 一句话描述该类作用:【个人房卡信息】
 *
 * @author: trevor
 * @create: 2019-03-05 0:20
 **/
@Data
public class PersonalCard {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 拥有的房卡数量
     */
    private Integer roomCardNum;
}
