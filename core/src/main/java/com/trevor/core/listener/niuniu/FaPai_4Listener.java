package com.trevor.core.listener.niuniu;

import com.trevor.core.listener.ListenerConfig;
import com.trevor.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 发4张牌事件
 */
public class FaPai_4Listener extends ListenerConfig {

    /**
     * 格式为fapai_4_roomId
     */
    private String key;

    @Async
    @Override
    public void executeEvent() {
        String roomId = key.split("_")[key.length()-1];

    }

    @Override
    public String getKey() {
        return key;
    }
}
