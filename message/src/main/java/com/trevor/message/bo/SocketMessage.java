package com.trevor.message.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 06/28/19 13:30
 */
@Data
public class SocketMessage {

    /**
     * 消息类型
     * 1-准备
     * 2-抢庄消息
     * 3-闲家下注
     * 4-摊牌的消息
     * 5-说话
     */
    private Integer messageCode;

    /**
     * 说话的对应码
     */
    private Integer shuoHuaCode;

    /**
     * 抢庄的倍数
     */
    private Integer qiangZhuangMultiple;

    /**
     * 闲家下注的倍数
     */
    private Integer xianJiaMultiple;

}
