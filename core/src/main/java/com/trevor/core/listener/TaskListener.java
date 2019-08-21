package com.trevor.core.listener;

import org.springframework.scheduling.annotation.Async;

public interface TaskListener {

    /**
     * 事件
     */
    @Async
    public void onCountDown();

    public String getKey();

}
