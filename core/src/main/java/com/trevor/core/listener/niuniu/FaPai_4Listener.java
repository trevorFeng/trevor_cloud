package com.trevor.core.listener.niuniu;

import com.google.common.collect.Lists;
import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.common.util.RandomUtils;
import com.trevor.core.listener.ListenerConfig;
import com.trevor.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 发4张牌事件
 */
public class FaPai_4Listener extends ListenerConfig {

    /**
     * 格式为fapai_4_roomId
     */
    private String key;

    @Async
    @Override
    public void executeEvent() {
        String roomId = key.split("_")[key.length()-1];
        List<Integer> paiXing = JsonUtil.parseJavaList(
                redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.PAIXING) ,Integer.class);
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size() ,
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
                    niu_16_nums ++;
                }
            }
            if (niu_16_nums < 2) {
                twoWuXiaoNiu = false;
            }
        }
        //设置每个人的牌
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        int num = 0;
        for (String playerId : readyPlayerUserIds) {
            List<String> pokes = pokesList.get(num);
            redisService.put(RedisConstant.POKES + roomId ,playerId , JsonUtil.toJsonString(pokes));
            num ++;
        }
        //改变状态
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS , GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode());
        Set<String> roomPlayer = redisService.getSetMembers(RedisConstant.ROOM_PLAYER + roomId);
        //给每个人发牌
        for (String playerId : roomPlayer) {
            if (readyPlayerUserIds.contains(playerId)) {
                Map<String ,String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
                List<String> userPokeList_4 = JsonUtil.parseJavaList(pokesMap.get(playerId) ,String.class).subList(0 ,4);
                SocketResult socketResult = new SocketResult(1004 ,userPokeList_4);
                sendMessage(socketResult ,playerId);
            }else {
                SocketResult socketResult = new SocketResult(1004);
                sendMessage(socketResult ,playerId);
            }
        }
    }

    @Override
    public String getKey() {
        return key;
    }
}
