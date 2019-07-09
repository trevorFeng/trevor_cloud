package com.trevor.common.dao.mysql;

import com.trevor.common.domain.mysql.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 14:13
 **/
@Repository
public interface UserMapper {

    /**
     * 查询玩家是否开启好友管理功能,1为是，0为否
     * @param userId
     * @return
     */
    Integer isFriendManage(@Param("userId") Long userId);

    /**
     * 根据openid查找用户是否存在
     * @param openid
     * @return
     */
    Long isExistByOpnenId(@Param("openid") String openid);

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    User findUserByOpenid(@Param("openid") String openid);


    /**
     * 新增一个用户
     * @param user
     */
    Long insertOne(@Param("user") User user);

    /**
     * 更新user
     * @param user
     */
    void updateUser(@Param("user") User user);

    /**
     * 根据手机号查询用户是否存在
     * @param phoneNum
     * @return
     */
    Long isExistByPhoneNum(@Param("phoneNum") String phoneNum);

    /**
     * 根据phoneNum查询用户，包含openid和hash字段
     * @param phoneNum
     * @return
     */
    User findUserByPhoneNumContainOpenidAndHash(@Param("phoneNum") String phoneNum);

    /**
     * 根据id集合查询用户
     * @param ids
     * @return
     */
    List<User> findUsersByIds(@Param("ids") List<Long> ids);

}
