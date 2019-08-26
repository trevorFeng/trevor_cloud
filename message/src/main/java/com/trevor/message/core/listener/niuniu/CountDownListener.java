package com.trevor.message.core.listener.niuniu;

import com.trevor.message.core.event.niuniu.CountDownEvent;
import com.trevor.message.core.listener.AbstractTaskListener;

/**
 * 倒计时
 */
public class CountDownListener extends AbstractTaskListener {

    /**
     * ready_roomId
     * qiangZhung_roomId
     * xiaZhu_roomId
     * tanPai_roomId
     */
    private String key;

    public CountDownListener(String key) {
        this.key = key;
    }

    @Override
    public void onCountDown() {
        actuator.addEvent(new CountDownEvent(5 ,getRoomIdByKey(),key));
    }

    @Override
    public String getKey() {
        return key;
    }
}
