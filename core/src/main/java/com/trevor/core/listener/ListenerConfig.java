package com.trevor.core.listener;

import com.trevor.common.service.RedisService;
import com.trevor.core.schedule.ScheduleDispatch;

import javax.annotation.Resource;

public abstract class ListenerConfig implements TaskListener{

    protected static RedisService redisService;

    protected static ScheduleDispatch scheduleDispatch;

    @Resource
    public void setRedisService(RedisService redisService){
        ListenerConfig.redisService = redisService;
    }
}
