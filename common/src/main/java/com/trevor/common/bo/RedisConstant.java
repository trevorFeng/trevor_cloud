package com.trevor.common.bo;

/**
 * @author trevor
 * @date 06/28/19 13:52
 */
public class RedisConstant {

    /**-----------------------------------------------------------------  redis key  -------------------------------------------------------------**/

    public static String getRuningNum(String roomId) {
        return RUNING_NUM + roomId;
    }

    public static String getRoomPlayer(String roomId) {
        return ROOM_PLAYER + roomId;
    }

    public static String getDisConnection(String roomId) {
        return DIS_CONNECTION + roomId;
    }

    public static String getRealRoomPlayer(String roomId) {
        return REAL_ROOM_PLAYER + roomId;
    }

    public static String getGuanZhong(String roomId) {
        return GUAN_ZHONG + roomId;
    }

    public static String getTotalScore(String roomId) {
        return TOTAL_SCORE + roomId;
    }

    public static String getGameStatus(String roomId ,String runingNum){
        return GAME_STATUS + roomId + "_" + runingNum;
    }

    public static String getPokes(String roomId ,String runingNum) {
        return POKES + roomId + "_" + runingNum;
    }

    public static String getReadyPlayer(String roomId ,String runingNum) {
        return READY_PLAYER + roomId + "_" + runingNum;
    }

    public static String getQiangZhuang(String roomId ,String runingNum) {
        return QIANG_ZHUANG + roomId + "_" + runingNum;
    }

    public static String getZhuangJia(String roomId ,String runingNum) {
        return ZHUANG_JIA + roomId + "_" + runingNum;
    }

    public static String getTanPai(String roomId ,String runingNum) {
        return TAN_PAI + roomId + "_" + runingNum;
    }

    public static String getXianjiaXiazhu(String roomId ,String runingNum) {
        return XIANJIA_XIAZHU + roomId + "_" + runingNum;
    }

    public static String getScore(String roomId ,String runingNum) {
        return SCORE + roomId + "_" + runingNum;
    }

    public static String getPaiXing(String roomId ,String runingNum) {
        return PAI_XING + roomId + "_" + runingNum;
    }

    public static String getBaseRoomInfo(String roomId) {
        return BASE_ROOM_INFO + roomId;
    }


    /**
     * 房间状态 value
     */
    public static final String GAME_STATUS = "gameStatus_";


    /**
     * 目前运行的局数value
     */
    public final static String RUNING_NUM = "runingNum_";

    /**
     * 玩家的消息队列(list)
     */
    public static final String MESSAGES_QUEUE = "MessageQueue_";

    /**
     * 房间里的玩家(set)
     */
    public static final String ROOM_PLAYER = "roomPlayer_";

    /**
     * 真正玩家断线的的(set)
     */
    public static final String DIS_CONNECTION = "disConnection_";

    /**
     * 真正的玩家，不包括观众(set)
     */
    public static final String REAL_ROOM_PLAYER = "realRoomPlayer_";

    /**
     * 观众(set)
     */
    public static final String GUAN_ZHONG = "guanZhong_";

    /**
     * 玩家的总分(hash)
     */
    public static final String TOTAL_SCORE = "totalScore_";

    /**
     * 玩家的牌(hash)
     */
    public static final String POKES = "pokes_";

    /**
     * 准备的玩家(set)
     */
    public static final String READY_PLAYER = "readyPlayer_";

    /**
     * 抢庄的玩家(hash)
     */
    public static final String QIANG_ZHUANG = "qiangZhuang_";

    /**
     * 庄家是哪个玩家 value
     */
    public static final String ZHUANG_JIA = "zhuangJia_";

    /**
     * 摊牌(set)
     */
    public static final String TAN_PAI = "tanPai_";

    /**
     * 玩家下注(hash)
     */
    public static final String XIANJIA_XIAZHU = "xianJiaXiaZhu_";

    /**
     * 玩家的得分(hash)
     */
    public static final String SCORE = "score_";

    /**
     * 玩家的牌型(hash)
     */
    public static final String PAI_XING = "paiXing_";

    /**-----------------------------------------------------------------  hash key  -------------------------------------------------------------**/

    /**--------------------------------------------- 房间基本信息 ---------------------------------------**/
    /**
     * 基本房间信息(hash)
     */
    public static final String BASE_ROOM_INFO = "baseRoomInfo_";


    /**
     * 房价类型
     */
    public final static String ROOM_TYPE = "roomType";

    /**
     * 抢庄类型
     */
    public final static String ROB_ZHUANG_TYPE = "robZhuangType";

    /**
     * 基本分数
     */
    public final static String BASE_POINT = "basePoint";

    /**
     * 规则
     */
    public final static String RULE = "rule";

    /**
     * 下注类型
     */
    public final static String XIAZHU = "xiazhu";

    /**
     * 特殊
     */
    public final static String SPECIAL = "special";

    /**
     * 牌型类型
     */
    public final static String PAIXING = "paiXing";

    /**
     * 总局数
     */
    public final static String TOTAL_NUM = "totalNum";

}
