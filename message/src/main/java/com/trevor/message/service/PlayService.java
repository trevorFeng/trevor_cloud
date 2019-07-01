package com.trevor.message.service;

import com.trevor.commom.bo.RedisConstant;
import com.trevor.commom.bo.SocketResult;
import com.trevor.commom.enums.GameStatusEnum;
import com.trevor.message.server.NiuniuServer;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author trevor
 * @date 06/28/19 13:18
 */
@Service
public class PlayService {

    @Resource
    private static StringRedisTemplate redisTemplate;

    /**
     * 处理准备的消息
     * @param roomId
     */
    public void dealReadyMessage(String roomId , NiuniuServer socket){
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        BoundListOperations<String, String> playerUserIds = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER + roomId);
        BoundListOperations<String, String> realPlayerUserIds = redisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER + roomId);
        if (realPlayerUserIds == null || realPlayerUserIds.size() == 0) {

        }
        if (realPlayerUserIds.size() == 2)
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_FAPAI_4.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        ops.put(RedisConstant.READY + socket.roomId ,socket.userId);
        //给其他玩家发准备的消息

    }
}
