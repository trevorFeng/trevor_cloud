package com.trevor.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author trevor
 * @date 2019/3/13 17:41
 */
@Data
public class RoomPoke implements Serializable {

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 每一局的玩家的牌
     */
    private volatile List<UserPokesIndex> userPokes = new ArrayList<>(2<<4);

    /**
     * 真正打牌的人的集合，不管是不是已经关闭浏览器重新进来的人
     */
    private volatile List<RealWanJiaInfo> realWanJias = new ArrayList<>(2<<4);

    /**
     * 玩家分数情况
     */
    private List<UserScore> userScores = new ArrayList<>(2<<4);

    /**
     * 默认为0，开到第几局了
     */
    private Integer runingNum = 0;

    /**
     * 总局数
     */
    private Integer totalNum;

    /**
     * 对Set<Session>操作的锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 游戏状态
     */
    private volatile Integer gameStatus;


}
