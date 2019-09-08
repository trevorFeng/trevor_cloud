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


    public FaPai4Event(String roomId ,String runingNum) {
        super.roomId = roomId;
        super.runingNum = runingNum;
    }

    @Override
    protected void executeEvent() {
        redisService.setValue(RedisConstant.getGameStatus(roomId ,runingNum) ,GameStatusEnum.FA_FOUR_PAI.getCode());
        //生成牌
        List<List<String>> pokesList = getPokesList();
        //设置每个人的牌
        Map<String, String> pokesMap = Maps.newHashMap();
        setPlayersPoke(pokesList ,pokesMap);
        //改变房间状态
        //发牌并发送房间状态
        faPai(pokesMap);
        //注册抢庄倒计时
        scheduleDispatch.addListener(new CountDownListener(ListenerKey.getQiangZhaungKey(roomId ,runingNum ,5)));
    }

    private void faPai(Map<String, String> pokesMap){
        Set<String> roomPlayer = redisService.getSetMembers(RedisConstant.getRoomPlayer(roomId));
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.getReadyPlayer(roomId ,runingNum));
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

    private void setPlayersPoke(List<List<String>> pokesList ,Map<String, String> map) {
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.getReadyPlayer(roomId ,runingNum));
        int num = 0;
        Map<String, String> pokeMap = Maps.newHashMap();
        for (String playerId : readyPlayerUserIds) {
            List<String> pokes = pokesList.get(num);
            pokeMap.put(playerId, JsonUtil.toJsonString(pokes));
            num++;
        }
        map = pokeMap;
        redisService.putAll(RedisConstant.getPokes(roomId ,runingNum), pokeMap);
    }

    private List<List<String>> getPokesList() {
        List<Integer> paiXing = JsonUtil.parseJavaList(
                redisService.getHashValue(RedisConstant.getBaseRoomInfo(roomId), RedisConstant.PAIXING), Integer.class);
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size(),
                    redisService.getSetSize(RedisConstant.getReadyPlayer(roomId ,runingNum)) * 5);
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
