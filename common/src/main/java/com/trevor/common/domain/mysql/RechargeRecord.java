package com.trevor.common.domain.mysql;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 一句话描述该类作用:【房卡充值记录】
 *
 * @author: trevor
 * @create: 2019-03-04 22:56
 **/
@Data
public class RechargeRecord {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 充值房卡数量
     */
    private Integer rechargeCard;

    /**
     * 房卡单价
     */
    private BigDecimal unitPrice;

    /**
     * 本次充值的总价
     */
    private BigDecimal totalPrice;

    /**
     * 充值时间
     */
    private Long time;

}
