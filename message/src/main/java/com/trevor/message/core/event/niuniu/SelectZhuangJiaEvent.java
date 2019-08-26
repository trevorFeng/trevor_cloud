package com.trevor.message.core.event.niuniu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.common.util.RandomUtils;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;

import java.util.*;

/**
 * 发送庄家事件
 */
public class
SelectZhuangJiaEvent extends Event {

    public SelectZhuangJiaEvent(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {
        Map<String, String> qiangZhuangMap = redisService.getMap(RedisConstant.QIANGZHAUNG + roomId);

        String zhuangJiaUserId;
        List<String> qiangZhuangZhuanQuanList = Lists.newArrayList();
        //没人抢庄
        if (qiangZhuangMap.isEmpty()) {
            zhuangJiaUserId = noPeopleQiangZhuang();
        }else {
            //一个人抢庄
            if (qiangZhuangMap.size() == 1) {
                zhuangJiaUserId = onePeopleQiangZhuang(qiangZhuangMap);
            //多人抢庄
            }else {
                List<Integer> beiShus = Lists.newArrayList();
                for (String beiShuStr : qiangZhuangMap.values()) {
                    beiShus.add(NumberUtil.stringFormatInteger(beiShuStr));
                }
                //升序排列
                Collections.reverse(beiShus);
                List<String> maxBeiShuPlayerIds = Lists.newArrayList();
                Integer maxBeiShu = beiShus.get(0);
                for (Map.Entry<String ,String> entry : qiangZhuangMap.entrySet()) {
                    if (Objects.equals(NumberUtil.stringFormatInteger(entry.getValue()) ,maxBeiShu)) {
                        maxBeiShuPlayerIds.add(entry.getKey());
                    }
                }
                Integer maxPlayerNum = maxBeiShuPlayerIds.size();
                if (maxPlayerNum == 1) {
                    zhuangJiaUserId = maxBeiShuPlayerIds.get(0);
                }else {
                    Integer randNum = RandomUtils.getRandNumMax(maxPlayerNum);
                    zhuangJiaUserId = maxBeiShuPlayerIds.get(randNum);
                    qiangZhuangZhuanQuanList = maxBeiShuPlayerIds;
                }
            }
        }
        //设置庄家
        redisService.setValue(RedisConstant.ZHUANGJIA + roomId ,zhuangJiaUserId);
        //改变状态
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS , GameStatusEnum.QIANG_ZHUANG_ZHUAN_QUAN.getCode());
        SocketResult socketResult = new SocketResult(1006 ,zhuangJiaUserId);
        Boolean noZhuanQuan = qiangZhuangZhuanQuanList.isEmpty();
        if (!noZhuanQuan) {
            socketResult.setZhuanQuanPlayers(qiangZhuangZhuanQuanList);
        }
        socketResult.setGameStatus(GameStatusEnum.QIANG_ZHUANG_ZHUAN_QUAN.getCode());
        messageHandle.broadcast(socketResult ,roomId);
        //注册转圈倒计时
        if (!noZhuanQuan) {
            scheduleDispatch.addListener(new CountDownListener(ListenerKey.ZHUAN_QUAN + ListenerKey.SPLIT + ListenerKey.TIME_TWO));
        }
    }

    /**
     * 没人抢庄
     */
    private String noPeopleQiangZhuang(){
        Integer randNum = RandomUtils.getRandNumMax(redisService.getSetSize(RedisConstant.READY_PLAYER + roomId));
        List<String> playerIds = Lists.newArrayList();
        Set<String> members = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        for (String s : members) {
            playerIds.add(s);
        }
        return playerIds.get(randNum);

    }

    /**
     * 一个人抢庄
     * @param qiangZhuangMap
     * @return
     */
    private String onePeopleQiangZhuang(Map<String, String> qiangZhuangMap){
        Iterator<String> iterator = qiangZhuangMap.keySet().iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

}
