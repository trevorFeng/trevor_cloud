package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.event.Event;

public class CountDownEvent extends Event {

    /**
     * 监听器的key
     */
    private String listenerKey;

    /**
     * 倒计时的时长
     */
    private Integer time;

    private String roomId;

    public CountDownEvent(Integer time ,String roomId ,String listenerKey) {
        this.time = time;
        this.roomId = roomId;
        this.listenerKey = listenerKey;
    }

    @Override
    protected void executeEvent() {
        if (time != 0) {
            SocketResult socketResult = new SocketResult(1002 ,time);
            messageHandle.broadcast(socketResult ,roomId);
            this.time--;
            if (time == 0) {
                //移除监听器
                scheduleDispatch.removeListener(listenerKey);
                //改变房间状态
                redisService.put(RedisConstant.BASE_ROOM_INFO ,RedisConstant.GAME_STATUS , GameStatusEnum.FA_FOUR_PAI.getCode());
                //给玩家发状态信息
                socketResult = new SocketResult();
                socketResult.setHead(1019);
                socketResult.setGameStatus(NumberUtil.stringFormatInteger(GameStatusEnum.FA_FOUR_PAI.getCode()));
                messageHandle.broadcast(socketResult ,roomId);
                //添加发4张牌事件
                actuator.addEvent(new FaPai4Event(roomId));
            }
        }
    }
}
