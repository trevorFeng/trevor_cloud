package com.trevor.common.domain.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-05 22:42
 **/
@Data
@Document(collection = "niuniu_room_param")
public class NiuniuRoomParam {


    private String id;

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 1为13人牛牛，2为10人牛牛，3为6人牛牛
     */
    private Integer roomType;

    /**
     * 1位明牌抢庄，2为通比开船，3为固定抢庄，4为牛牛上庄
     */
    private Integer robZhuangType;

    /**
     * 底分，取值为1-5
     */
    private Integer basePoint;

    /**
     * 规则
     * 1---牛牛x3，牛九x2，牛八x2
     * 2---牛牛x4，牛九x3，牛八x2，牛7x2
     */
    private Integer rule;

    /**
     * 下注
     * 1---可下1，2，3，5倍
     * 2---可下1，3，5，8倍
     */
    private Integer xiazhu;

    /**
     * 特殊
     * 1---仅限好友
     * 2---允许观战
     * 3---癞子牛牛
     * 4---允许搓牌
     *
     */
    private Set<Integer> special;

    /**
     * 1---顺子牛，5倍
     * 2---五花牛，6倍
     * 3---同花牛，6倍
     * 4---葫芦牛，7倍
     * 5---炸弹牛，8倍
     * 6---五小牛，10倍
     */
    private Set<Integer> paiXing;

    /**
     * 局数
     * 1---12局，房卡x3
     * 2---24局，房卡x6
     */
    private Integer consumCardNum;
}
