package com.trevor.message.socket;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.bo.WebKeys;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.ObjectUtil;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.decoder.MessageDecoder;
import com.trevor.message.encoder.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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
        this.roomId = roomId;
        this.userId = String.valueOf(user.getId());
        roomService.join(roomId ,this);
        session.setMaxIdleTimeout(1000 * 60 * 45);
        this.session = session;
        redisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);
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
            roomService.leave(roomId ,this);
            //如果是真正的玩家则广播消息
            if (redisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER + roomId).range(0 ,-1).contains(userId)) {
                SocketResult res = new SocketResult(1001 ,userId);
                roomService.broadcast(roomId ,res);
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
        BoundListOperations<String, String> messageChannel = redisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + userId);
        messageChannel.rightPush(JsonUtil.toJsonString(pack));
    }

    /**
     * 向客户端刷消息
     */
    public void flush(){
        try {
            BoundListOperations<String ,String> ops = redisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + userId);
            if (ops != null && ops.size() > 0) {
                List<String> messages = ops.range(0, -1);
                redisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);

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
        redisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);
    }


    private void checkRoom(){

    }




}
