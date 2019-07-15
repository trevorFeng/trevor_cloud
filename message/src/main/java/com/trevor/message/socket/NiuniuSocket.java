package com.trevor.message.socket;

import com.alibaba.fastjson.JSON;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.bo.WebKeys;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.Room;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.FriendManageEnum;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.enums.RoomTypeEnum;
import com.trevor.common.enums.SpecialEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.ObjectUtil;
import com.trevor.message.bo.ReturnMessage;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.decoder.MessageDecoder;
import com.trevor.message.encoder.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;


/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint(
        value = "/niuniu/{roomId}",
        encoders = {MessageEncoder.class},
        decoders = {MessageDecoder.class}
)
@Component
@Slf4j
public class NiuniuSocket extends BaseServer {

    public Session session;

    public String userId;

    public String roomId;

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        //roomId合法性检查
        Long roomIdLong = Long.valueOf(roomId);
        Room room = roomService.findOneById(roomIdLong);
        if (room == null) {
            sendMessage(new SocketResult(507));
            close(session);
            return;
        }
        if (!Objects.equals(room.getStatus() ,0)) {
            sendMessage(new SocketResult(506));
            close(session);
            return;
        }
        //token合法性检查
        List<String> params = session.getRequestParameterMap().get(WebKeys.TOKEN);
        if (ObjectUtil.isEmpty(params)) {
            sendMessage(new SocketResult(400));
            close(session);
            return;
        }
        String token = session.getRequestParameterMap().get(WebKeys.TOKEN).get(0);
        User user = userService.getUserByToken(token);
        if (ObjectUtil.isEmpty(user)) {
            sendMessage(new SocketResult(404));
            close(session);
            return;
        }
        //检查是否是断线重连的玩家或者中途退出的玩家
        BoundListOperations<String, String> realPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER);
        BoundListOperations<String, String> roomPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.ROOM_PLAYER);
        if (roomPlayerOps != null && roomPlayerOps.size()>0) {
            //不是因为网络断线的玩家
            if (!realPlayerOps.range(0 ,-1).contains(userId)) {
                //中途退出的玩家，给其他玩家发玩家重新加入的消息
                if (realPlayerOps != null && realPlayerOps.size()>0 && realPlayerOps.range(0 ,-1).contains(userId)) {
                    SocketResult socketResult = new SocketResult(1015 ,userId);
                    roomSocketService.broadcast(roomId ,socketResult);
                    return;
                }
            //网络断线的玩家,给玩家发房间状态信息
            }else {

            }
        }

        SocketResult socketResult = checkRoom(room, user);


        this.roomId = roomId;
        this.userId = String.valueOf(user.getId());
        roomSocketService.join(roomId ,this);
        session.setMaxIdleTimeout(1000 * 60 * 45);
        this.session = session;
        stringRedisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);
    }

    /**
     * 接受用户消息
     */
    @OnMessage
    public void onMessage(SocketMessage socketMessage){
        if (Objects.equals(socketMessage.getMessageCode() ,1)) {
            playService.dealReadyMessage(roomId ,this);
        }else if (Objects.equals(socketMessage.getMessageCode() ,2)) {
            playService.dealQiangZhuangMessage(roomId ,this ,socketMessage);
        }
    }

    /**
     * 关闭连接调用的方法
     */
    @OnClose
    public void onClose(){
        if (!ObjectUtil.isEmpty(userId)) {
            roomSocketService.leave(roomId ,this);
            //如果是真正的玩家则广播消息
            if (stringRedisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER + roomId).range(0 ,-1).contains(userId)) {
                SocketResult res = new SocketResult(1001 ,userId);
                roomSocketService.broadcast(roomId ,res);
            }
        }
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Throwable t){
        log.error(t.getMessage() ,t);
    }

    /**
     * 向客户端发消息
     * @param pack
     */
    public void sendMessage(SocketResult pack) {
        BoundListOperations<String, String> messageChannel = stringRedisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + userId);
        messageChannel.rightPush(JsonUtil.toJsonString(pack));
    }

    /**
     * 向客户端刷消息
     */
    public void flush(){
        try {
            BoundListOperations<String ,String> ops = stringRedisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + userId);
            if (ops != null && ops.size() > 0) {
                List<String> messages = ops.range(0, -1);
                stringRedisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);

                StringBuffer stringBuffer = new StringBuffer("{");
                for (String mess : messages) {
                    stringBuffer.append(mess).append(",");
                }
                stringBuffer.setLength(stringBuffer.length() - 1);
                stringBuffer.append("}");
                synchronized (session) {
                    RemoteEndpoint.Async async = session.getAsyncRemote();
                    if (session.isOpen()) {
                        async.sendText(stringBuffer.toString());
                    } else {
                        close(session);
                    }
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage() ,e);
        }
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    public void close(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("close", e.getMessage(), e);
            }
        }
    }

    public void stop(){
        stringRedisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);
    }


    private SocketResult checkRoom(Room room ,User user){
        //房主是否开启好友管理功能
        Boolean isFriendManage = Objects.equals(userService.isFriendManage(room.getRoomAuth()) , FriendManageEnum.YES.getCode());
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO);
        HashSet<Integer> special = JsonUtil.parse(baseRoomInfoOps.get(RedisConstant.SPECIAL), new HashSet<Integer>());
        //开通
        if (isFriendManage) {
            //配置仅限好友
            if (special.contains(SpecialEnum.JUST_FRIENDS.getCode())) {
                Long count = friendManageMapper.countRoomAuthFriendAllow(room.getRoomAuth(), user.getId());
                //不是房主的好友
                if (Objects.equals(count ,0L)) {
                    return new SocketResult(508);
                    //是房主的好友
                }else {
                    return this.dealCanSee(user ,special);
                }
            }
            //未配置仅限好友
            else {
                return this.dealCanSee(user ,special);
            }
            // 未开通
        }else {
            return this.dealCanSee( user ,special);
        }

    }

    /**
     * 处理是否可以观战
     * @throws IOException
     */
    private SocketResult dealCanSee(User user, HashSet<Integer> special){
        SocketResult socketResult = new SocketResult();
        socketResult.setUserId(String.valueOf(user.getId()));
        socketResult.setName(user.getAppName());
        socketResult.setPictureUrl(user.getAppPictureUrl());
        BoundListOperations<String, String> realPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER);
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO);
        //允许观战
        if (special!= null && special.contains(SpecialEnum.CAN_SEE.getCode())) {
            if (realPlayerOps != null || realPlayerOps.size() < RoomTypeEnum.getRoomNumByType(Integer.valueOf(baseRoomInfoOps.get(RedisConstant.ROOM_TYPE)))) {
                socketResult.setIsGuanZhong(false);
                socketResult.setIsChiGuaPeople(Boolean.FALSE);
            }else {
                socketResult.setIsChiGuaPeople(Boolean.TRUE);
            }
            return socketResult;
        //不允许观战
        }else {
            if (realPlayerOps != null || realPlayerOps.size() < RoomTypeEnum.getRoomNumByType(Integer.valueOf(baseRoomInfoOps.get(RedisConstant.ROOM_TYPE)))) {
                socketResult.setIsGuanZhong(false);
                socketResult.setIsChiGuaPeople(Boolean.FALSE);
                return socketResult;
            }else {
                return new SocketResult(509);
            }

        }
    }

    /**
     * 玩家加入
     */
    private void welcome(String roomId){
        BoundHashOperations<String, String, String> roomBaseInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);

    }




}
