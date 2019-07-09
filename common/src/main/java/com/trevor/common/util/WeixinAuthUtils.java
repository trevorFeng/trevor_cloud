package com.trevor.common.util;

import com.alibaba.fastjson.JSON;
import com.trevor.common.bo.WebKeys;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;


@Slf4j
public class WeixinAuthUtils {

    /**
     * 请求微信token的基本url
     */
    private final static String ACCESS_TOKEN_BASE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
            + WebKeys.WEIXIN_APPID + "&secret=" + WebKeys.WEIXIN_APP_SECRET + "&grant_type"+WebKeys.GRANT_TYPE;

    /**
     * 通过refresh_token请求微信token的基本url
     */
    private final static String ACCESS_TOKEN_BASE_URL_BY_REFRESH = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + WebKeys.WEIXIN_APPID +
            "&grant_type=refresh_token";

    /**
     * 请求用户信息基本url
     */
    private final static String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?lang=zh_CN";

    /**
     * 通过code换取网页授权access_token
     */
    public static Map<String, String> getWeixinToken(String code) throws IOException {
        String url = ACCESS_TOKEN_BASE_URL + "&code=" + code;
        String res = HttpUtil.httpGet(url);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        log.info("通过code换取网页授权access_token 返回结果:-------------------------" + resMap.toString());
        return resMap;
    }

    /**
     * 刷新access_token（如果需要）
     * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
     * refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
     */
    public static Map<String, String> getWeixinTokenByRefreshToken(String refresh_token) throws IOException {

        String url = ACCESS_TOKEN_BASE_URL_BY_REFRESH + "&refresh_token=" + refresh_token;
        String res = HttpUtil.httpGet(url);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        System.out.println("刷新access_token 返回结果:---------------------------------" + resMap);
        return resMap;
    }

    /**
     * 拉取用户信息
     */
    public static Map<String, String> getUserInfo(String access_token,String openid) throws IOException {
        String url = USER_INFO_URL + "&openid=" + openid + "&access_token=" + access_token;
        String res = HttpUtil.httpGet(url);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        log.info("拉取用户信息 返回结果:------------------------------------- " + resMap.toString());
        return resMap;
    }


}
