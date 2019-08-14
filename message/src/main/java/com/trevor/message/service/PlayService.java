package com.trevor.message.service;

import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.feign.PlayFeign;
import com.trevor.message.socket.NiuniuSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author trevor
 * @date 06/28/19 13:18
 */
@Service
public class PlayService {

    @Resource
    private RoomSocketService roomSocketService;

    @Resource
    private PlayFeign playFeign;

    @Resource
    private RedisService redisService;

    /**
     * 处理准备的消息
     * @param roomId
     */
    public void dealReadyMessage(String roomId , NiuniuSocket socket){
        String gameStatus = getRoomStatus(roomId);
        //根据房间状态判断
        if (gameStatus == null) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        if (!Objects.equals(gameStatus , GameStatusEnum.BEFORE_FAPAI_4.getCode())
            && !Objects.equals(gameStatus , GameStatusEnum.BEFORE_READY.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        //准备的人是否是真正的玩家
        if (!redisService.getSetMembers(RedisConstant.REAL_ROOM_PLAYER + roomId).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(-502));
            return;
        }
        redisService.setAdd(RedisConstant.READY_PLAYER + roomId ,socket.userId);
        //广播准备的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1003 ,socket.userId));

        //准备的人数超过两人
        Integer readyPlayerSize = redisService.getSetSize(RedisConstant.READY_PLAYER + roomId);
        Integer realPlayerSize = redisService.getSetSize(RedisConstant.REAL_ROOM_PLAYER + roomId);
        //判断房间里真正玩家的人数，如果只有两人，直接开始游戏，否则开始倒计时
        if (readyPlayerSize == 2) {
            if (realPlayerSize == 2) {
                playFeign.niuniuEqualsTwo(roomId);
            }else if (realPlayerSize > 2) {
                playFeign.niuniuOverTwo(roomId);
            }
        }
    }


    /**
     * 处理抢庄的消息
     * @param roomId
     */
    public void dealQiangZhuangMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        //验证状态
        if (!Objects.equals(getRoomStatus(roomId) , GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        if (!checkAlreadyReady(roomId ,socket ,-503)) {
            return;
        }
        redisService.put(RedisConstant.QIANGZHAUNG + roomId ,socket.userId ,socketMessage.getQiangZhuangMultiple().toString());

        //广播抢庄的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1010 ,socket.userId ,socketMessage.getQiangZhuangMultiple()));
    }

    /**
     * 处理闲家下注的消息
     * @param roomId
     */
    public void dealXiaZhuMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        if (!Objects.equals(getRoomStatus(roomId) , GameStatusEnum.BEFORE_LAST_POKE.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        if (!checkAlreadyReady(roomId ,socket ,-504)) {
            return;
        }
        //该玩家是否是闲家
        if (Objects.equals(redisService.getValue(RedisConstant.ZHUANGJIA + roomId) ,socket.userId)) {
            socket.sendMessage(new SocketResult(-505));
            return;
        }
        redisService.put(RedisConstant.XIANJIA_XIAZHU + roomId ,socket.userId ,socketMessage.getXianJiaMultiple().toString());
        //广播下注的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1011 ,socket.userId ,socketMessage.getXianJiaMultiple(), Boolean.TRUE));
    }

    /**
     * 处理摊牌的消息
     * @param roomId
     */
    public void dealTanPaiMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        //状态信息
        if (!Objects.equals(getRoomStatus(roomId) , GameStatusEnum.BEFORE_CALRESULT.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        if (!checkAlreadyReady(roomId ,socket ,-503)) {
            return;
        }
        redisService.setAdd(RedisConstant.TANPAI + roomId ,socket.userId);
        //广播摊牌的消息
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1014);
        socketResult.setUserId(socket.userId);
        List<String> pokes = JsonUtil.parseJavaList(redisService.getHashValue(RedisConstant.POKES + socket.roomId ,socket.userId) ,String.class);
        List<Integer> paiXingSet = JsonUtil.parseJavaList(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.PAIXING) , Integer.class);
        Integer rule = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.RULE));
        PaiXing paiXing = PokeUtil.isNiuNiu(pokes ,paiXingSet ,rule);
        Map<String ,Integer> map = new HashMap<>();
        map.put(socket.userId ,paiXing.getPaixing());
        socketResult.setPaiXing(map);
        roomSocketService.broadcast(roomId ,socketResult);
    }



    /**
     * 校验玩家是否已经准备
     * @param roomId
     * @param socket
     * @param head
     * @return
     */
    private Boolean checkAlreadyReady(String roomId ,NiuniuSocket socket ,Integer head){
        if (!redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId).contains(socket.userId)) {
            socket.sendMessage(new SocketResult(head));
            return false;
        }
        return true;
    }

    /**
     * 得到房间状态
     * @param roomId
     * @return
     */
    private String getRoomStatus(String roomId){
        String gameStatus = redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS);
        return gameStatus;
    }
}
