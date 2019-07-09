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
import com.trevor.common.util.WeixinAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trevor
 * @date 2019/3/4 11:38
 */
@Service
@Slf4j
public class WeixinService{

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<String> weixinAuth(String code) throws IOException {
        //获取access_token
        Map<String, String> accessTokenMap = WeixinAuthUtils.getWeixinToken(code);
        //拉取用户信息
        Map<String, String> userInfoMap = WeixinAuthUtils.getUserInfo(accessTokenMap.get(WebKeys.ACCESS_TOKEN)
                ,accessTokenMap.get(WebKeys.OPEN_ID));
        //有可能access token以被使用
        if (userInfoMap.get(WebKeys.ERRCODE) != null) {
            log.error("拉取用户信息 失败啦,快来围观:-----------------" + userInfoMap.get(WebKeys.ERRMSG));
            //刷新access_token
            Map<String, String> accessTokenByRefreshTokenMap = WeixinAuthUtils.getWeixinTokenByRefreshToken(accessTokenMap.get(WebKeys.REFRESH_TOKEN));
            // 再次拉取用户信息
            userInfoMap = WeixinAuthUtils.getUserInfo(accessTokenByRefreshTokenMap.get(WebKeys.ACCESS_TOKEN),
                    accessTokenByRefreshTokenMap.get(WebKeys.OPEN_ID));
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
                user.setOpenid(openid);
                user.setHash(hash);
                user.setAppName(userInfoMap.get("nickname"));
                user.setAppPictureUrl(userInfoMap.get("headimgurl"));
                user.setType(0);
                user.setFriendManageFlag(0);
                userService.insertOne(user);

                //新增用户房卡记录
                PersonalCard personalCard = new PersonalCard();
                personalCard.setRoomCardNum(0);
                personalCard.setUserId(user.getId());
                personalCardMapper.insertOne(personalCard);

                claims.put("openid" ,user.getOpenid());
                claims.put("hash" ,user.getHash());
                claims.put("timestamp" ,System.currentTimeMillis());
            } else {
                //更新头像，昵称，hash
                String hash = RandomUtils.getRandomChars(10);
                User user = new User();
                user.setAppName(userInfoMap.get("nickname"));
                user.setHash(hash);
                user.setAppPictureUrl(userInfoMap.get("headimgurl"));
                userService.updateUser(user);

                claims.put("openid" ,user.getOpenid());
                claims.put("hash" ,user.getHash());
                claims.put("timestamp" ,System.currentTimeMillis());
            }

            String token = TokenUtil.generateToken(claims);
            return ResponseHelper.createInstance(token, MessageCodeEnum.AUTH_SUCCESS);
        }
    }

}
