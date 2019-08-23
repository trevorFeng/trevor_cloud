package com.trevor.message.core.event;

import com.trevor.message.core.MessageHandle;
import com.trevor.message.core.actuator.Actuator;
import com.trevor.message.core.schedule.ScheduleDispatch;

public abstract class Event implements Runnable{

    public static Actuator actuator;

    public static ScheduleDispatch scheduleDispatch;

    public static MessageHandle messageHandle;


    protected abstract void executeEvent();

    @Override
    public void run() {
        executeEvent();
    }
}
