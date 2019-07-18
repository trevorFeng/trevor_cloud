package com.trevor.message.socket;

import com.trevor.common.dao.mongo.NiuniuRoomParamMapper;
import com.trevor.common.dao.mysql.FriendManageMapper;
import com.trevor.common.service.RoomService;
import com.trevor.common.service.UserService;
import com.trevor.message.service.PlayService;
import com.trevor.message.service.RoomSocketService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 06/27/19 18:05
 */
@Component
public class BaseServer {

    protected static StringRedisTemplate stringRedisTemplate;

    protected static UserService userService;

    protected static RoomSocketService roomSocketService;

    protected static PlayService playService;

    protected static RoomService roomService;

    protected static FriendManageMapper friendManageMapper;

    @Resource
    public void setPlayService(PlayService playService){
        BaseServer.playService = playService;
    }

    @Resource
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate){
        BaseServer.stringRedisTemplate = stringRedisTemplate;
    }

    @Resource
    public void setUserService(UserService userService){
        BaseServer.userService = userService;
    }

    @Resource
    public void setRoomSocketService(RoomSocketService roomSocketService){
        BaseServer.roomSocketService = roomSocketService;
    }

    @Resource
    public void setRoomService(RoomService roomService){
        BaseServer.roomService = roomService;
    }

}