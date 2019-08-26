package com.trevor.message.core.event.niuniu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.NumberUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.common.util.RandomUtils;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.event.Event;
import com.trevor.message.core.listener.niuniu.CountDownListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FaPai4Event extends Event {


    public FaPai4Event(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {
        //生成牌
        List<List<String>> pokesList = getPokesList();
        //设置每个人的牌
        setPlayersPoke(pokesList);
        //改变房间状态
        redisService.put(RedisConstant.BASE_ROOM_INFO, RedisConstant.GAME_STATUS, GameStatusEnum.FA_FOUR_PAI.getCode());
        //发牌
        faPai();
        //注册抢庄倒计时
        scheduleDispatch.addListener(new CountDownListener(ListenerKey.QIANG_ZHAUNG));
    }

    private void sendGameStatus(String gameStatus ,String roomId) {

        //给玩家发状态信息
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1019);

        messageHandle.broadcast(socketResult, roomId);
    }

    private void faPai(){
        Set<String> roomPlayer = redisService.getSetMembers(RedisConstant.ROOM_PLAYER + roomId);
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        Map<String, String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
        //给每个人发牌
        for (String playerId : roomPlayer) {
            if (readyPlayerUserIds.contains(playerId)) {
                List<String> userPokeList_4 = JsonUtil.parseJavaList(pokesMap.get(playerId), String.class).subList(0, 4);
                SocketResult soc = new SocketResult(1004, userPokeList_4);
                soc.setGameStatus(GameStatusEnum.FA_FOUR_PAI.getCode());
                messageHandle.sendMessage(soc, playerId);
            } else {
                SocketResult soc = new SocketResult(1004);
                soc.setGameStatus(GameStatusEnum.FA_FOUR_PAI.getCode());
                messageHandle.sendMessage(soc, playerId);
            }
        }
    }

    private void setPlayersPoke(List<List<String>> pokesList) {
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        int num = 0;
        Map<String, String> pokeMap = Maps.newHashMap();
        for (String playerId : readyPlayerUserIds) {
            List<String> pokes = pokesList.get(num);
            pokeMap.put(playerId, JsonUtil.toJsonString(pokes));
            num++;
        }
        redisService.putAll(RedisConstant.POKES + roomId, pokeMap);
    }

    private List<List<String>> getPokesList() {
        List<Integer> paiXing = JsonUtil.parseJavaList(
                redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId, RedisConstant.PAIXING), Integer.class);
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size(),
                    redisService.getSetSize(RedisConstant.READY_PLAYER + roomId) * 5);
            //生成牌
            pokesList = Lists.newArrayList();
            for (List<Integer> integers : lists) {
                List<String> stringList = Lists.newArrayList();
                integers.forEach(index -> {
                    stringList.add(rootPokes.get(index));
                });
                pokesList.add(stringList);
            }
            int niu_16_nums = 0;
            for (List<String> pokes : pokesList) {
                PaiXing niu_16 = PokeUtil.isNiu_16(pokes, paiXing);
                if (niu_16 != null) {
                    niu_16_nums++;
                }
            }
            if (niu_16_nums < 2) {
                twoWuXiaoNiu = false;
            }
        }

        return pokesList;
    }
}
