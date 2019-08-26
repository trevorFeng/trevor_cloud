package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;

import java.util.Objects;

public class CountDownEvent extends Event {

    /**
     * 监听器的key
     */
    private String listenerKey;

    /**
     * 倒计时的时长
     */
    private Integer time;

    public CountDownEvent(Integer time ,String roomId ,String listenerKey) {
        this.time = time;
        super.roomId = roomId;
        this.listenerKey = listenerKey;
    }

    @Override
    protected void executeEvent() {
        if (time != 0) {
            //前端转圈效果不发送倒计时
            if (!listenerKey.startsWith(ListenerKey.ZHUAN_QUAN)) {
                sendCountDown();
            }
            this.time--;
            if (time == 0) {
                //移除监听器
                scheduleDispatch.removeListener(listenerKey);
                //添加事件
                addEvent();
            }
        }
    }


    private void sendCountDown(){
        Integer keyTime = getTimeByKey();
        SocketResult soc = new SocketResult();
        //准备的倒计时
        if (listenerKey.startsWith(ListenerKey.READY)) {
            soc.setHead(1002);
            soc.setCountDown(time);
            if (Objects.equals(time ,keyTime)) {
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_START.getCode());
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_START.getCode());
            }
            if (time == 1) {
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_END.getCode());
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_END.getCode());
            }
         //抢庄的倒计时
        }else if (listenerKey.startsWith(ListenerKey.QIANG_ZHAUNG)) {
            soc.setHead(1020);
            soc.setCountDown(time);
            if (Objects.equals(time ,keyTime)) {
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_START.getCode());
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_START.getCode());
            }
            if (time == 1) {
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_END.getCode());
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_END.getCode());
            }
        }

        messageHandle.broadcast(soc ,roomId);
    }

    private void addEvent(){
        //准备的倒计时
        if (listenerKey.startsWith(ListenerKey.READY)) {
            actuator.addEvent(new FaPai4Event(roomId));
        //抢庄的倒计时
        }else if (listenerKey.startsWith(ListenerKey.QIANG_ZHAUNG)) {
            actuator.addEvent(new SelectZhuangJiaEvent(roomId));
        //转圈
        }else if (listenerKey.startsWith(ListenerKey.ZHUAN_QUAN)) {
            scheduleDispatch.addListener(new CountDownListener(ListenerKey.XIA_ZHU + ListenerKey.SPLIT + ListenerKey.TIME_FIVE));
        }
    }

    /**
     * 得到time
     * @return
     */
    protected Integer getTimeByKey(){
        String str[] = listenerKey.split(ListenerKey.SPLIT);
        return NumberUtil.stringFormatInteger(str[str.length-1]);
    }
}
