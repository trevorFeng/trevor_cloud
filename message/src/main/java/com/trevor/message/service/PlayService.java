package com.trevor.message.service;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.socket.NiuniuServer;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
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
    private  StringRedisTemplate redisTemplate;

    @Resource
    private RoomService roomService;

    /**
     * 处理准备的消息
     * @param roomId
     */
    public void dealReadyMessage(String roomId , NiuniuServer socket){
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        //根据房间状态判断
        BoundListOperations<String, String> realPlayerUserIds = redisTemplate.boundListOps(RedisConstant.REAL_ROOM_PLAYER + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_FAPAI_4.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //准备的人是否是真正的玩家
        if (!realPlayerUserIds.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-502));
            return;
        }
        BoundListOperations<String, String> readyPlayerOps = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        readyPlayerOps.rightPush(socket.userId);
        //广播准备的消息
        roomService.broadcast(roomId ,new SocketResult(1003 ,socket.userId));
    }


    /**
     * 处理抢庄的消息
     * @param roomId
     */
    public void dealQiangZhuangMessage(String roomId , NiuniuServer socket , SocketMessage socketMessage){
        //验证状态
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-503));
            return;
        }
        BoundHashOperations<String, String ,String> qiangZhuangOps = redisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        qiangZhuangOps.put(socket.userId ,socketMessage.getQiangZhuangMultiple().toString());

        //广播抢庄的消息
        roomService.broadcast(roomId ,new SocketResult(1003 ,socket.userId ,socketMessage.getQiangZhuangMultiple()));
    }

    /**
     * 处理闲家下注的消息
     * @param roomId
     */
    public void dealXiaZhuMessage(String roomId , NiuniuServer socket , SocketMessage socketMessage){
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_LAST_POKE.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-504));
            return;
        }
        //该玩家是否是闲家
        BoundValueOperations<String, String> zhuangJiaOps = redisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId);
        if (Objects.equals(zhuangJiaOps.get() ,socket.userId)) {
            socket.sendMessage(new SocketResult(-505));
            return;
        }
        BoundHashOperations<String, String ,String> xianJiaXiaZhuOps = redisTemplate.boundHashOps(RedisConstant.XIANJIA_XIAZHU + roomId);
        xianJiaXiaZhuOps.put(socket.userId ,socketMessage.getXianJiaMultiple().toString());
        //广播下注的消息
        roomService.broadcast(roomId ,new SocketResult(1003 ,socket.userId ,socketMessage.getXianJiaMultiple(), Boolean.TRUE));
    }

    /**
     * 处理摊牌的消息
     * @param roomId
     */
    public void dealTanPaiMessage(String roomId , NiuniuServer socket , SocketMessage socketMessage){
        //状态信息
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_CALRESULT.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-503));
            return;
        }

        BoundListOperations<String, String> tanPaiOps = redisTemplate.boundListOps(RedisConstant.TANPAI + roomId);
        tanPaiOps.rightPush(socket.userId);

        //广播摊牌的消息
        roomService.broadcast(roomId ,new SocketResult(1014 ,socket.userId));
    }
}
