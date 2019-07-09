package com.trevor.auth.service;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.bo.WebKeys;
import com.trevor.common.dao.mysql.PersonalCardMapper;
import com.trevor.common.domain.mysql.PersonalCard;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.UserService;
import com.trevor.common.util.RandomUtils;
import com.trevor.common.util.TokenUtil;
import com.trevor.common.util.XianliaoAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
@Service
@Slf4j
public class XianliaoService{

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 根据code获取闲聊用户基本信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<String> weixinAuth(String code) throws IOException {
        //获取access_token
        Map<String, String> accessTokenMap = XianliaoAuthUtils.getXianliaoToken(code);
        //拉取用户信息
        Map<String, String> userInfoMap = XianliaoAuthUtils.getUserInfo(accessTokenMap.get(WebKeys.ACCESS_TOKEN));
        //有可能access token以被使用
        if (Objects.equals(WebKeys.SUCCESS ,userInfoMap.get(WebKeys.ERRMSG))) {
            log.error("拉取用户信息 失败啦,快来围观:-----------------" + userInfoMap.get(WebKeys.ERRMSG));
            //刷新access_token
            Map<String, String> accessTokenByRefreshTokenMap = XianliaoAuthUtils.getXianliaoTokenByRefreshToken(accessTokenMap.get(WebKeys.REFRESH_TOKEN));
            // 再次拉取用户信息
            userInfoMap = XianliaoAuthUtils.getUserInfo(accessTokenByRefreshTokenMap.get(WebKeys.ACCESS_TOKEN));
        }
        String openid = userInfoMap.get(WebKeys.OPEN_ID);
        if (openid == null) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.AUTH_FAILED);
        } else {
            //判断用户是否存在
            Boolean isExist = userService.isExistByOpnenId(openid);
            Map<String,Object> claims = new HashMap<>(2<<4);
            if (!isExist) {
                //新增
                String hash = RandomUtils.getRandomChars(10);
                User user = new User();
                user.setOpenid(userInfoMap.get(WebKeys.OPEN_ID));
                user.setAppName(userInfoMap.get("nickName"));
                user.setAppPictureUrl(userInfoMap.get("smallAvatar"));
                user.setHash(hash);
                user.setType(1);
                user.setFriendManageFlag(0);
                userService.insertOne(user);
                //新增用户房卡记录
                PersonalCard personalCard = new PersonalCard();
                personalCard.setUserId(user.getId());
                personalCard.setRoomCardNum(0);
                personalCardMapper.insertOne(personalCard);

                claims.put("hash" ,user.getHash());
                claims.put("openid" ,user.getOpenid());
                claims.put("timestamp" ,System.currentTimeMillis());
            } else {
                //更新头像，昵称，hash
                String hash = RandomUtils.getRandomChars(10);
                User user = new User();
                user.setAppName(userInfoMap.get("nickName"));
                user.setHash(hash);
                user.setAppPictureUrl(userInfoMap.get("smallAvatar"));
                userService.updateUser(user);

                claims.put("hash" ,user.getHash());
                claims.put("openid" ,user.getOpenid());
                claims.put("timestamp" ,System.currentTimeMillis());
            }
            String token = TokenUtil.generateToken(claims);
            return ResponseHelper.createInstance(token ,MessageCodeEnum.AUTH_SUCCESS);
        }
    }

}
