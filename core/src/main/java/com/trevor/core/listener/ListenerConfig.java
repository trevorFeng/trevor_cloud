package com.trevor.core.listener;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.JsonUtil;
import com.trevor.core.schedule.ScheduleDispatch;

import javax.annotation.Resource;

public abstract class ListenerConfig implements TaskListener{

    protected static RedisService redisService;

    protected static ScheduleDispatch scheduleDispatch;

    @Resource
    public void setRedisService(RedisService redisService){
        ListenerConfig.redisService = redisService;
    }

    /**
     * 给玩家发消息
     * @param socketResult
     * @param playerId
     */
    protected void sendMessage(SocketResult socketResult , String playerId){
        redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + playerId , JsonUtil.toJsonString(socketResult));
    }
}
