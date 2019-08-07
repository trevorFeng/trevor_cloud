package com.trevor.play.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.dao.mongo.PlayerResultMapper;
import com.trevor.common.domain.mongo.PlayerResult;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.enums.NiuNiuPaiXingEnum;
import com.trevor.common.service.RedisService;
import com.trevor.common.service.UserService;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
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
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PlayerResultMapper playerResultMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;


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
        Integer rule = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.RULE));
        List<Integer> paiXing = JsonUtil.parseJavaList(
                redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.PAIXING) ,Integer.class);
        Integer basePoint = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.BASE_POINT));
        //发4张牌
        fapai_4(roomIdStr ,paiXing);
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
        Map<String ,PaiXing> paiXingMap = new HashMap<>();
        Map<String ,Integer> scoreMap = new HashMap<>(2<<4);
        setScore(roomIdStr
                ,paiXing
                ,rule
                ,basePoint
                ,scoreMap
                ,paiXingMap);
        //保存结果
        List<PlayerResult> playerResults = generatePlayerResults(roomIdStr);
        playerResultMapper.saveAll(playerResults);
        //给玩家发送分数、玩家发送其他人的最后一张牌,玩家的牌型
        sendResultToUser(roomIdStr ,scoreMap ,paiXingMap);
        //删除redis的键
        deleteKeys(roomIdStr);
        continueOrStop(roomIdStr);
    }

    /**
     * 倒计时
     * @param head
     * @param gameStatus
     */
    private void countDown(Integer head ,String gameStatus ,String roomIdStr){
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.GAME_STATUS , gameStatus);
        for (int i = 5; i > 0 ; i--) {
            SocketResult socketResult = new SocketResult(head ,i);
            broadcast(socketResult ,roomIdStr);
            try {
                Thread.sleep(1000);
            }catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    /**
     * 发4张牌
     * @param roomId
     * @param paiXing
     */
    private void fapai_4(String roomId ,List<Integer> paiXing){
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
        Map<String ,List<String>> userPokeMap = new HashMap<>(2<<4);
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        int num = 0;
        for (String s : readyPlayerUserIds) {
            userPokeMap.put(s ,pokesList.get(num).subList(0 ,4));
            redisService.put(RedisConstant.POKES + roomId ,s ,JsonUtil.toJsonString(pokesList.get(num)));
            num ++;
        }
        //改变状态
        BoundHashOperations<String, String, String> roomBaseInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
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
        BoundHashOperations<String, String, String> qiangZhuangUserIds = stringRedisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        Map<String, String> qiangZhuangMap = qiangZhuangUserIds.entries();
        Integer randNum = 0;
        BoundValueOperations<String, String> zhuangJiaOps = stringRedisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId);
        String zhuangJiaUserId;
        //没人抢庄
        if (qiangZhuangMap.isEmpty()) {
            BoundSetOperations<String, String> readyPlayerOps = stringRedisTemplate.boundSetOps(RedisConstant.READY_PLAYER + roomId);
            randNum = RandomUtils.getRandNumMax(readyPlayerOps.size().intValue());
            List<String> playerIds = Lists.newArrayList();
            Set<String> members = readyPlayerOps.members();
            for (String s : members) {
                playerIds.add(s);
            }
            zhuangJiaUserId = playerIds.get(randNum);
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
        stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode());
    }

    /**
     * 发一张牌
     */
    private void fapai_1(String roomId){
        stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_TABPAI_COUNTDOWN.getCode());
        BoundHashOperations<String, String, String> pokesOps = stringRedisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        Map<String ,List<String>> userPokeMap = new HashMap<>(2<<4);
        Map<String, String> map = pokesOps.entries();
        for (Map.Entry<String ,String> entry : map.entrySet()) {
            userPokeMap.put(entry.getKey() ,JsonUtil.parseJavaList(entry.getValue() ,String.class).subList(4,5));
        }
        SocketResult socketResult = new SocketResult(1008 , null,userPokeMap);
        broadcast(socketResult ,roomId);
    }

    /**
     * 給玩家返回得分和最后一张牌
     */
    private void sendResultToUser(String roomId ,Map<String ,Integer> scoreMap ,Map<String ,PaiXing> paiXingMap){
        stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_RETURN_RESULT.getCode());
        SocketResult socketResult = new SocketResult();
        socketResult.setHead(1012);
        socketResult.setScoreMap(scoreMap);
        BoundSetOperations<String, String> tanPaiOps = stringRedisTemplate.boundSetOps(RedisConstant.TANPAI + roomId);
        BoundHashOperations<String, String, String> pokesOps = stringRedisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        Set<String> tanPaiPlayers = tanPaiOps.members();

        Map<String ,List<String>> userPokeMap_1 = new HashMap<>();
        Map<String, String> userPokeStrMap = pokesOps.entries();
        for (Map.Entry<String ,String> entry : userPokeStrMap.entrySet()) {
            if (!tanPaiPlayers.contains(entry.getKey())) {
                userPokeMap_1.put(entry.getKey() ,JsonUtil.parseJavaList(entry.getValue() ,String.class).subList(4 ,5));
            }
        }
        socketResult.setUserPokeMap_1(userPokeMap_1);
        Map<String ,Integer> paiXing = new HashMap<>();
        for (Map.Entry<String ,PaiXing> entry : paiXingMap.entrySet()) {
            paiXing.put(entry.getKey() ,entry.getValue().getPaixing());
        }
        socketResult.setPaiXing(paiXing);
        broadcast(socketResult ,roomId);
    }

    private List<PlayerResult> generatePlayerResults(String roomId){
        Long entryDatetime = System.currentTimeMillis();
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        Map<String ,String> baseRoomInfoMap = baseRoomInfoOps.entries();
        BoundHashOperations<String, String, String> scoreOps = stringRedisTemplate.boundHashOps(RedisConstant.SCORE + roomId);
        Map<String ,String> scoreMap = scoreOps.entries();
        BoundSetOperations<String, String> readyPlayerOps = stringRedisTemplate.boundSetOps(RedisConstant.READY_PLAYER + roomId);
        Set<String> readyPlayerStr = readyPlayerOps.members();
        List<Long> readyPlayerLong = readyPlayerStr.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
        List<User> users = userService.findUsersByIds(readyPlayerLong);
        BoundValueOperations<String, String> zhuangJiaOps = stringRedisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId);
        String zhuangJiaId = zhuangJiaOps.get();
        BoundHashOperations<String, String, String> totalSocreOps = stringRedisTemplate.boundHashOps(RedisConstant.TOTAL_SCORE + roomId);
        Map<String ,String> totalScoreMap = totalSocreOps.entries();
        BoundHashOperations<String, String, String> pokesOps = stringRedisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        Map<String ,String> pokesMap = pokesOps.entries();
        BoundHashOperations<String, String, String> paiXingOps = stringRedisTemplate.boundHashOps(RedisConstant.PAI_XING + roomId);
        Map<String ,String> paiXingMap = paiXingOps.entries();
        List<PlayerResult> playerResults = new ArrayList<>();
        for (User user : users) {
            PlayerResult playerResult = new PlayerResult();
            Long userId = user.getId();
            String userIdStr = String.valueOf(user.getId());
            //玩家id
            playerResult.setUserId(userId);
            //房间id
            playerResult.setRoomId(Long.valueOf(roomId));
            //第几局
            playerResult.setGameNum(Integer.valueOf(baseRoomInfoMap.get(RedisConstant.RUNING_NUM)));
            //本局得分情况
            playerResult.setScore(Integer.valueOf(scoreMap.get(userIdStr)));
            //是否是庄家
            if (Objects.equals(zhuangJiaId ,userIdStr)) {
                playerResult.setIsZhuangJia(Boolean.TRUE);
            }else {
                playerResult.setIsZhuangJia(Boolean.FALSE);
            }
            //设置总分
            playerResult.setTotalScore(Integer.valueOf(totalScoreMap.get(userIdStr)));
            //设置牌
            playerResult.setPokes(JsonUtil.parseJavaList(pokesMap.get(userIdStr) ,String.class));
            //设置牌型
            PaiXing paiXing = JsonUtil.parseJavaObject(paiXingMap.get(userIdStr) ,PaiXing.class);
            playerResult.setPaiXing(paiXing.getPaixing());
            //设置倍数
            playerResult.setPaiXing(paiXing.getMultiple());
            //设置时间
            playerResult.setEntryTime(entryDatetime);
            playerResults.add(playerResult);
        }
        return playerResults;
    }

    private void deleteKeys(String roomId){
        stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_DELETE_KEYS.getCode());
        List<String> keys = new ArrayList<>();
        keys.add(RedisConstant.POKES + roomId);
        keys.add(RedisConstant.READY_PLAYER + roomId);
        keys.add(RedisConstant.QIANGZHAUNG + roomId);
        keys.add(RedisConstant.ZHUANGJIA + roomId);
        keys.add(RedisConstant.TANPAI + roomId);
        keys.add(RedisConstant.XIANJIA_XIAZHU + roomId);
        keys.add(RedisConstant.SCORE + roomId);
        keys.add(RedisConstant.PAI_XING + roomId);
        stringRedisTemplate.delete(keys);
    }

    /**
     * 继续开始或者停止
     */
    private void continueOrStop(String roomId){
        BoundHashOperations<String, String, String> baseRoomInfoOps = stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
        Integer runingNum = Integer.valueOf(baseRoomInfoOps.get(RedisConstant.RUNING_NUM));
        Integer totalNum = Integer.valueOf(baseRoomInfoOps.get(RedisConstant.TOTAL_NUM));
        stringRedisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId).put(RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_READY.getCode());
        //结束
        if (Objects.equals(runingNum ,totalNum)) {
            SocketResult socketResult = new SocketResult(1013);
            broadcast(socketResult ,roomId);
        }else {
            Integer next = runingNum + 1;
            baseRoomInfoOps.put(RedisConstant.RUNING_NUM ,String.valueOf(next));
            SocketResult socketResult = new SocketResult();
            socketResult.setHead(1016);
            socketResult.setRuningAndTotal(next + "/" + totalNum);
            broadcast(socketResult ,roomId);
        }




    }


    private void setScore(String roomId ,List<Integer> paiXing ,Integer rule ,Integer basePoint
            ,Map<String ,Integer> scoreMap ,Map<String ,PaiXing> paiXingMap){
        String zhuangJiaUserId = stringRedisTemplate.boundValueOps(RedisConstant.ZHUANGJIA + roomId).get();
        BoundHashOperations<String, String, String> qiangZhuangOps = stringRedisTemplate.boundHashOps(RedisConstant.QIANGZHAUNG + roomId);
        BoundHashOperations<String, String, String> xianJiaXiaZhuOps = stringRedisTemplate.boundHashOps(RedisConstant.XIANJIA_XIAZHU + roomId);
        BoundHashOperations<String, String ,String> pokes = stringRedisTemplate.boundHashOps(RedisConstant.POKES + roomId);
        BoundHashOperations<String, String, String> scoreOps = stringRedisTemplate.boundHashOps(RedisConstant.SCORE + roomId);
        BoundHashOperations<String, String, String> totalScoreOps = stringRedisTemplate.boundHashOps(RedisConstant.TOTAL_SCORE + roomId);
        BoundHashOperations<String, String, String> paiXingOps = stringRedisTemplate.boundHashOps(RedisConstant.PAI_XING + roomId);
        List<String> zhuangJiaPokes = JsonUtil.parseJavaList(pokes.get(zhuangJiaUserId) ,String.class);
        PaiXing zhuangJiaPaiXing = PokeUtil.isNiuNiu(zhuangJiaPokes , paiXing ,rule);
        Integer zhuangJiaScore = 0;
        paiXingMap.put(zhuangJiaUserId ,zhuangJiaPaiXing);
        Integer zhuangJiaQiangZhuang = qiangZhuangOps.get(zhuangJiaUserId) == null ? 1 : Integer.valueOf(qiangZhuangOps.get(zhuangJiaUserId));
        for (Map.Entry<String ,String> entry : pokes.entries().entrySet()) {
            if (!Objects.equals(entry.getKey() ,zhuangJiaUserId)) {
                List<String> xianJiaPokes = JsonUtil.parseJavaList(entry.getValue() ,String.class);
                PaiXing xianJiaPaiXing = PokeUtil.isNiuNiu(xianJiaPokes ,paiXing ,rule);
                paiXingOps.put(entry.getKey() ,JsonUtil.toJsonString(xianJiaPaiXing));
                paiXingMap.put(entry.getKey() ,xianJiaPaiXing);
                Integer xianJiaQiangZhuang = xianJiaXiaZhuOps.get(entry.getKey()) == null ? 1 : Integer.valueOf(xianJiaXiaZhuOps.get(entry.getKey()));
                Integer score = zhuangJiaQiangZhuang * xianJiaQiangZhuang * basePoint;
                Integer xianJiaTotalScore = totalScoreOps.get(entry.getKey()) == null ? 0 : Integer.valueOf(totalScoreOps.get(entry.getKey()));
                //庄家大于闲家
                if (zhuangJiaPaiXing.getPaixing() > xianJiaPaiXing.getPaixing()) {
                    score = score * zhuangJiaPaiXing.getMultiple();
                    zhuangJiaScore += score;
                    scoreOps.put(entry.getKey() ,String.valueOf(-score));

                    totalScoreOps.put(entry.getKey() ,String.valueOf(xianJiaTotalScore - score));
                    scoreMap.put(entry.getKey() ,-score);
                    //庄家小于闲家
                }else if (zhuangJiaPaiXing.getPaixing() < xianJiaPaiXing.getPaixing()) {
                    score = score * xianJiaPaiXing.getMultiple();
                    zhuangJiaScore -= score;
                    scoreOps.put(entry.getKey() ,String.valueOf(-score));
                    totalScoreOps.put(entry.getKey() ,String.valueOf(xianJiaTotalScore + score));
                    scoreMap.put(entry.getKey() ,score);
                }else{
                    boolean zhuangJiaDa = true;
                    //炸弹牛，比炸弹大小(已经设置不可能出现两个五小牛)
                    if (Objects.equals(zhuangJiaPaiXing ,NiuNiuPaiXingEnum.NIU_15.getPaiXingCode())){
                        if (!PokeUtil.niu_15_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //葫芦牛，比3张牌一样的大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_14.getPaiXingCode())) {
                        if (!PokeUtil.niu_14_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //同花牛，先比花色大小，再比牌值大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_13.getPaiXingCode())) {
                        if (!PokeUtil.niu_13_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //五花牛，比最大牌，再比花色 //顺子牛，比最大牌，再比花色//比最大牌，最后比花色
                    }else {
                        //倒叙排，比大小
                        Integer paiZhi = PokeUtil.biPaiZhi(zhuangJiaPokes, xianJiaPokes);
                        if (Objects.equals(paiZhi ,1)) {
                            zhuangJiaDa = true;
                        }else if (Objects.equals(-1 ,paiZhi)) {
                            zhuangJiaDa = false;
                        }else {
                            List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> PokeUtil.changePai(str.substring(1 ,2))
                            ).collect(Collectors.toList());
                            Map<String ,String> zhuangJiaMap = Maps.newHashMap();
                            for (String zhuang : zhuangJiaPokes) {
                                zhuangJiaMap.put(zhuang.substring(1 ,2) ,zhuang.substring(0 ,1));
                            }
                            List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> PokeUtil.changePai(str.substring(1 ,2))
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
                        totalScoreOps.put(entry.getKey() ,String.valueOf(xianJiaTotalScore - score));
                    }else {
                        score = score * xianJiaPaiXing.getMultiple();
                        zhuangJiaScore += score;
                        scoreOps.put(entry.getKey() ,String.valueOf(score));
                        totalScoreOps.put(entry.getKey() ,String.valueOf(xianJiaTotalScore + score));
                        scoreMap.put(entry.getKey() ,score);
                    }
                }

            }
        }
        Integer zhuangJiaTotalScore = totalScoreOps.get(zhuangJiaUserId) == null ? 0 : Integer.valueOf(totalScoreOps.get(zhuangJiaUserId));
        paiXingOps.put(zhuangJiaUserId ,JsonUtil.toJsonString(zhuangJiaPaiXing));
        scoreOps.put(zhuangJiaUserId ,String.valueOf(zhuangJiaScore));
        scoreMap.put(zhuangJiaUserId ,zhuangJiaScore);
        totalScoreOps.put(zhuangJiaUserId ,String.valueOf(zhuangJiaTotalScore + zhuangJiaScore));
    }

    /**
     * 广播消息
     * @param socketResult
     * @param roomIdStr
     */
    private void broadcast(SocketResult socketResult ,String roomIdStr){
        BoundSetOperations<String, String> roomPlayerOps = stringRedisTemplate.boundSetOps(RedisConstant.ROOM_PLAYER + roomIdStr);
        Set<String> playerIds = roomPlayerOps.members();
        for (String playerId : playerIds) {
            stringRedisTemplate.boundListOps(RedisConstant.MESSAGES_QUEUE + playerId).rightPush(JsonUtil.toJsonString(socketResult));
        }

    }

}
