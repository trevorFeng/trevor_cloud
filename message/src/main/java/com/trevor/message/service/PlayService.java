package com.trevor.message.service;

import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.NumberUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.message.bo.SocketMessage;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.actuator.Actuator;
import com.trevor.message.core.event.niuniu.FaPai1Event;
import com.trevor.message.core.event.niuniu.FaPai4Event;
import com.trevor.message.core.event.niuniu.SelectZhuangJiaEvent;
import com.trevor.message.core.listener.niuniu.CountDownListener;
import com.trevor.message.core.schedule.ScheduleDispatch;
import com.trevor.message.socket.NiuniuSocket;
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
    private RoomSocketService roomSocketService;


    @Resource
    private RedisService redisService;

    @Resource
    private ScheduleDispatch scheduleDispatch;

    @Resource
    private Actuator actuator;

    /**
     * 处理准备的消息
     * @param roomId
     */
    public void dealReadyMessage(String roomId , NiuniuSocket socket){
        //准备的人是否是真正的玩家
        if (!redisService.jugeSetMember(RedisConstant.getRealRoomPlayer(roomId) ,socket.userId)) {
            socket.sendMessage(new SocketResult(-502));
            return;
        }
        //总局数
        String totalNum = redisService.getHashValue(RedisConstant.getBaseRoomInfo(roomId) ,RedisConstant.TOTAL_NUM);
        //当前局数
        String runingNum = redisService.getValue(RedisConstant.getRuningNum(roomId));
        //当前的房间状态
        String gameStatus = redisService.getValue(RedisConstant.getGameStatus(roomId ,runingNum));
        //房间状态不是准备状态，可以准备下一局
        if (!Objects.equals(gameStatus ,GameStatusEnum.READY.getCode())) {
            //判断是否是最后一局，不是得话就准备下一局
            if (Objects.equals(runingNum ,totalNum)) {
                socket.sendMessage(new SocketResult(-501));
                return;
            }else {
                String nextRuningNum = NumberUtil.stringFormatInteger(runingNum) + 1 + "";
                redisService.setAdd(RedisConstant.getReadyPlayer(roomId ,nextRuningNum) ,socket.userId);
            }
        }else {
            redisService.setAdd(RedisConstant.getReadyPlayer(roomId ,runingNum) ,socket.userId);
            //广播准备的消息
            SocketResult soc = new SocketResult();
            soc.setHead(1003);
            soc.setReadyPlayerIds(redisService.getSetMembers(RedisConstant.getReadyPlayer(roomId ,runingNum)));
            roomSocketService.broadcast(roomId ,soc);

            //准备的人数超过两人
            Integer readyPlayerSize = redisService.getSetSize(RedisConstant.getReadyPlayer(roomId ,runingNum));
            Integer realPlayerSize = redisService.getSetSize(RedisConstant.getRealRoomPlayer(roomId));

            //如果准备得玩家等于真正玩家得人数，则移除监听器,直接开始发牌
            if (Objects.equals(readyPlayerSize ,realPlayerSize)) {
                scheduleDispatch.removeListener(ListenerKey.READY + roomId);
                actuator.addEvent(new FaPai4Event(roomId ,runingNum));

            }

            //判断房间里真正玩家的人数，如果只有两人，直接开始游戏，否则开始倒计时
            if (readyPlayerSize == 2 && realPlayerSize > 2) {
                //注册准备倒计时监听器
                scheduleDispatch.addListener(new CountDownListener(ListenerKey.getReadyKey(roomId ,runingNum ,5)+ roomId));
            }
        }
    }


    /**
     * 处理抢庄的消息
     * @param roomId
     */
    public void dealQiangZhuangMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        //当前局数
        String runingNum = redisService.getValue(RedisConstant.getRuningNum(roomId));
        //当前的房间状态
        String gameStatus = redisService.getValue(RedisConstant.getGameStatus(roomId ,runingNum));
        //验证状态
        if (!Objects.equals(gameStatus , GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_START.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        Set<String> readyPlayers = redisService.getSetMembers(RedisConstant.getReadyPlayer(roomId ,runingNum));
        if (!checkAlreadyReady(roomId ,socket ,-503)) {
            return;
        }

        Integer multiple = socketMessage.getQiangZhuangMultiple() == 0 ? 1 : socketMessage.getQiangZhuangMultiple();
        redisService.put(RedisConstant.getQiangZhuang(roomId ,runingNum) ,socket.userId ,multiple.toString());

        //广播抢庄的消息
        roomSocketService.broadcast(roomId ,new SocketResult(1010 ,socket.userId ,socketMessage.getQiangZhuangMultiple()));

        Integer readyPlayerSize = readyPlayers.size();
        Integer qiangZhuangSize = redisService.getMapSize(RedisConstant.getQiangZhuang(roomId ,runingNum));

        if (Objects.equals(readyPlayerSize ,qiangZhuangSize)) {
            //删除抢庄倒计时监听器
            scheduleDispatch.removeListener(ListenerKey.getQiangZhaungKey(roomId ,runingNum ,ListenerKey.TIME_FIVE));
            //添加选择庄家事件事件
            actuator.addEvent(new SelectZhuangJiaEvent(roomId ,runingNum));
        }
    }

    /**
     * 处理闲家下注的消息
     * @param roomId
     */
    public void dealXiaZhuMessage(String roomId , NiuniuSocket socket , SocketMessage socketMessage){
        if (!Objects.equals(getRoomStatus(roomId) , GameStatusEnum.XIA_ZHU_COUNT_DOWN_START.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        Set<String> readyPlayers = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
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

        Integer readyPlayerSize = readyPlayers.size();
        Integer xiaZhuSize = redisService.getMapSize(RedisConstant.XIANJIA_XIAZHU + roomId);
        if (Objects.equals(readyPlayerSize - 1 ,xiaZhuSize)) {
            //删除下注倒计时监听器
            scheduleDispatch.removeListener(ListenerKey.getListenerKey(ListenerKey.XIA_ZHU ,roomId ,ListenerKey.TIME_FIVE));
            //添加发一张牌事件
            actuator.addEvent(new FaPai1Event(roomId));
        }
    }

    /**
     * 处理摊牌的消息
     * @param roomId
     */
    public void dealTanPaiMessage(String roomId , NiuniuSocket socket){
        //状态信息
        if (!Objects.equals(getRoomStatus(roomId) , GameStatusEnum.TAN_PAI_COUNT_DOWN_START.getCode())) {
            socket.sendMessage(new SocketResult(-501));
            return;
        }
        Set<String> readyPlayers = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        if (!checkAlreadyReady(roomId ,socket ,-503)) {
            return;
        }
        redisService.setAdd(RedisConstant.TANPAI + roomId ,socket.userId);
        //广播摊牌的消息
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1014);
        socketResult.setUserId(socket.userId);
        roomSocketService.broadcast(roomId ,socketResult);

        Integer readyPlayerSize = readyPlayers.size();
        Integer tanPaiSize = redisService.getMapSize(RedisConstant.TANPAI + roomId);

        if (Objects.equals(readyPlayerSize ,tanPaiSize)) {
            //删除摊牌倒计时监听器
            scheduleDispatch.removeListener(ListenerKey.getListenerKey(ListenerKey.TAI_PAI ,roomId ,ListenerKey.TIME_FIVE));
            //添加发一张牌事件
            actuator.addEvent(new FaPai1Event(roomId));
        }

    }

    /**
     * 处理说话的消息
     * @param roomId
     */
    public void dealShuoHuaMessage(String roomId ,NiuniuSocket niuniuSocket ,SocketMessage socketMessage){
        SocketResult socketResult = new SocketResult(1017);
        socketResult.setShuoHuaCode(socketMessage.getShuoHuaCode());
        socketResult.setUserId(niuniuSocket.userId);
        roomSocketService.broadcast(roomId ,socketResult);
    }

    /**
     * 处理为切换为观战的消息
     * @param roomId
     * @param niuniuSocket
     */
    public void dealChangeToGuanZhan(String roomId ,NiuniuSocket niuniuSocket){
        if (redisService.jugeSetMember(RedisConstant.REAL_ROOM_PLAYER + roomId ,niuniuSocket.userId)) {
            redisService.setAdd(RedisConstant.GUANZHONG + roomId ,niuniuSocket.userId);
            SocketResult socketResult = new SocketResult();
            socketResult.setHead(1018);
            socketResult.setUserId(niuniuSocket.userId);
            roomSocketService.broadcast(roomId ,socketResult);
            return;
        }
        niuniuSocket.sendMessage(new SocketResult(-501));
        return;
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
}
