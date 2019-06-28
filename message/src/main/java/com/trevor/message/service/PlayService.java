package com.trevor.message.service;

import com.trevor.commom.bo.RedisConstant;
import com.trevor.commom.bo.RedisKey;
import com.trevor.commom.bo.SocketResult;
import com.trevor.commom.enums.GameStatusEnum;
import com.trevor.message.server.NiuniuServer;
import org.springframework.data.redis.core.BoundHashOperations;
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
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        String gameStatus = ops.get(RedisKey.GAME_STATUS);
        if (!Objects.equals(gameStatus , GameStatusEnum.BEFORE_FAPAI_4.getCode().toString())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
    }
}
