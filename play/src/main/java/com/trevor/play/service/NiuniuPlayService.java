package com.trevor.play.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.commom.bo.*;
import com.trevor.commom.domain.mongo.NiuniuRoomParam;
import com.trevor.commom.domain.mysql.Room;
import com.trevor.commom.enums.GameStatusEnum;
import com.trevor.commom.enums.NiuNiuPaiXingEnum;
import com.trevor.commom.service.RoomParamService;
import com.trevor.commom.service.RoomService;
import com.trevor.commom.util.JsonUtil;
import com.trevor.commom.util.PokeUtil;
import com.trevor.commom.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * @author trevor
 * @date 06/28/19 14:39
 */
@Service
@Slf4j
public class NiuniuPlayService {

    @Resource
    private RoomService roomService;

    @Resource
    private RoomParamService roomParamService;

    @Resource
    private static StringRedisTemplate redisTemplate;


    public void play(String roomIdStr){
        BoundHashOperations<String, String, String> roomBaseInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomIdStr);
        //准备的倒计时
        countDown(1002 ,GameStatusEnum.BEFORE_FAPAI_4.getCode() ,roomIdStr);
        //发4张牌
        fapai_4(roomIdStr ,JsonUtil.parse(roomBaseInfoOps.get(RedisConstant.PAIXING) ,new HashSet<>()));
        //开始抢庄倒计时
        countDown(1005 ,GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode() ,roomIdStr);
        //选取庄家
        selectZhaungJia(roomIdStr);
        try {
            Thread.sleep(2000);
        }catch (Exception e) {
            log.error(e.toString());
        }
        //闲家下注倒计时
        countDown(1007 ,GameStatusEnum.BEFORE_LAST_POKE.getCode() ,roomIdStr);
        fapai_1(roomIdStr);

    }

    /**
     * 倒计时
     * @param head
     * @param gameStatus
     */
    private void countDown(Integer head ,String gameStatus ,String roomIdStr){
        BoundHashOperations<String, String, String> roomBaseInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomIdStr);
        roomBaseInfoOps.put(RedisConstant.GAME_STATUS , gameStatus);
        for (int i = 5; i > 0 ; i--) {
            SocketResult socketResult = new SocketResult(head ,i);
            broadcast(socketResult ,roomIdStr);
        }
        try {
            Thread.sleep(500);
        }catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 发4张牌
     * @param roomId
     * @param paiXing
     */
    private void fapai_4(String roomId ,Set<Integer> paiXing){
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size() ,
                    redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId).range(0 ,-1).size() * 5);
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
                PaiXing niu_16 = isNiu_16(pokes, paiXing);
                if (niu_16 != null) {
                    niu_16_nums ++;
                }
            }
            if (niu_16_nums < 2) {
                twoWuXiaoNiu = false;
            }
        }
        //设置每个人的牌
        Map<String ,List<String>> userPokeMap = new HashMap<>(2<<4);
        BoundHashOperations<String, String, String> pokesOps = redisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        BoundListOperations<String, String> readyPlayerOps = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
        List<String> readyPlayerUserIds = readyPlayerOps.range(0 ,-1);
        for (int j=0 ;j<readyPlayerUserIds.size();j++) {
            userPokeMap.put(readyPlayerUserIds.get(j) ,pokesList.get(j).subList(0 ,4));
            pokesOps.put(readyPlayerUserIds.get(j) ,JsonUtil.toJsonString(pokesList.get(j)));
        }
        //改变状态
        BoundHashOperations<String, String, String> roomBaseInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        roomBaseInfoOps.put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_QIANGZHUANG_COUNTDOWN.getCode());
        //给每个人发牌
        SocketResult socketResult = new SocketResult(1004 ,userPokeMap);
        broadcast(socketResult ,roomId);
    }

    /**
     * 选取庄家
     * @param roomId
     */
    private void selectZhaungJia(String roomId){
        BoundHashOperations<String, String, String> qiangZhuangUserIds = redisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        Integer randNum = 0;
        BoundListOperations<String, String> zhuangJiaOps = redisTemplate.boundListOps(RedisConstant.ZHUANGJIA + roomId);
        String zhuangJiaUserId = "";
        //没人抢庄
        if (qiangZhuangUserIds == null || qiangZhuangUserIds.size() == 0) {
            BoundListOperations<String, String> readyPlayerUserIds = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
            randNum = RandomUtils.getRandNumMax(readyPlayerUserIds.size().intValue());
            zhuangJiaUserId = readyPlayerUserIds.range(0 ,-1).get(randNum);
            qiangZhuangUserIds.put(zhuangJiaUserId ,"1");
            zhuangJiaOps.rightPush(zhuangJiaUserId);
        }else {
            randNum = RandomUtils.getRandNumMax(qiangZhuangUserIds.size().intValue());
            List<String> userIds = new ArrayList<>(qiangZhuangUserIds.keys());
            zhuangJiaUserId = userIds.get(randNum);
            zhuangJiaOps.rightPush(zhuangJiaUserId);
        }

        SocketResult socketResult = new SocketResult(1006 ,zhuangJiaUserId);
        broadcast(socketResult ,roomId);
        //改变状态
        redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode());
    }

    /**
     * 发一张牌
     */
    private void fapai_1(String roomId){
        redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_TABPAI_COUNTDOWN.getCode());
        BoundHashOperations<String, String, String> pokesOps = redisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        Map<String ,List<String>> userPokeMap = new HashMap<>(2<<4);
        Map<String, String> map = pokesOps.entries();
        for (Map.Entry<String ,String> entry : map.entrySet()) {
            userPokeMap.put(entry.getKey() ,JsonUtil.parse(entry.getValue() ,new ArrayList<String>()).subList(4,5));
        }
        SocketResult socketResult = new SocketResult(1008 ,userPokeMap);
        broadcast(socketResult ,roomId);
    }

    /**
     * 是否是五小牛 10 倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_16(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(6)) {
            int num = 0;
            boolean glt_5 = true;
            for (String str : pokes) {
                String pai = str.substring(1 ,2);
                num += changePai(pai);
                if (changePai(pai) < 5) {
                    glt_5 = false;
                    break;
                }
            }
            if (num <= 10 && glt_5) {
                paiXing = new PaiXing();
                paiXing.setMultiple(10);
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_16.getPaiXingCode());
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 比大小
     * @param pai
     * @return
     */
    private Integer changePai(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 11;
        }else if (Objects.equals("c" ,pai)) {
            return 12;
        }else if (Objects.equals("d" ,pai)) {
            return 13;
        }else {
            return Integer.valueOf(pai);
        }
    }

    /**
     * 广播消息
     * @param socketResult
     * @param roomIdStr
     */
    private void broadcast(SocketResult socketResult ,String roomIdStr){
        BoundListOperations<String, String> roomPlayerOps = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER + roomIdStr);
        if (roomPlayerOps != null && roomPlayerOps.size() > 0) {
            List<String> playerIds = roomPlayerOps.range(0, -1);
            for (String playerId : playerIds) {
                redisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + playerId).rightPush(JsonUtil.toJsonString(socketResult));
            }
        }
    }

}
