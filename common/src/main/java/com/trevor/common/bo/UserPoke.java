package com.trevor.common.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trevor
 * @date 2019/3/19 09:53
 */
@Data
public class UserPoke {

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 玩家本局的poke牌
     */
    private List<String> pokes = new ArrayList<>(2<<3);

    /**
     * 牌型
     */
    private String paiXing;

    /**
     * 本局的分数增减
     */
    private Integer thisScore = 0;

    /**
     * 抢庄倍数
     */
    private volatile Integer qiangZhuangMultiple = 1;

    /**
     * 是否抢庄
     */
    private volatile Boolean isQiangZhuang = false;

    /**
     * 是否已经摊牌
     */
    private volatile Boolean isTanPai = false;

    /**
     * 是否是庄家
     */
    private volatile Boolean isZhuangJia = false;

    /**
     * 闲家下注的倍数，默认为1倍
     */
    private volatile Integer xianJiaMultiple = 1;
}
