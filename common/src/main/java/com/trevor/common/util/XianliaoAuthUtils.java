package com.trevor.common.util;

import com.alibaba.fastjson.JSON;
import com.trevor.common.bo.WebKeys;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;

import java.io.IOException;
import java.util.Map;


@Slf4j
public class XianliaoAuthUtils {

    /**
     * 请求闲聊token的基本url
     */
    private final static String ACCESS_TOKEN_BASE_URL = "https://ssgw.updrips.com/oauth2/access_token";

    /**
     * 请求用户信息基本url
     */
    private final static String USER_INFO_URL = "https://ssgw.updrips.com/resource/user/getUserInfo";

    /**
     * 通过code换取网页授权access_token
     */
    public static Map<String, String> getXianliaoToken(String code) throws IOException {
        String url = ACCESS_TOKEN_BASE_URL;
        FormBody formBody = new FormBody.Builder()
                .add("appid", WebKeys.XIANLIAO_APPID)
                .add("appsecret", WebKeys.XIANLIAO_APP_SECRET)
                .add("grant_type", WebKeys.GRANT_TYPE)
                .add("code" ,code)
                .build();
        String res = HttpUtil.httpPost(url ,formBody);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        log.info("通过code换取网页授权access_token 返回结果:-------------------------" + resMap.toString());
        return resMap;
    }

    /**
     * 刷新access_token（如果需要）
     * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
     * refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
     */
    public static Map<String, String> getXianliaoTokenByRefreshToken(String refresh_token) throws IOException {
        String url = ACCESS_TOKEN_BASE_URL;
        FormBody formBody = new FormBody.Builder()
                .add("appid", WebKeys.XIANLIAO_APPID)
                .add("appsecret", WebKeys.XIANLIAO_APP_SECRET)
                .add("grant_type", WebKeys.GRANT_TYPE)
                .add("refresh_token" ,refresh_token)
                .build();
        String res = HttpUtil.httpPost(url ,formBody);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        System.out.println("刷新access_token 返回结果:---------------------------------" + resMap);
        return resMap;
    }

    /**
     * 拉取用户信息
     */
    public static Map<String, String> getUserInfo(String access_token) throws IOException {
        String url = USER_INFO_URL;
        FormBody formBody = new FormBody.Builder()
                .add(WebKeys.ACCESS_TOKEN ,access_token)
                .build();
        String res = HttpUtil.httpPost(url ,formBody);
        Map<String, String> resMap = JSON.parseObject(res ,Map.class);
        log.info("拉取用户信息 返回结果:------------------------------------- " + resMap.toString());
        return resMap;
    }


}
