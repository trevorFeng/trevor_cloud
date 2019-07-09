package com.trevor.common.dao.mysql;

import com.trevor.common.domain.mysql.FriendsManage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 23:59
 **/
@Repository
public interface FriendManageMapper {

    /**
     * 查询玩家是否是房主的好友
     * @param userId
     * @param manageFriendId
     * @return
     */
    Long countRoomAuthFriendAllow(@Param("userId") Long userId, @Param("manageFriendId") Long manageFriendId);

    /**
     * 根据用户id查询管理的好友
     * @param userId
     * @return
     */
    List<FriendsManage> findByUserId(@Param("userId") Long userId);


    /**
     * 申请好友
     * @param userId
     * @param manageFriendId
     */
    void applyFriend(@Param("userId") Long userId, @Param("manageFriendId") Long manageFriendId);

    /**
     * 通过好友申请
     * @param userId
     * @param manageFriendId
     */
    void passFriend(@Param("userId") Long userId, @Param("manageFriendId") Long manageFriendId);

    /**
     * 移除好友
     * @param userId
     * @param manageFriendId
     */
    void removeFriend(@Param("userId") Long userId, @Param("manageFriendId") Long manageFriendId);
}
