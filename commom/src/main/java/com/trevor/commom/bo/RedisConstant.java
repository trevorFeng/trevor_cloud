package com.trevor.commom.bo;

/**
 * @author trevor
 * @date 06/28/19 13:52
 */
public class RedisConstant {

    /**-----------------------------------------------------------------  redis key  -------------------------------------------------------------**/

    /**
     * 基本房间信息
     */
    public static final String BASE_ROOM_INFO = "baseRoomInfo_";

    /**
     * 玩家的消息队列
     */
    public static final String MESSAGES_QUEUE = "MessageQueue_";

    /**
     * 房间里的玩家
     */
    public static final String ROOM_PLAYER = "roomPlayer_";

    /**
     * 真正的玩家，不包括观众
     */
    public static final String REAL_ROOM_PLAYER = "realRoomPlayer_";

    /**
     * 玩家的牌
     */
    public static final String POKES = "pokes_";

    /**
     * 准备的玩家
     */
    public static final String READY_PLAYER = "readyPlayer_";

    /**
     * 抢庄的玩家
     */
    public static final String QIANGZHAUNG = "qiangZhuang_";

    /**
     * 庄家是哪个玩家
     */
    public static final String ZHUANGJIA = "zhuangJia_";

    /**
     * 摊牌
     */
    public static final String TANPAI = "tanPai_";

    /**
     * 玩家下注
     */
    public static final String XIANJIA_XIAZHU = "xianJiaXiaZhu_";

    /**
     * 玩家的得分
     */
    public static final String SCORE = "score_";

    /**
     * 玩家的总分
     */
    public static final String TOTAL_SCORE = "totalScore_";


    /**-----------------------------------------------------------------  hash key  -------------------------------------------------------------**/

    /**
     * 房价类型
     */
    public final static String ROOM_TYPE = "roomType";

    /**
     *
     */
    public final static String ROB_ZHUANG_TYPE = "robZhuangType";

    public final static String BASE_POINT = "basePoint";

    public final static String RULE = "rule";

    public final static String XIAZHU = "xiazhu";

    public final static String SPECIAL = "special";

    public final static String PAIXING = "paiXing";

    public final static String GAME_STATUS = "gameStatus";

    public final static String RUNING_NUM = "runingNum";

    public final static String TOTAL_NUM = "totalNum";
}
