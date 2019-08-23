package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.SocketResult;
import com.trevor.message.core.event.Event;

public class CountDownEvent extends Event {

    private Integer time;

    private String roomId;

    public CountDownEvent(Integer time ,String roomId) {
        this.time = time;
        this.roomId = roomId;
    }

    @Override
    protected void executeEvent() {

        SocketResult socketResult = new SocketResult(1002 ,time);
        messageHandle.broadcast(socketResult ,roomId);
        this.time--;
        if (time == 0) {
            scheduleDispatch.removeListener(key);
            //改变房间状态

            //注册发4张牌事件
            actuator.addEvent(new FaPai4Event());

        }
    }
}
