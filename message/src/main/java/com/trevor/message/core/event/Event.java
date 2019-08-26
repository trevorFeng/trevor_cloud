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

    protected void sendGameStatus(String gameStatus ,String roomId) {
        //改变房间状态
        redisService.put(RedisConstant.BASE_ROOM_INFO, RedisConstant.GAME_STATUS, gameStatus);
        //给玩家发状态信息
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1019);
        socketResult.setGameStatus(gameStatus);
        messageHandle.broadcast(socketResult, roomId);
    }
}
