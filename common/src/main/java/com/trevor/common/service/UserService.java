package com.trevor.common.service;

import com.trevor.common.bo.Authentication;
import com.trevor.common.bo.WebKeys;
import com.trevor.common.dao.mysql.UserMapper;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.util.ObjectUtil;
import com.trevor.common.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * token合法性检查
     *
     * @param token
     * @throws IOException
     */
    public User getUserByToken(String token) {
        try {
            Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
            String openid = (String) claims.get(WebKeys.OPEN_ID);
            String hash = (String) claims.get("hash");
            Long timestamp = (Long) claims.get("timestamp");
            if (openid == null || hash == null || timestamp == null) {
                return null;
            }
            User user = findUserByOpenid(openid);
            if (user == null || !Objects.equals(user.getHash(), hash)) {
                return null;
            }
            return user;
        }catch (Exception e) {
            log.error("解析token错误，token：" + token);
            return null;
        }

    }

    /**
     * 查询玩家是否开启好友管理功能,1为是，0为否
     * @param userId
     * @return
     */
    public Boolean isFriendManage(Long userId) {
        Integer userFriendManage = userMapper.isFriendManage(userId);
        if (Objects.equals(1 ,userFriendManage)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据openid查找用户是否存在
     * @param openid
     * @return
     */
    public Boolean isExistByOpnenId(String openid) {
        Long existByOpnenId = userMapper.isExistByOpnenId(openid);
        if (Objects.equals(existByOpnenId ,0L)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 新增一个用户
     * @param user
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertOne(User user) {
        userMapper.insertOne(user);
    }


    /**
     * 根据微信id查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    public User findUserByOpenid(String openid) {
        return userMapper.findUserByOpenid(openid);
    }



    /**
     * 更新user
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * 根据phoneNum查找用户是否存在
     * @param phoneNum
     * @return
     */
    public Boolean isExistByPhoneNum(String phoneNum) {
        Long existByPhoneNum = userMapper.isExistByPhoneNum(phoneNum);
        if (Objects.equals(existByPhoneNum ,0L)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 根据phoneNum查询WebSessionUser
     * @param phoneNum
     * @return
     */
    public User getUserByPhoneNumContainOpenidAndHash(String phoneNum) {
        User user = userMapper.findUserByPhoneNumContainOpenidAndHash(phoneNum);
        return user;
    }

    /**
     * 根据用户id绑定手机号
     * @param userId
     * @param phoneNum
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePhoneByUserId(Long userId, String phoneNum) {
        User user = new User();
        user.setId(userId);
        user.setPhoneNumber(phoneNum);
        userMapper.updateUser(user);
    }


    /**
     * 实名认证
     * @param authentication
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void realNameAuth(Long userId, Authentication authentication) {
        User user = new User();
        user.setId(userId);
        user.setIdCard(authentication.getIdCard());
        user.setRealName(authentication.getRealName());
        userMapper.updateUser(user);
    }

    /**
     * 退出登录
     * @param userId
     */
    public void loginOut(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setHash("loginOut");
        userMapper.updateUser(user);
    }

    public List<User> findUsersByIds(List<Long> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userMapper.findUsersByIds(ids);
    }
}
