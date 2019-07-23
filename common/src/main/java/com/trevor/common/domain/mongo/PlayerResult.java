package com.trevor.common.domain.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-12 20:02
 **/
@Data
@Document(collection = "player_result")
public class PlayerResult {


    private String id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 开房的id
     */
    private Long roomId;

    /**
     * 第几局
     */
    private Integer gameNum;

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
    private Integer paiXing;

    /**
     * 倍数
     */
    private Integer beiShu;


    private Long entryTime;
}
