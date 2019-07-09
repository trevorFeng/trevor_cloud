package com.trevor.common.util;

import okhttp3.*;

import java.io.IOException;

/**
 * @author trevor
 * @date 2019/3/4 12:16
 */
public class HttpUtil {

    /**
     * 发送get请求，并将返回的json字符串转化为对象
     * @param url 请求的url
     * @return
     * @throws IOException
     */
    public static String httpGet(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        return responseString;
    }

    /**
     * 发送post请求，并将返回的json字符串转化为对象
     * @param url 请求的url
     * @return
     * @throws IOException
     */
    public static String httpPost(String url ,FormBody formBody) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        //post方式提交的数据
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        return responseString;
    }
}
