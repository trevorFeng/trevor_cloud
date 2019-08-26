package com.trevor.message.core.listener.niuniu;

import com.trevor.message.core.event.niuniu.CountDownEvent;
import com.trevor.message.core.listener.AbstractTaskListener;

/**
 * 倒计时
 */
public class CountDownListener extends AbstractTaskListener {

    /**
     * ready_roomId_time
     * qiangZhung_roomId_time
     * xiaZhu_roomId_time
     * tanPai_roomId_time
     * zhaunQuan_roomId_time
     */
    private String key;

    public CountDownListener(String key) {
        this.key = key;
    }

    @Override
    public void onCountDown() {
        actuator.addEvent(new CountDownEvent(getTimeByKey() ,getRoomIdByKey(),key));
    }

    @Override
    public String getKey() {
        return key;
    }
}
