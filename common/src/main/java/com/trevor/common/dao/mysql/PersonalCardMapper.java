package com.trevor.common.dao.mysql;

import com.trevor.common.bo.ReturnCard;
import com.trevor.common.domain.mysql.PersonalCard;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-08 0:14
 **/
@Repository
public interface PersonalCardMapper {

    /**
     * 根据玩家查询玩家拥有的房卡数量
     * @param userId
     * @return
     */
    Integer findCardNumByUserId(@Param("userId") Long userId);

    /**
     * 根据玩家id更新房卡数量
     * @param userId
     * @param card
     */
    void updatePersonalCardNum(@Param("userId") Long userId, @Param("card") Integer card);

    /**
     * 插入一条新纪录
     * @param personalCard
     */
    void insertOne(@Param("personalCard") PersonalCard personalCard);

    /**
     * 批量更新玩家的房卡
     * @param returnCards
     */
    void updatePersonalCardNumByUserIds(@Param("returnCards") List<ReturnCard> returnCards);

    /**
     * 根据玩家id集合查询个人房卡
     * @param userIds
     * @return
     */
    List<PersonalCard> findByUserIds(@Param("userIds") List<Long> userIds);
}
