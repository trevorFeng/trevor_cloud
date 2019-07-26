package com.trevor.message.service;

import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.feign.PlayFeign;
import com.trevor.message.socket.NiuniuSocket;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author trevor
 * @date 06/28/19 13:18
 */
@Service
public class PlayService {

    @Resource
    private  StringRedisTemplate stringRedisTemplate;

    @Resource
    private RoomSocketService roomSocketService;

    @Resource
    private PlayFeign playFeign;

    /**
     * 处理准备的消息
     * @param roomId
     */
    public void dealReadyMessage(String roomId , NiuniuSocket socket){
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        //根据房间状态判断
        BoundSetOperations<String, String> realPlayerUserIds = stringRedisTemplate.boundSetOps(RedisConstant.REAL_ROOM_PLAYER + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_FAPAI_4.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //准备的人是否是真正的玩家
        if (!realPlayerUserIds.members().contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-502));
            return;
        }
        BoundListOperations<String, String> readyPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        readyPlayerOps.rightPush(socket.userId);
        //广播准备的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1003 ,socket.userId));

        //判断房间里真正玩家的人数，如果只有两人，直接开始游戏，否则开始倒计时
        if (Objects.equals(realPlayerUserIds.size() ,2L)) {
            playFeign.niuniuEqualsTwo(roomId);
        }else if (realPlayerUserIds.size() > 3L) {
            playFeign.niuniuOverTwo(roomId);
        }
    }


    /**
     * 处理抢庄的消息
     * @param roomId
     */
    public void dealQiangZhuangMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        //验证状态
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-503));
            return;
        }
        BoundHashOperations<String, String ,String> qiangZhuangOps = stringRedisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        qiangZhuangOps.put(socket.userId ,socketMessage.getQiangZhuangMultiple().toString());

        //广播抢庄的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1003 ,socket.userId ,socketMessage.getQiangZhuangMultiple()));
    }

    /**
     * 处理闲家下注的消息
     * @param roomId
     */
    public void dealXiaZhuMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_LAST_POKE.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-504));
            return;
        }
        //该玩家是否是闲家
        BoundValueOperations<String, String> zhuangJiaOps = stringRedisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId);
        if (Objects.equals(zhuangJiaOps.get() ,socket.userId)) {
            socket.sendMessage(new SocketResult(-505));
            return;
        }
        BoundHashOperations<String, String ,String> xianJiaXiaZhuOps = stringRedisTemplate.boundHashOps(RedisConstant.XIANJIA_XIAZHU + roomId);
        xianJiaXiaZhuOps.put(socket.userId ,socketMessage.getXianJiaMultiple().toString());
        //广播下注的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1003 ,socket.userId ,socketMessage.getXianJiaMultiple(), Boolean.TRUE));
    }

    /**
     * 处理摊牌的消息
     * @param roomId
     */
    public void dealTanPaiMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        //状态信息
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        if (!Objects.equals(baseRoomInfoOps.get(RedisConstant.GAME_STATUS) , GameStatusEnum.BEFORE_CALRESULT.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //该玩家是否已经准备
        BoundListOperations<String, String> readyPlayerOps = stringRedisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        if (!readyPlayerOps.range(0 ,-1).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-503));
            return;
        }

        BoundListOperations<String, String> tanPaiOps = stringRedisTemplate.boundListOps(RedisConstant.TANPAI + roomId);
        tanPaiOps.rightPush(socket.userId);

        //广播摊牌的消息
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1014);
        socketResult.setUserId(socket.userId);
        BoundHashOperations<String, String, String> pokesOps = stringRedisTemplate.boundHashOps(RedisConstant.POKES + socket.roomId);
        List<String> pokes = JsonUtil.parse(pokesOps.get(socket.userId) ,new ArrayList<String>());
        Set<Integer> paiXingSet = JsonUtil.parse(baseRoomInfoOps.get(RedisConstant.PAIXING) ,new HashSet<Integer>());
        Integer rule = Integer.valueOf(baseRoomInfoOps.get(RedisConstant.RULE));
        PaiXing paiXing = PokeUtil.isNiuNiu(pokes ,paiXingSet ,rule);
        Map<String ,Integer> map = new HashMap<>();
        map.put(socket.userId ,paiXing.getPaixing());
        socketResult.setPaiXing(map);
        roomSocketService.broadcast(roomId ,socketResult);
    }
}
