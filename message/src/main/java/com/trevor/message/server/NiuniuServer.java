package com.trevor.message.server;

import com.trevor.commom.bo.WebKeys;
import com.trevor.commom.domain.User;
import com.trevor.commom.util.JsonUtil;
import com.trevor.commom.util.ObjectUtil;
import com.trevor.commom.util.TokenUtil;
import com.trevor.message.bo.Constant;
import com.trevor.message.bo.SocketResult;
import com.trevor.message.decoder.MessageDecoder;
import com.trevor.message.encoder.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
public class NiuniuServer extends BaseServer {

    private Session session;

    private Long userId;

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Long roomId) {
        List<String> params = session.getRequestParameterMap().get(WebKeys.TOKEN);
        if (ObjectUtil.isEmpty(params)) {
            sendMessage(session, new SocketResult(400));
            close(session);
            return;
        }
        String token = session.getRequestParameterMap().get(WebKeys.TOKEN).get(0);
        User user = checkToken(token);
        if (ObjectUtil.isEmpty(user)) {
            sendMessage(session, new SocketResult(404));
            close(session);
            return;
        }
        this.userId = user.getId();
        session.setMaxIdleTimeout(1000 * 60 * 45);
        this.session = session;
        redisTemplate.delete(Constant.CACHE_SOCKET_MESSAGES + userId);


    }

    public void sendMessage(Session session, SocketResult pack) {
        BoundListOperations<String, String> messageChannel = redisTemplate.boundListOps(Constant.CACHE_SOCKET_MESSAGES + userId);
        messageChannel.rightPush(JsonUtil.toJsonString(pack));
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    private void close(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("close", e.getMessage(), e);
            }
        }
    }


    /**
     * token合法性检查
     *
     * @param token
     * @throws IOException
     */
    private User checkToken(String token) {
        Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
        String openid = (String) claims.get(WebKeys.OPEN_ID);
        String hash = (String) claims.get("hash");
        Long timestamp = (Long) claims.get("timestamp");
        if (openid == null || hash == null || timestamp == null) {
            return null;
        }
        User user = userService.findUserByOpenid(openid);
        if (user == null || !Objects.equals(user.getHash(), hash)) {
            return null;
        }
        return user;
    }

}
