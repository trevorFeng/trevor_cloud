package com.trevor.commom.bo;

import lombok.Data;

import java.util.List;

/**
 * @author trevor
 * @date 06/28/19 12:40
 */
@Data
public class Player {

    private Long userId;

    private String name;

    private String pictureUrl;

    /**
     * 观众，玩家可以参与打牌
     */
    private Boolean isGuanZhong;

    /**
     * 吃瓜群众，全程不能参与打牌
     */
    private Boolean isChiGuaPeople;

    /**
     * 总分
     */
    private Integer score;

}
