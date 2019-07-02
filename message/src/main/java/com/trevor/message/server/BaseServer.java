package com.trevor.message.server;

import com.trevor.commom.service.UserService;
import com.trevor.message.service.PlayService;
import com.trevor.message.service.RoomService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 06/27/19 18:05
 */
@Component
public class BaseServer {

    protected static StringRedisTemplate redisTemplate;

    protected static UserService userService;

    protected static RoomService roomService;

    protected static PlayService playService;

    @Resource
    public void setPlayService(PlayService playService){
        BaseServer.playService = playService;
    }

    @Resource
    public void setStringRedisTemplate(StringRedisTemplate redisTemplate){
        BaseServer.redisTemplate = redisTemplate;
    }

    @Resource
    public void setUserService(UserService userService){
        BaseServer.userService = userService;
    }

    @Resource
    public void setRoomService(RoomService roomService){
        BaseServer.roomService = roomService;
    }

}
