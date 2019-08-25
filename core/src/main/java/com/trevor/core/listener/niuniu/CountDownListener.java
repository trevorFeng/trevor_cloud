package com.trevor.core.listener.niuniu;

import com.trevor.core.listener.ListenerConfig;
import com.trevor.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 倒计时
 */
public class CountDownListener extends ListenerConfig {

    private Integer conuntDown;

    private String key;

    public CountDownListener(Integer conuntDown, String key) {
        this.conuntDown = conuntDown;
        this.key = key;
    }

    @Async("ectraor")
    @Override
    public void executeEvent() {
        this.conuntDown--;
        if (conuntDown == 1) {
            //改变房间状态
            //注册发4张牌事件

        }

    }

    @Override
    public String getKey() {

        return null;
    }
}
