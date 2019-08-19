package com.trevor.common.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trevor
 * @date 06/27/19 18:23
 */
@Data
public class SocketResult {

    /**
     * 400--表示token错误
     * 404--根据token找不到user
     * 500--表示重复登陆了，需要下线一个客户端
     * 501--不在规定的时间内干不该干的事
     * 502--表示不是真正的玩家准备
     * 503--表示没有准备的玩家发来了抢庄的消息
     * 504--表示没有准备的玩家发来了下注的消息
     * 505--表示庄家发来了下注的消息
     * 506--表示房间已关闭
     * 507--表示房间未找到
     * 508--不是房主的好友
     * 509--房间满员
     * ****************************1000-2000内发给所有人
     * 1000--新人加入，发给所有人，数据为新人的情况
     * 1001--玩家掉线的消息，数据为玩家id
     * 1002--准备的倒计时,数据为数字
     * 1003--某个玩家准备的消息，数据为玩家id
     * 1004--发牌的消息，userPokeMap为数据
     * 1005--玩家下注倒计时
     * 1006--选取庄家的消息,数据为庄家id
     * 1007--闲家下注倒计时
     * 1008--发一张牌
     * 1009--摊牌倒计时
     * 1010--某个玩家抢庄的消息
     * 1011--某个玩家下注的消息
     * 1012--本局的结果
     * 1013--本房间结束
     * 1014--摊牌的消息
     * 1015--玩家重新连接的消息
     * 1016--继续下一句
     * 1017--说话
     * 1018--切换为观战
     * ****************************2000以上发给自己
     * 2002--房间内情况，发给新人，数据为其他人的得分，牌，是否抢庄等
     *
     */
    private Integer head;

    private Integer shuoHuaCode;


    private String userId;


    private String name;


    private String pictureUrl;

    /**
     * 分数
     */
    private String totalScore;

    /**
     * 观众，玩家可以参与打牌
     */
    private Boolean isGuanZhong;

    /**
     * 吃瓜群众，全程不能参与打牌
     */
    private Boolean isChiGuaPeople;

    /**
     * 倒计时
     */
    private Integer countDown;


    /**
     * 抢庄倍数
     */
    private Integer qiangZhuangBeiShu;

    /**
     * 下注倍数
     */
    private Integer xiaZhuBeiShu;


    /**
     * 用于控制前端抢庄的转圈的显示效果
     */
    private Set<String> zhuanQuanPlayers;


    /**
     * 玩家列表
     */
    private List<Player> players;

    /**
     * 已经掉线的玩家
     */
    private Set<String> disConnectionPlayerIds;

    /**
     * 已经点击准备的玩家
     */
    private Set<String> readyPlayerIds;

    /**
     * 玩家的4张牌
     */
    private List<String> userPokeList_4;

    /**
     * 玩家的5张牌
     */
    private Map<String ,List<String>> userPokeMap_5;

    /**
     * 抢庄的玩家
     */
    private Map<String ,String> qiangZhuangMap;

    /**
     * 庄家的id
     */
    private String zhuangJiaUserId;

    /**
     * 闲家下注的相关信息
     */
    private Map<String ,String> xianJiaXiaZhuMap;

    /**
     * 摊牌的玩家
     */
    private Set<String> tanPaiPlayerUserIds;


    /**
     * 本局的分数情况
     */
    private Map<String ,Integer> scoreMap;

    /**
     * 玩家牌型
     */
    private Map<String ,Integer> paiXing;

    /**
     * 5/12这样的信息
     */
    private String runingAndTotal;

    /**
     * 游戏状态
     * 1-----进入房间-准备倒计时前或者倒计时-发4张牌前
     * 2-----发4张牌-确定庄家前
     * 3-----确定庄家-再发一张牌前
     * 4-----再发一张牌前-
     * 5-----再发一张牌-摊牌倒计时前
     * 6-----摊牌倒计时-摊牌倒计时结束
     */
    private Integer gameStatus;

    public SocketResult(){}


    public SocketResult(Integer head){
        this.head = head;
    }

    public SocketResult(Integer head ,Integer countDown) {
        this.head = head;
        this.countDown = countDown;
    }

    public SocketResult(Integer head ,String userId) {
        this.head = head;
        this.userId = userId;
    }

    public SocketResult(Integer head ,List<String> userPokeList_4) {
        this.head = head;
        this.userPokeList_4 = userPokeList_4;
    }

    public SocketResult(Integer head ,Map<String ,List<String>> userPokeMap_5) {
        this.head = head;
        this.userPokeMap_5 = userPokeMap_5;
    }

    public  SocketResult(Integer head ,String userId ,Integer qiangZhuangBeiShu){
        this.head = head;
        this.userId = userId;
        this.qiangZhuangBeiShu = qiangZhuangBeiShu;
    }

    public  SocketResult(Integer head ,String userId ,Integer xiaZhuBeiShu ,Boolean isXiaZhu){
        this.head = head;
        this.userId = userId;
        this.xiaZhuBeiShu = xiaZhuBeiShu;
    }

}
