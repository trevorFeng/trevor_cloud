package com.trevor.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class GetMessageCodeUtil {

    private static final String QUERY_PATH = "https://api.miaodiyun.com/20150822/industrySMS/sendSMS";

    private static final String ACCOUNT_SID = "9f64db84a48c4357a2cafb738fc5fd97";

    private static final String AUTH_TOKEN = "af0fb5b3ff2d47e986f28773dd879fb9";


    /**
     * 根据相应的手机号发送验证码
     * @param phone
     * @return
     */
    public static String getCode(String phone) {
        String rod = RandomUtils.getRandNum();
        String timestamp = getTimestamp();
        String sig = getMD5(ACCOUNT_SID, AUTH_TOKEN, timestamp);
        String tamp = "【测试专用】尊敬的用户，您的验证码为" + rod;
        OutputStreamWriter out = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(QUERY_PATH);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 设置是否允许数据写入
            connection.setDoInput(true);
            // 设置是否允许参数数据输出
            connection.setDoOutput(true);
            // 设置链接响应时间
            connection.setConnectTimeout(5000);
            // 设置参数读取时间
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            // 提交请求
            out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            String args = getQueryArgs(ACCOUNT_SID, tamp, phone, timestamp, sig, "JSON");
            out.write(args);
            out.flush();
            // 读取返回参数
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String temp = "";
            while ((temp = br.readLine()) != null) {
                result.append(temp);
            }
        } catch (Exception e) {
            log.error("验证码发送失败");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject json = JSONObject.parseObject(result.toString());
        String respCode = json.getString("respCode");
        String defaultRespCode = "00000";
        if (defaultRespCode.equals(respCode)) {
            return rod;
        } else {
            return defaultRespCode;
        }
    }

    /**
     * 定义一个请求参数拼接方法
     * @param accountSid
     * @param smsContent
     * @param to
     * @param timestamp
     * @param sig
     * @param respDataType
     * @return
     */
    public static String getQueryArgs(String accountSid, String smsContent, String to, String timestamp, String sig,
                                      String respDataType) {
        return "accountSid=" + accountSid + "&smsContent=" + smsContent + "&to=" + to + "&"+"timestamp=" + timestamp
                + "&sig=" + sig + "&respDataType=" + respDataType;
    }

    /**
     * 获取时间戳
     * @return
     */
    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * sing签名
     * @param sid
     * @param token
     * @param timestamp
     * @return
     */
    public static String getMD5(String sid, String token, String timestamp) {

        StringBuilder result = new StringBuilder();
        String source = sid + token + timestamp;
        // 获取某个类的实例
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 要进行加密的东西
            byte[] bytes = digest.digest(source.getBytes());
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    result.append("0" + hex);
                } else {
                    result.append(hex);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result.toString();
    }



}
