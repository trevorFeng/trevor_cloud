package com.trevor.commom.dao;

import com.trevor.commom.domain.GameSituation;
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
     * @param gameSituation
     */
    void insertOne(@Param("gameSituation") GameSituation gameSituation);
}
