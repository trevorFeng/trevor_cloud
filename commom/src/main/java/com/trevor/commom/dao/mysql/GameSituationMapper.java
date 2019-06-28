package com.trevor.commom.dao.mysql;

import org.apache.ibatis.annotations.Param;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-12 20:51
 **/

public interface GameSituationMapper {

    /**
     * 生成一条记录
     * @param gameResult
     */
    void insertOne(@Param("gameResult") GameResult gameResult);
}
