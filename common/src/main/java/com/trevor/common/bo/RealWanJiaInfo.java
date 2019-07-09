package com.trevor.common.bo;

import lombok.Data;

import java.util.List;

/**
 * @author trevor
 * @date 06/18/19 12:37
 */
@Data
public class RealWanJiaInfo {

    /**
     * id
     */
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 头像
     */
    private String picture;

    /**
     * 是否是观众，可以参与打牌
     */
    private Boolean isGuanZhong;

    /**
     * 是否已经准备
     */
    private Boolean isReady;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 是否是庄家
     */
    private Boolean isZhuangJia;

    /**
     * 是否抢庄
     */
    private Boolean isQiangZuang;

    /**
     * 玩家的牌
     */
    private List<String> pokes;

    /**
     * 是否离线
     */
    private Boolean isUnconnection;
}
