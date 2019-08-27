package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;

import java.util.Objects;

public class CountDownEvent extends Event {

    /**
     * 监听器的key
     */
    private String listenerKey;

    /**
     * 倒计时的时长
     */
    private Integer time;

    public CountDownEvent(Integer time ,String roomId ,String listenerKey) {
        this.time = time;
        super.roomId = roomId;
        this.listenerKey = listenerKey;
    }

    @Override
    protected void executeEvent() {
        if (time != 0) {
            //前端转圈效果不发送倒计时
            if (!listenerKey.startsWith(ListenerKey.ZHUAN_QUAN) && !listenerKey.startsWith(ListenerKey.SETTLE)) {
                sendCountDown();
            }
            this.time--;
            if (time == 0) {
                //移除监听器
                scheduleDispatch.removeListener(listenerKey);
                //添加事件
                addEvent();
            }
        }
    }


    private void sendCountDown(){
        Integer keyTime = getTimeByKey();
        SocketResult soc = new SocketResult();
        soc.setCountDown(time);
        //准备的倒计时
        if (listenerKey.startsWith(ListenerKey.READY)) {
            if (Objects.equals(time ,keyTime)) {
                soc.setHead(1002);
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_START.getCode());

                messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_START.getCode());
                messageHandle.broadcast(soc ,roomId);
            }else if (time == 1) {
                soc.setHead(1002);
                soc.setGameStatus(GameStatusEnum.READY_COUNT_DOWN_END.getCode());

                messageHandle.broadcast(soc ,roomId);
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.READY_COUNT_DOWN_END.getCode());
            }
         //抢庄的倒计时
        }else if (listenerKey.startsWith(ListenerKey.QIANG_ZHAUNG)) {
            if (Objects.equals(time ,keyTime)) {
                soc.setHead(1005);
                soc.setGameStatus(GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_START.getCode());

                messageHandle.changeGameStatus(roomId ,GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_START.getCode());
                messageHandle.broadcast(soc ,roomId);
            }else if (time == 1) {
                soc.setHead(1005);
                soc.setGameStatus(GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_END.getCode());

                messageHandle.broadcast(soc ,roomId);
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.QIANG_ZHUANG_COUNT_DOWN_END.getCode());
            }
        //下注的倒计时
        }else if (listenerKey.startsWith(ListenerKey.XIA_ZHU)) {
            if (Objects.equals(time ,keyTime)) {
                soc.setHead(1007);
                soc.setGameStatus(GameStatusEnum.XIA_ZHU_COUNT_DOWN_START.getCode());

                messageHandle.changeGameStatus(roomId ,GameStatusEnum.XIA_ZHU_COUNT_DOWN_START.getCode());
                messageHandle.broadcast(soc ,roomId);
            }else if (time == 1) {
                soc.setHead(1007);
                soc.setGameStatus(GameStatusEnum.XIA_ZHU_COUNT_DOWN_END.getCode());

                messageHandle.broadcast(soc ,roomId);
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.XIA_ZHU_COUNT_DOWN_END.getCode());
            }
        //摊牌的倒计时
        }else if (listenerKey.startsWith(ListenerKey.TAI_PAI)) {
            if (Objects.equals(time ,keyTime)) {
                soc.setHead(1009);
                soc.setGameStatus(GameStatusEnum.TAN_PAI_COUNT_DOWN_START.getCode());

                messageHandle.changeGameStatus(roomId ,GameStatusEnum.TAN_PAI_COUNT_DOWN_START.getCode());
                messageHandle.broadcast(soc ,roomId);
            }else if (time == 1) {
                soc.setHead(1008);
                soc.setGameStatus(GameStatusEnum.TAN_PAI_COUNT_DOWN_END.getCode());

                messageHandle.broadcast(soc ,roomId);
                messageHandle.changeGameStatus(roomId ,GameStatusEnum.TAN_PAI_COUNT_DOWN_END.getCode());
            }
        }

    }


    private void addEvent(){
        //准备的倒计时
        if (listenerKey.startsWith(ListenerKey.READY)) {
            actuator.addEvent(new FaPai4Event(roomId));
        //抢庄的倒计时
        }else if (listenerKey.startsWith(ListenerKey.QIANG_ZHAUNG)) {
            actuator.addEvent(new SelectZhuangJiaEvent(roomId));
        //转圈
        }else if (listenerKey.startsWith(ListenerKey.ZHUAN_QUAN)) {
            scheduleDispatch.addListener(new CountDownListener(ListenerKey.XIA_ZHU + ListenerKey.SPLIT + roomId + ListenerKey.SPLIT +ListenerKey.TIME_FIVE));
        //下注
        }else if (listenerKey.startsWith(ListenerKey.XIA_ZHU)) {
            actuator.addEvent(new DefaultXiaZhuEvent(roomId));
        //摊牌倒计时
        }else if (listenerKey.startsWith(ListenerKey.TAI_PAI)) {
            scheduleDispatch.addListener(new CountDownListener(ListenerKey.SETTLE + ListenerKey.SPLIT + roomId + ListenerKey.SPLIT + ListenerKey.TIME_TWO));
        }else if ()
    }

    /**
     * 得到time
     * @return
     */
    protected Integer getTimeByKey(){
        String str[] = listenerKey.split(ListenerKey.SPLIT);
        return NumberUtil.stringFormatInteger(str[str.length-1]);
    }
}
