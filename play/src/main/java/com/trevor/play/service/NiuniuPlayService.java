package com.trevor.play.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.commom.bo.PaiXing;
import com.trevor.commom.bo.RedisConstant;
import com.trevor.commom.bo.RoomPoke;
import com.trevor.commom.bo.SocketResult;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
        Long roomId = Long.valueOf(roomIdStr);
        BoundHashOperations<String, String, String> roomBaseInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomIdStr);
        BoundListOperations<String, String> roomPlayerOps = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER + roomIdStr);
        String runingNum = roomBaseInfoOps.get(RedisConstant.RUNING_NUM);
        String totalNum = roomBaseInfoOps.get(RedisConstant.TOTAL_NUM);

        //准备的倒计时
        countDown(1002 ,GameStatusEnum.BEFORE_FAPAI_4.getCode() ,roomBaseInfoOps ,roomPlayerOps);


    }

    /**
     * 倒计时
     * @param head
     * @param gameStatus
     * @param roomBaseInfoOps
     * @param roomPlayerOps
     */
    private void countDown(Integer head ,String gameStatus ,BoundHashOperations<String, String, String> roomBaseInfoOps
            ,BoundListOperations<String, String> roomPlayerOps){
        roomBaseInfoOps.put(RedisConstant.GAME_STATUS , gameStatus);
        for (int i = 5; i > 0 ; i--) {
            SocketResult socketResult = new SocketResult(head ,i);
            List<String> playerIds = roomPlayerOps.range(0, -1);
            for (String playerId : playerIds) {
                redisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + playerId).rightPush(JsonUtil.toJsonString(socketResult));
            }
        }
        try {
            Thread.sleep(500);
        }catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void fapai_4(String roomId ,RoomPoke roomPoke ,NiuniuRoomParam niuniuRoomParam){
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size() ,redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId).range(0 ,-1).size() * 5);
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
                PaiXing niu_16 = isNiu_16(pokes, niuniuRoomParameter.getPaiXing());
                if (niu_16 != null) {
                    niu_16_nums ++;
                }
            }
            if (niu_16_nums < 2) {
                twoWuXiaoNiu = false;
            }
        }
        //设置每个人的牌
        for (int j = 0; j < userPokeList.size(); j++) {
            UserPoke userPoke = userPokeList.get(j);
            userPoke.setPokes(pokesList.get(j));
        }
        //给每个人发牌
        /**
         * 加读锁
         */
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        roomPoke.getGameStatusLock().writeLock().lock();
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_QIANGZHUANG_COUNTDOWN.getCode());
        roomPoke.getGameStatusLock().writeLock().unlock();

        //设置realWanJias
        roomPoke.getRealWanJiaLock().lock();
        List<RealWanJiaInfo> realWanJias = roomPoke.getRealWanJias();
        userPokeList.forEach(u -> {
            for (RealWanJiaInfo realWanJiaInfo : realWanJias) {
                if (Objects.equals(u.getUserId() ,realWanJiaInfo.getId())) {
                    realWanJiaInfo.setPokes(u.getPokes().subList(0,4));
                    break;
                }
            }
        });
        roomPoke.getRealWanJiaLock().unlock();

        Map<Long ,List<String>> pokeMap = Maps.newHashMap();
        for (UserPoke userPoke : userPokeList) {
            pokeMap.put(userPoke.getUserId() ,userPoke.getPokes());
        }
        ReturnMessage< Map<Long ,List<String>>> returnMessage3 = new ReturnMessage<>(pokeMap,4);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage3);
        /**
         * 加读锁结束
         */
        leaderReadLock.unlock();
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

}
