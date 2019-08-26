package com.trevor.message.core.event;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.service.RedisService;
import com.trevor.message.core.MessageHandle;
import com.trevor.message.core.actuator.Actuator;
import com.trevor.message.core.schedule.ScheduleDispatch;

public abstract class Event implements Runnable{

    protected String roomId;

    public static Actuator actuator;

    public static ScheduleDispatch scheduleDispatch;

    public static MessageHandle messageHandle;

    public static RedisService redisService;


    protected abstract void executeEvent();

    @Override
    public void run() {
        executeEvent();
    }


}
