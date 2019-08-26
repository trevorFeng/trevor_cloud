package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;

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
            sendCountDown();
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
        Integer head = 0;
        if (Objects.equals(listenerKey , ListenerKey.READY)) {
            head = 1002;
        }else if (Objects.equals(listenerKey , ListenerKey.QIANG_ZHAUNG)) {
            head = 1020;
        }
        SocketResult socketResult = new SocketResult(head ,time);
        if (time == 5) {
            messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_START.getCode());
            socketResult.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_START.getCode());
        }
        if (time == 1) {
            socketResult.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_END.getCode());
            messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_END.getCode());
        }
        messageHandle.broadcast(socketResult ,roomId);
    }

    private void addEvent(){
        if (Objects.equals(listenerKey , ListenerKey.READY)) {
            actuator.addEvent(new FaPai4Event(roomId));
        }else if (Objects.equals(listenerKey , ListenerKey.QIANG_ZHAUNG)) {

        }
    }
}
