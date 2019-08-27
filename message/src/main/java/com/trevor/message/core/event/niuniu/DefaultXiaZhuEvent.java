package com.trevor.message.core.event.niuniu;

import com.google.common.collect.Maps;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultXiaZhuEvent extends Event {

    public DefaultXiaZhuEvent(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {
        Map<String ,String> map = Maps.newHashMap();
        Set<String> readyPlayers = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        Set<String> xiaZhuPlayers = redisService.getMapKeys(RedisConstant.XIANJIA_XIAZHU + roomId);
        String zhuangJiaPlayerId = redisService.getValue(RedisConstant.ZHUANGJIA + roomId);
        for (String s : readyPlayers) {
            if (!Objects.equals(s ,zhuangJiaPlayerId) && !xiaZhuPlayers.contains(s)) {
                map.put(s ,"1");
            }
        }
        if (!map.isEmpty()) {
            messageHandle.changeGameStatus(roomId , GameStatusEnum.DEFAULT_XIA_ZHU.getCode());
            SocketResult soc = new SocketResult();
            soc.setHead(1020);
            soc.setXianJiaXiaZhuMap(map);
            soc.setGameStatus(GameStatusEnum.DEFAULT_XIA_ZHU.getCode());
            messageHandle.broadcast(soc ,roomId);
            actuator.addEvent(new FaPai4Event(roomId));
//            scheduleDispatch.addListener(new CountDownListener(ListenerKey.TAI_PAI + ListenerKey.SPLIT + roomId
//                    + ListenerKey.SPLIT + ListenerKey.TIME_FIVE));
        }
    }
}
