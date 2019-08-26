package com.trevor.message.core.event.niuniu;

import com.trevor.message.core.event.Event;

public class StopOrContinueEvent extends Event {

    public StopOrContinueEvent(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {

    }
}
