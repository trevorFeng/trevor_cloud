package com.trevor.message.socket;

import com.trevor.common.dao.mysql.FriendManageMapper;
import com.trevor.common.service.RedisService;
import com.trevor.common.service.RoomService;
import com.trevor.common.service.UserService;
import com.trevor.message.service.PlayService;
import com.trevor.message.service.RoomSocketService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 06/27/19 18:05
 */
@Component
public class BaseServer {

    protected static UserService userService;

    protected static RoomSocketService roomSocketService;

    protected static PlayService playService;

    protected static RoomService roomService;

    protected static FriendManageMapper friendManageMapper;

    protected static RedisService redisService;

    @Resource
    public void setRedisService(RedisService redisService){
        BaseServer.redisService = redisService;
    }


    @Resource
    public void setPlayService(PlayService playService){
        BaseServer.playService = playService;
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
