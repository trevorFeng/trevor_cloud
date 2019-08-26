package com.trevor.message.core.event.niuniu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.NumberUtil;
import com.trevor.common.util.RandomUtils;
import com.trevor.message.core.event.Event;

import java.util.*;

/**
 * 发送庄家事件
 */
public class SelectZhuangJiaEvent extends Event {

    public SelectZhuangJiaEvent(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {
        Map<String, String> qiangZhuangMap = redisService.getMap(RedisConstant.QIANGZHAUNG + roomId);

        String zhuangJiaUserId;
        Boolean isZhuanQuan = Boolean.FALSE;
        //没人抢庄
        if (qiangZhuangMap.isEmpty()) {
            zhuangJiaUserId = noPeopleQiangZhuang();
            isZhuanQuan = Boolean.TRUE;
        }else {
            if (qiangZhuangMap.size() == 1) {
                zhuangJiaUserId = onePeopleQiangZhuang(qiangZhuangMap);
                isZhuanQuan = Boolean.FALSE;
            }else {

            }

        }

        redisService.setValue(RedisConstant.ZHUANGJIA + roomId ,zhuangJiaUserId);

        SocketResult socketResult = new SocketResult(1006 ,zhuangJiaUserId);

        messageHandle.broadcast(socketResult ,roomId);
        //改变状态
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS , GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode());
    }

    /**
     * 没人抢庄
     */
    private String noPeopleQiangZhuang(){
        Integer randNum = RandomUtils.getRandNumMax(redisService.getSetSize(RedisConstant.READY_PLAYER + roomId));
        List<String> playerIds = Lists.newArrayList();
        Set<String> members = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        //Set<String> zhuanQuanPlayerIds = Sets.newHashSet();
        for (String s : members) {
            playerIds.add(s);
        }
        //zhuanQuanPlayerIds.addAll(members);
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

    private String manyPeopleQiangZhuang(Map<String, String> qiangZhuangMap){
        //抢庄倍数大的才能抢到庄，若一样，则随机选择
        String qiangZhuangBeiShu = null;
        //是否是同样的
        Boolean isEqualsQiangZhuangBeiShu = Boolean.TRUE;
        for (String str : qiangZhuangMap.values()) {
            if (qiangZhuangBeiShu == null) {
                qiangZhuangBeiShu = str;
            }else if (!Objects.equals(qiangZhuangBeiShu ,str)) {
                isEqualsQiangZhuangBeiShu = Boolean.FALSE;
                break;
            }
        }
        if (isEqualsQiangZhuangBeiShu) {
            Integer randNum = RandomUtils.getRandNumMax(redisService.getMapSize(RedisConstant.QIANGZHAUNG + roomId));
            List<String> userIds = new ArrayList<>(redisService.getMapKeys(RedisConstant.QIANGZHAUNG + roomId));
            return userIds.get(randNum);
        }else {
            List<Integer> qiangZhungBeiShus = Lists.newArrayList();
            for (String str : qiangZhuangMap.values()) {
                qiangZhungBeiShus.add(NumberUtil.stringFormatInteger(str));
            }
            //升序排列
            Collections.sort(qiangZhungBeiShus);

        }
    }
}
