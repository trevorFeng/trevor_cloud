package com.trevor.message.game.niuniu;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.niuniu.FaPai4Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;
import com.trevor.message.game.RoomData;
import com.trevor.message.game.Task;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class Ready {

    public void ready(RoomData roomData , Task task){
        //准备的人是否是真正的玩家
        if (!roomData.getRealPlayers().contains(task.getPlayId())) {

        }
    }
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
        if (!Objects.equals(gameStatus , GameStatusEnum.READY.getCode())) {
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
