package com.trevor.commom.bo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:53
 **/
@Data
@ApiModel
public class RechargeCard {

    /**
     * 被充值玩家的id
     */
    @ApiModelProperty("被充值玩家的id")
    private Long userId;

    /**
     * 充值的数量
     */
    @ApiModelProperty("充值的数量")
    private Integer cardNum;

    /**
     * 房卡单价
     */
    @ApiModelProperty("房卡单价")
    private BigDecimal unitPrice;

    /**
     * 本次充值的总价
     */
    @ApiModelProperty("本次充值的总价")
    private BigDecimal totalPrice;
}
