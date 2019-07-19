package com.trevor.play.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.common.bo.*;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.enums.NiuNiuPaiXingEnum;
import com.trevor.common.service.RoomParamService;
import com.trevor.common.service.RoomService;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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


    /**
     * 房间只有两个人打牌
     * @param roomIdStr
     */
    public void playEqualTwo(String roomIdStr){
        play(roomIdStr);
    }

    /**
     * 房间里超过两个人
     * @param roomIdStr
     */
    public void playOverTwo(String roomIdStr){
        //准备的倒计时
        countDown(1002 ,GameStatusEnum.BEFORE_FAPAI_4.getCode() ,roomIdStr);
        play(roomIdStr);
    }

    private void play(String roomIdStr){
        BoundHashOperations<String, String, String> roomBaseInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomIdStr);
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
        //准备摊牌倒计时
        countDown(1009 ,GameStatusEnum.BEFORE_CALRESULT.getCode() ,roomIdStr);
        //设置分数
        Map<String ,Integer> scoreMap = new HashMap<>(2<<4);
        setScore(roomIdStr ,JsonUtil.parse(roomBaseInfoOps.get(RedisConstant.PAIXING) ,new HashSet<>())
                ,Integer.valueOf(roomBaseInfoOps.get(RedisConstant.RULE)) ,Integer.valueOf(roomBaseInfoOps.get(RedisConstant.BASE_POINT)) ,scoreMap);
        // todo 保存结果

        //给玩家发送其他人的最后一张牌
        //给玩家发送分数
        sendResultToUser(roomIdStr ,scoreMap);
        continueOrStop(roomIdStr);
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
        SocketResult socketResult = new SocketResult(1004 ,userPokeMap ,null);
        broadcast(socketResult ,roomId);
    }

    /**
     * 选取庄家
     * @param roomId
     */
    private void selectZhaungJia(String roomId){
        BoundHashOperations<String, String, String> qiangZhuangUserIds = redisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        Integer randNum = 0;
        BoundValueOperations<String, String> zhuangJiaOps = redisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId);
        String zhuangJiaUserId = "";
        //没人抢庄
        if (qiangZhuangUserIds == null || qiangZhuangUserIds.size() == 0) {
            BoundListOperations<String, String> readyPlayerUserIds = redisTemplate.boundListOps(RedisConstant.READY_PLAYER + roomId);
            randNum = RandomUtils.getRandNumMax(readyPlayerUserIds.size().intValue());
            zhuangJiaUserId = readyPlayerUserIds.range(0 ,-1).get(randNum);
            qiangZhuangUserIds.put(zhuangJiaUserId ,"1");
            zhuangJiaOps.set(zhuangJiaUserId);
        }else {
            randNum = RandomUtils.getRandNumMax(qiangZhuangUserIds.size().intValue());
            List<String> userIds = new ArrayList<>(qiangZhuangUserIds.keys());
            zhuangJiaUserId = userIds.get(randNum);
            zhuangJiaOps.set(zhuangJiaUserId);
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
        SocketResult socketResult = new SocketResult(1008 , null,userPokeMap);
        broadcast(socketResult ,roomId);
    }

    /**
     * 給玩家返回結果
     */
    private void sendResultToUser(String roomId ,Map<String ,Integer> scoreMap){
        SocketResult socketResult = new SocketResult(1012 ,scoreMap ,Boolean.TRUE);
        broadcast(socketResult ,roomId);
    }

    /**
     * 继续开始或者停止
     */
    private void continueOrStop(String roomId){
        BoundHashOperations<String, String, String> baseRoomInfoOps = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO);
        String runingNum = baseRoomInfoOps.get(RedisConstant.RUNING_NUM);
        String totalNum = baseRoomInfoOps.get(RedisConstant.TOTAL_NUM);
        //结束
        if (Objects.equals(Integer.valueOf(runingNum) ,Integer.valueOf(totalNum))) {

        }else {
            SocketResult socketResult = new SocketResult(1013 ,Boolean.TRUE);
            broadcast(socketResult ,roomId);
        }

    }


    private void setScore(String roomId ,Set<Integer> paiXing ,Integer rule ,Integer basePoint ,Map<String ,Integer> scoreMap){
        String zhuangJiaUserId = redisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId).get();
        BoundHashOperations<String, String, String> qiangZhuangOps = redisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        BoundHashOperations<String, String, String> xianJiaXiaZhuOps = redisTemplate.boundHashOps(RedisConstant.XIANJIA_XIAZHU + roomId);
        BoundHashOperations<String, String ,String> pokes = redisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        BoundHashOperations<String, String, String> scoreOps = redisTemplate.boundHashOps(RedisConstant.SCORE + roomId);
        BoundHashOperations<String, String, String> totalScoreOps = redisTemplate.boundHashOps(RedisConstant.TOTAL_SCORE + roomId);
        List<String> zhuangJiaPokes = JsonUtil.parse(pokes.get(zhuangJiaUserId) ,new ArrayList<String>());
        PaiXing zhuangJiaPaiXing = isNiuNiu(zhuangJiaPokes , paiXing ,rule);
        Integer zhuangJiaScore = 0;
        for (Map.Entry<String ,String> entry : pokes.entries().entrySet()) {
            if (!Objects.equals(entry.getKey() ,zhuangJiaUserId)) {
                List<String> xianJiaPokes = JsonUtil.parse(entry.getValue() ,new ArrayList<String>());
                PaiXing xianJiaPaiXing = isNiuNiu(xianJiaPokes ,paiXing ,rule);
                Integer score = Integer.valueOf(qiangZhuangOps.get(zhuangJiaUserId)) * Integer.valueOf(xianJiaXiaZhuOps.get(entry.getKey())) * basePoint;
                //庄家大于闲家
                if (zhuangJiaPaiXing.getPaixing() > xianJiaPaiXing.getPaixing()) {
                    score = score * zhuangJiaPaiXing.getMultiple();
                    zhuangJiaScore += score;
                    scoreOps.put(entry.getKey() ,String.valueOf(-score));
                    totalScoreOps.put(entry.getKey() ,String.valueOf(Integer.valueOf(totalScoreOps.get(entry.getKey())) - score));
                    scoreMap.put(entry.getKey() ,-score);
                    //庄家小于闲家
                }else if (zhuangJiaPaiXing.getPaixing() < xianJiaPaiXing.getPaixing()) {
                    score = score * xianJiaPaiXing.getMultiple();
                    zhuangJiaScore -= score;
                    scoreOps.put(entry.getKey() ,String.valueOf(-score));
                    totalScoreOps.put(entry.getKey() ,String.valueOf(Integer.valueOf(totalScoreOps.get(entry.getKey())) + score));
                    scoreMap.put(entry.getKey() ,score);
                }else{
                    boolean zhuangJiaDa = true;
                    //炸弹牛，比炸弹大小(已经设置不可能出现两个五小牛)
                    if (Objects.equals(zhuangJiaPaiXing ,NiuNiuPaiXingEnum.NIU_15.getPaiXingCode())){
                        if (!niu_15_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //葫芦牛，比3张牌一样的大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_14.getPaiXingCode())) {
                        if (!niu_14_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //同花牛，先比花色大小，再比牌值大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_13.getPaiXingCode())) {
                        if (!niu_13_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //五花牛，比最大牌，再比花色 //顺子牛，比最大牌，再比花色//比最大牌，最后比花色
                    }else {
                        //倒叙排，比大小
                        Integer paiZhi = biPaiZhi(zhuangJiaPokes, xianJiaPokes);
                        if (Objects.equals(paiZhi ,1)) {
                            zhuangJiaDa = true;
                        }else if (Objects.equals(-1 ,paiZhi)) {
                            zhuangJiaDa = false;
                        }else {
                            List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
                            ).collect(Collectors.toList());
                            Map<String ,String> zhuangJiaMap = Maps.newHashMap();
                            for (String zhuang : zhuangJiaPokes) {
                                zhuangJiaMap.put(zhuang.substring(1 ,2) ,zhuang.substring(0 ,1));
                            }
                            List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
                            ).collect(Collectors.toList());
                            Map<String ,String> xianJiaMap = Maps.newHashMap();
                            for (String xian : xianJiaPokes) {
                                xianJiaMap.put(xian.substring(1 ,2) ,xian.substring(0 ,1));
                            }
                            zhuangJiaNums.sort(Comparator.reverseOrder());
                            xianJiaNums.sort(Comparator.reverseOrder());
                            if (Integer.valueOf(zhuangJiaMap.get(zhuangJiaNums.get(0))) > Integer.valueOf(xianJiaMap.get(xianJiaNums.get(0)))) {
                                zhuangJiaDa = true;
                            }else {
                                zhuangJiaDa = false;
                            }
                        }
                    }
                    if (zhuangJiaDa) {
                        score = score * zhuangJiaPaiXing.getMultiple();
                        zhuangJiaScore += score;
                        scoreOps.put(entry.getKey() ,String.valueOf(-score));
                        scoreMap.put(entry.getKey() ,-score);
                        totalScoreOps.put(entry.getKey() ,String.valueOf(Integer.valueOf(totalScoreOps.get(entry.getKey())) - score));
                    }else {
                        score = score * xianJiaPaiXing.getMultiple();
                        zhuangJiaScore += score;
                        scoreOps.put(entry.getKey() ,String.valueOf(score));
                        totalScoreOps.put(entry.getKey() ,String.valueOf(Integer.valueOf(totalScoreOps.get(entry.getKey())) + score));
                        scoreMap.put(entry.getKey() ,score);
                    }
                }

            }
        }
        scoreOps.put(zhuangJiaUserId ,String.valueOf(zhuangJiaScore));
        scoreMap.put(zhuangJiaUserId ,zhuangJiaScore);
        totalScoreOps.put(zhuangJiaUserId ,String.valueOf(Integer.valueOf(totalScoreOps.get(zhuangJiaUserId)) + zhuangJiaScore));
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
     * 判断玩家的是否为牛
     *
     *      * 1---顺子牛，5倍
     *      * 2---五花牛，6倍
     *      * 3---同花牛，6倍
     *      * 4---葫芦牛，7倍
     *      * 5---炸弹牛，8倍
     *      * 6---五小牛，10倍
     *
     *      * 规则
     *      * 1---牛牛x3，牛九x2，牛八x2
     *      * 2---牛牛x4，牛九x3，牛八x2，牛7x2
     *
     * @param pokes
     * @return
     */
    public PaiXing isNiuNiu(List<String> pokes , Set<Integer> paiXingSet ,Integer rule){
        PaiXing paiXing;
        if (paiXingSet == null) {
            paiXingSet = new HashSet<>();
        }
        //是否是五小牛
        paiXing = isNiu_16(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是炸弹牛
        paiXing = isNiu_15(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是葫芦牛
        paiXing = isNiu_14(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是同花牛
        paiXing = isNiu_13(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是五花牛
        paiXing = isNiu_12(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是顺子牛
        paiXing = isNiu_11(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        int ii = 0;
        int jj = 0;
        int kk = 0;
        boolean isNiu = Boolean.FALSE;
        for (int i = 0; i < pokes.size(); i++) {
            if (i >= 3) {
                break;
            }
            for (int j = i+1; j < pokes.size(); j++) {
                for (int k = j+1; k < pokes.size(); k++) {
                    int num = changePai_10(pokes.get(i).substring(1,2)) +
                            changePai_10(pokes.get(j).substring(1 ,2)) +
                            changePai_10(pokes.get(k).substring(1 ,2));
                    if (num == 10 || num == 20 || num == 30) {
                        ii = i;
                        jj = j;
                        kk = k;
                        isNiu = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        //没牛
        if (!isNiu) {
            paiXing = new PaiXing();
            paiXing.setMultiple(1);
            paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_0.getPaiXingCode());
            return paiXing;
        }else {
            paiXing = new PaiXing();
            int num = 0;
            for (int i = 0; i < pokes.size(); i++) {
                if (i != ii && i != jj && i != kk) {
                    num += changePai_10(pokes.get(i).substring(1 ,2));
                }
            }
            // 1 - 牛牛x3，牛九x2，牛八x2
            if (Objects.equals(rule ,1)) {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    paiXing.setPaixing(num);
                    return paiXing;
                }
                //2---牛牛x4，牛九x3，牛八x2，牛7x2
            }else {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(4);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                } else if (num == 7 || num == 17) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_7.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    paiXing.setPaixing(num);
                    return paiXing;
                }
            }

        }
    }

    /**
     * 筹10
     * @param pai
     * @return
     */
    private Integer changePai_10(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 10;
        }else if (Objects.equals("c" ,pai)) {
            return 10;
        }else if (Objects.equals("d" ,pai)) {
            return 10;
        }else {
            return Integer.valueOf(pai);
        }
    }

    /**
     * 比较两个同花牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_13_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        if (Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) > Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) {
            return true;
        }else if (Objects.equals(Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) ,Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) ) {
            Integer paiZhi = biPaiZhi(zhuangJiaPokes ,xianJiaPokes);
            if (Objects.equals(paiZhi ,1)) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 是否是炸弹牛 8倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_15(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(5)) {
            int num = 0;
            String pai = pokes.get(0).substring(1,2);
            for (String str : pokes) {
                if (Objects.equals(pai ,str.substring(1 ,2))) {
                    num ++;
                }
            }
            if (num == 0 || num == 4 || num == 1 || num == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_15.getPaiXingCode());
                paiXing.setMultiple(8);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 比较两个葫芦牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_14_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,3);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,3);
        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    private Integer getDianShuNumberMap(List<String> pokes ,Integer ciShu){
        Map<String ,Integer> map = Maps.newHashMap();
        for (String poke : pokes) {
            String dianShu = poke.substring(1 ,2);
            if (!map.keySet().contains(dianShu)) {
                map.put(dianShu ,1);
            }else {
                map.put(dianShu ,map.get(dianShu) + 1);
            }
        }
        for (Map.Entry<String ,Integer> entry : map.entrySet()) {
            if (Objects.equals(ciShu ,entry.getValue())) {
                return changePai(entry.getKey());
            }
        }
        throw new RuntimeException("出现炸弹牛或葫芦牛，但是牌不对");
    }

    /**
     * 是否是五花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_12(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(2)) {
            boolean j_q_k = true;
            List<String> pais = new ArrayList<>();
            pais.add("b");
            pais.add("c");
            pais.add("d");
            for (String str : pokes) {
                if (!pais.contains(str.substring(1 ,2))) {
                    j_q_k = false;
                    break;
                }
            }
            if (j_q_k) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_12.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是葫芦牛 7倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_14(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(4)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(1 ,2));
            }
            if (set.size() <=2) {
                int num = 0;
                String pai = pokes.get(0).substring(1,2);
                for (String str : pokes) {
                    if (Objects.equals(pai ,str.substring(1 ,2))) {
                        num ++;
                    }
                }
                if (num == 2 || num == 3) {
                    paiXing = new PaiXing();
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_14.getPaiXingCode());
                    paiXing.setMultiple(7);
                    return paiXing;
                }
            }
        }
        return null;
    }

    /**
     * 是否是同花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_13(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(3)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(0,1));
            }
            if (set.size() == 1) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_13.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是顺子牛 ，5倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_11(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(1)) {
            List<Integer> paiList = Lists.newArrayList();
            Set<Integer> paiSet = new HashSet<>();
            for (String str : pokes) {
                paiList.add(changePai(str.substring(1 ,2)));
                paiSet.add(changePai(str.substring(1 ,2)));
            }
            paiList.sort(Comparator.reverseOrder());
            if (paiList.get(0) - paiList.get(4) == 4 && paiSet.size() == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_11.getPaiXingCode());
                paiXing.setMultiple(5);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 比较两个炸弹牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_15_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,4);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,4);

        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    /**
     * 比牌值大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回1 ,zhuangJiaPokes < xianJiaPokes返回-1,zhuangJiaPokes == xianJiaPokes返回0
     */
    private Integer biPaiZhi(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        zhuangJiaNums.sort(Comparator.reverseOrder());
        xianJiaNums.sort(Comparator.reverseOrder());
        for (int j = 0; j < xianJiaNums.size(); j++) {
            if (zhuangJiaNums.get(j) > xianJiaNums.get(j)) {
                return 1;
            }else if (Objects.equals(zhuangJiaNums.get(j) ,xianJiaNums.get(j))) {
                continue;
            }else {
                return -1;
            }
        }
        return 0;
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
