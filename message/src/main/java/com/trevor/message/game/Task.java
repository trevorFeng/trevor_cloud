package com.trevor.message.game;

import lombok.Data;

@Data
public class
Task {

    /**
     * 游戏类型
     */
    private String gameType;

    /**
     * 游戏的任务标志
     */
    private String flag;

    /**
     * 房间id
     */
    private String roomId;

    /**
     * 玩家id
     */
    private String playId;

    /**
     * 搶莊的倍數
     */
    private Integer qiangZhuangBeiShu;

    /**
     * 下注的倍數
     */
    private Integer xiaZhuBeiShu;

}
