package com.trevor.message.bo;

import com.trevor.commom.enums.MessageCodeEnum;
import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\17 0017 22:32
 * @Description:
 */
@Data
public class ReturnMessage<T> {

    /**
     * 消息类型,负数为错误
     * -1-错误,data为错误信息(string)
     * 2-别的玩家准备的消息，data为准备玩家id
     * 3-准备的倒计时
     * 4-发4张牌 data的为数据格式：{}
     * 5-庄家确定
     * 6-再发一张牌
     * 7-得分结果和没有摊牌玩家的牌
     * 8-给别人发抢庄的消息
     * 9-给被人发闲家下注的消息
     * 10-摊牌消息
     *
     * 11-抢庄倒计时
     *
     * 20-关闭浏览器自己重新进来的消息 ,data为玩家列表
     * 21-关闭浏览器别人重新进来的消息 ,data为玩家的id
     * 22-掉线（别的玩家关闭浏览器）的消息 ,data为玩家的id
     * 23-断线重连的消息，给
     * 24-吃瓜群众加入房间的消息,只给自己发消息，并且不给自己的信息，只给座位上的人的信息，data为真正玩家的列表
     * 25-真正的玩家第一次加入房间给自己发的消息,data为其余玩家的列表
     * 26-真正的玩家第一次加入房间给别人发的消息，data为新加入玩家的信息
     */
    private Integer messageCode;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 消息
     */
    private T data;

    /**
     * 错误消息构造器
     * @param messageCodeEnum
     */
    public ReturnMessage(MessageCodeEnum messageCodeEnum){
        this.messageCode = messageCodeEnum.getCode();
        this.message = messageCodeEnum.getMessage();
    }

    /**
     * 正确消息构造器
     * @param t
     * @param messageCode
     */
    public ReturnMessage(T t , Integer messageCode){
        this.data = t;
        this.messageCode = messageCode;
    }
}
