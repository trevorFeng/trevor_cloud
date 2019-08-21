package com.trevor.core.listener.niuniu;

import com.trevor.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Async;

public class FaPai_4Listener implements TaskListener {

    @Async
    @Override
    public void onCountDown() {

    }

    @Override
    public String getKey() {
        return null;
    }
}
