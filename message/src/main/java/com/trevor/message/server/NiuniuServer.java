package com.trevor.message.server;

import com.trevor.commom.bo.RedisConstant;
import com.trevor.commom.bo.SocketResult;
import com.trevor.commom.bo.WebKeys;
import com.trevor.commom.domain.mysql.User;
import com.trevor.commom.util.JsonUtil;
import com.trevor.commom.util.ObjectUtil;
import com.trevor.message.decoder.MessageDecoder;
import com.trevor.message.encoder.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;


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
public class NiuniuServer extends BaseServer {

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
        redisTemplate.delete(RedisConstant.CACHE_SOCKET_MESSAGES + userId);

    }

    /**
     * 接受用户消息
     */
    @OnMessage
    public void onMessage(String userId){

    }

    /**
     * 关闭连接调用的方法
     */
    @OnClose
    public void onClose(){
        if (!ObjectUtil.isEmpty(userId)) {
            roomService.leave(roomId ,this);
            SocketResult res = new SocketResult(1001);

        }
    }

    /**
     * 发生错误时调用的方法
     */
    public void onError(){

    }

    public void sendMessage(SocketResult pack) {
        BoundListOperations<String, String> messageChannel = redisTemplate.boundListOps(RedisConstant.CACHE_SOCKET_MESSAGES + userId);
        messageChannel.rightPush(JsonUtil.toJsonString(pack));
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




}
