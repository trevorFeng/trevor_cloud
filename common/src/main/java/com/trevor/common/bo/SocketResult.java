package com.trevor.common.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
     * 1013--本房间是否结束
     * 1014--摊牌的消息
     * 1015--玩家重新连接的消息
     * ****************************2000以上发给自己
     * 2002--房间内情况，发给新人，数据为其他人的得分，牌，是否抢庄等
     *
     */
    private Integer head;


    private String userId;


    private String name;


    private String pictureUrl;

    /**
     * 分数
     */
    private Integer score;

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
     * 玩家列表
     */
    private List<Player> players;

    /**
     * 玩家的牌
     */
    private Map<String ,List<String>> userPokeMap;

    /**
     * 本局的分数情况
     */
    private Map<String ,Integer> scoreMap;

    /**
     * 是否结束
     */
    private Boolean isOver;

    /**
     * 5/12这样的信息
     */
    private String runingAndTotal;

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

    public SocketResult(Integer head ,Map<String ,List<String>> userPokeMap) {
        this.head = head;
        this.userPokeMap = userPokeMap;
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

    /**
     * 本局结果
     * @param head
     * @param scoreMap
     */
    public SocketResult(Integer head ,Map<String ,Integer> scoreMap ,Boolean isResult){
        this.head = head;
        this.scoreMap = scoreMap;
    }

    /**
     * 是否继续
     * @param head
     */
    public SocketResult(Integer head ,Boolean isOver){
        this.head = head;
        this.isOver = isOver;
    }

}
