package com.trevor.common.bo;

import lombok.Data;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-12 20:02
 **/
@Data
public class NiuniuSituation {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 本局得分情况
     */
    private Integer score;

    /**
     * 是否是庄家
     */
    private Boolean isZhuangJia;

    /**
     * 算上本局后的总分
     */
    private Integer totalScore;

    /**
     * 用户本局的牌
     */
    private List<String> pokes;

    /**
     * 牛牛或牛一
     */
    private String pokesDesc;

    /**
     * 倍数
     */
    private Integer beiShu;
}
