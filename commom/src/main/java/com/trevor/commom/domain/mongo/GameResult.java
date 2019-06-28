package com.trevor.commom.domain.mongo;

import lombok.Data;

/**
 * 一句话描述该类作用:【每间房的对局情况】
 *
 * @author: trevor
 * @create: 2019-03-04 23:39
 **/
@Data
public class GameResult {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 开房的id
     */
    private Long roomId;

    /**
     * 第几局
     */
    private Integer gameNum;

    /**
     * 对局情况，为json字符串
     */
    private String gameSituation;



}
