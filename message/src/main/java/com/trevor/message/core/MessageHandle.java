package com.trevor.message.core;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.JsonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class MessageHandle {

    @Resource
    private RedisService redisService;

    /**
     * 给玩家发消息
     * @param socketResult
     * @param playerId
     */
    public void sendMessage(SocketResult socketResult , String playerId){
        redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + playerId , JsonUtil.toJsonString(socketResult));
    }

    /**
     * 改变房间状态
     * @param roomId
     * @param gameStatus
     */
    public void changeGameStatus(String roomId ,String gameStatus){
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS , gameStatus);
    }

    /**
     * 广播消息
     * @param socketResult
     * @param roomIdStr
     */
    public void broadcast(SocketResult socketResult ,String roomIdStr){
        Set<String> playerIds = redisService.getSetMembers(RedisConstant.ROOM_PLAYER + roomIdStr);
        for (String playerId : playerIds) {
            redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + playerId ,JsonUtil.toJsonString(socketResult));
        }

    }
}
