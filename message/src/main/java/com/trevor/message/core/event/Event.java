package com.trevor.message.core.event;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.dao.mongo.PlayerResultMapper;
import com.trevor.common.service.RedisService;
import com.trevor.common.service.RoomService;
import com.trevor.common.service.UserService;
import com.trevor.message.core.MessageHandle;
import com.trevor.message.core.actuator.Actuator;
import com.trevor.message.core.schedule.ScheduleDispatch;

public abstract class Event implements Runnable{

    protected String roomId;

    protected String runingNum;

    public static Actuator actuator;

    public static ScheduleDispatch scheduleDispatch;

    public static MessageHandle messageHandle;

    public static RedisService redisService;

    public static RoomService roomService;

    public static UserService userService;

    public static PlayerResultMapper playerResultMapper;


    protected abstract void executeEvent();

    @Override
    public void run() {
        executeEvent();
    }




}
