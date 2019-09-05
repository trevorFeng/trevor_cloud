package com.trevor.message.core;

public class ListenerKey {

    public static String SPLIT = "_";

    public static String TIME_FIVE = "5";

    public static String TIME_TWO = "2";

    public static String READY = "ready";

    public static String QIANG_ZHAUNG = "qiangZhuang";

    public static String ZHUAN_QUAN = "zhuanQuan";

    public static String XIA_ZHU = "xiaZhu";

    public static String TAI_PAI = "tanPai";

    /**
     *  结算
     */
    public static String SETTLE = "settle";

    /**
     *
     * @param key
     * @param roomId
     * @param runingNum
     * @param time
     * @return
     */
    public static String getListenerKey(String key ,String roomId , String runingNum ,Integer time){
        return key + SPLIT + roomId + SPLIT + runingNum + SPLIT + time;
    }

    public static String getReadyKey(String roomId , String runingNum ,Integer time){return READY + SPLIT + roomId + SPLIT + runingNum + SPLIT + time;}

    public static String getQiangZhaungKey(String roomId , String runingNum ,String time){return QIANG_ZHAUNG + SPLIT + roomId + SPLIT + runingNum + SPLIT + time;}


}
