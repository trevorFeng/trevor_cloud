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
import com.trevor.common.service.RoomService;
import com.trevor.common.service.UserService;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.PokeUtil;
import com.trevor.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
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
    private PlayerResultMapper playerResultMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;

    @Resource
    private RoomService roomService;


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
        roomService.updateStatus(Long.valueOf(roomIdStr) ,1);
        Integer rule = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.RULE));
        List<Integer> paiXing = JsonUtil.parseJavaList(
                redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.PAIXING) ,Integer.class);
        Integer basePoint = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.BASE_POINT));
        //发4张牌
        fapai_4(roomIdStr ,paiXing);
        sleep(2000);
        //开始抢庄倒计时
        countDown(1005 ,GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode() ,roomIdStr);
        sleep(2000);
        //选取庄家
        selectZhaungJia(roomIdStr);
        sleep(2000);
        //闲家下注倒计时
        countDown(1007 ,GameStatusEnum.BEFORE_LAST_POKE.getCode() ,roomIdStr);
        sleep(2000);
        //设置分数
        Map<String ,PaiXing> paiXingMap = new HashMap<>();
        Map<String ,Integer> scoreMap = new HashMap<>(2<<4);
        setScore(roomIdStr
                ,paiXing
                ,rule
                ,basePoint
                ,scoreMap
                ,paiXingMap);
        //再发一张牌
        fapai_1(roomIdStr ,scoreMap ,paiXingMap);
        //准备摊牌倒计时
        countDown(1009 ,GameStatusEnum.BEFORE_CALRESULT.getCode() ,roomIdStr);
        sleep(2000);
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomIdStr ,RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_DELETE_KEYS.getCode());
        //保存结果
        List<PlayerResult> playerResults = generatePlayerResults(roomIdStr);
        playerResultMapper.saveAll(playerResults);
        //删除redis的键
        deleteKeysAndContinueOrStop(roomIdStr);
    }

    /**
     * 暂停
     */
    public void sleep(Integer millis){
        try {
            Thread.sleep(millis);
        }catch (Exception e) {
            log.error(e.toString());
        }
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
        Set<String> readyPlayerUserIds = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        int num = 0;
        for (String playerId : readyPlayerUserIds) {
            List<String> pokes = pokesList.get(num);
            redisService.put(RedisConstant.POKES + roomId ,playerId ,JsonUtil.toJsonString(pokes));
            num ++;
        }
        //改变状态
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_QIANGZHUANG_COUNTDOWN.getCode());
        //给每个人发牌
        for (String playerId : readyPlayerUserIds) {
            Map<String ,String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
            List<String> userPokeList_4 = JsonUtil.parseJavaList(pokesMap.get(playerId) ,String.class).subList(0 ,4);
            SocketResult socketResult = new SocketResult(1004 ,userPokeList_4);
            sendMessage(socketResult ,playerId);
        }
    }

    /**
     * 选取庄家
     * @param roomId
     */
    private void selectZhaungJia(String roomId){
        Map<String, String> qiangZhuangMap = redisService.getMap(RedisConstant.QIANGZHAUNG + roomId);
        Integer randNum = 0;
        String zhuangJiaUserId;
        //没人抢庄
        if (qiangZhuangMap.isEmpty()) {
            randNum = RandomUtils.getRandNumMax(redisService.getSetSize(RedisConstant.READY_PLAYER + roomId));
            List<String> playerIds = Lists.newArrayList();
            Set<String> members = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
            for (String s : members) {
                playerIds.add(s);
            }
            zhuangJiaUserId = playerIds.get(randNum);
            redisService.put(RedisConstant.QIANGZHAUNG + roomId ,zhuangJiaUserId ,"1");
            redisService.setValue(RedisConstant.ZHUANGJIA + roomId ,zhuangJiaUserId);
        }else {
            randNum = RandomUtils.getRandNumMax(redisService.getMapSize(RedisConstant.QIANGZHAUNG + roomId));
            List<String> userIds = new ArrayList<>(redisService.getMapKeys(RedisConstant.QIANGZHAUNG + roomId));
            zhuangJiaUserId = userIds.get(randNum);
            redisService.setValue(RedisConstant.ZHUANGJIA + roomId ,zhuangJiaUserId);
        }

        SocketResult socketResult = new SocketResult(1006 ,zhuangJiaUserId);
        broadcast(socketResult ,roomId);
        //改变状态
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode());
    }

    /**
     * 发一张牌
     */
    private void fapai_1(String roomId ,Map<String ,Integer> scoreMap ,Map<String ,PaiXing> paiXingMap){
        redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_TABPAI_COUNTDOWN.getCode());
        Map<String ,List<String>> userPokeMap_5 = new HashMap<>(2<<4);
        Map<String, String> map = redisService.getMap(RedisConstant.POKES + roomId);
        for (Map.Entry<String ,String> entry : map.entrySet()) {
            userPokeMap_5.put(entry.getKey() ,JsonUtil.parseJavaList(entry.getValue() ,String.class));
        }
        SocketResult socketResult = new SocketResult(1008 , userPokeMap_5);

        socketResult.setScoreMap(scoreMap);

        Map<String ,Integer> paiXing = new HashMap<>();
        for (Map.Entry<String ,PaiXing> entry : paiXingMap.entrySet()) {
            paiXing.put(entry.getKey() ,entry.getValue().getPaixing());
        }
        socketResult.setPaiXing(paiXing);

        broadcast(socketResult ,roomId);
    }

    private List<PlayerResult> generatePlayerResults(String roomId){
        Long entryDatetime = System.currentTimeMillis();
        Map<String ,String> baseRoomInfoMap = redisService.getMap(RedisConstant.BASE_ROOM_INFO + roomId);
        Map<String ,String> scoreMap = redisService.getMap(RedisConstant.SCORE + roomId);
        Set<String> readyPlayerStr = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        List<Long> readyPlayerLong = readyPlayerStr.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
        List<User> users = userService.findUsersByIds(readyPlayerLong);
        String zhuangJiaId = redisService.getValue(RedisConstant.ZHUANGJIA + roomId);
        Map<String ,String> totalScoreMap = redisService.getMap(RedisConstant.TOTAL_SCORE + roomId);
        Map<String ,String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
        Map<String ,String> paiXingMap = redisService.getMap(RedisConstant.PAI_XING + roomId);
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

    private void deleteKeysAndContinueOrStop(String roomId){
        List<String> keys = new ArrayList<>();
        keys.add(RedisConstant.POKES + roomId);
        keys.add(RedisConstant.READY_PLAYER + roomId);
        keys.add(RedisConstant.QIANGZHAUNG + roomId);
        keys.add(RedisConstant.ZHUANGJIA + roomId);
        keys.add(RedisConstant.TANPAI + roomId);
        keys.add(RedisConstant.XIANJIA_XIAZHU + roomId);
        keys.add(RedisConstant.SCORE + roomId);
        keys.add(RedisConstant.PAI_XING + roomId);
        redisService.deletes(keys);

        Integer runingNum = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.RUNING_NUM));
        Integer totalNum = Integer.valueOf(redisService.getHashValue(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.TOTAL_NUM));
        Boolean isOver = Objects.equals(runingNum ,totalNum);
        //结束
        if (isOver) {
            roomService.updateStatus(Long.valueOf(roomId) ,2);
            SocketResult socketResult = new SocketResult(1013);
            List<String> keyList = new ArrayList<>();
            keyList.add(RedisConstant.TOTAL_SCORE + roomId);
            keyList.add(RedisConstant.BASE_ROOM_INFO + roomId);
            keyList.add(RedisConstant.REAL_ROOM_PLAYER + roomId);
            redisService.deletes(keyList);
            broadcast(socketResult ,roomId);
        }else {
            Integer next = runingNum + 1;
            roomService.updateRuningNum(Long.valueOf(roomId) ,runingNum);
            SocketResult socketResult = new SocketResult();
            socketResult.setHead(1016);
            socketResult.setRuningAndTotal(next + "/" + totalNum);
            redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.GAME_STATUS ,GameStatusEnum.BEFORE_READY.getCode());
            redisService.put(RedisConstant.BASE_ROOM_INFO + roomId ,RedisConstant.RUNING_NUM ,String.valueOf(next));
            broadcast(socketResult ,roomId);
        }

    }


    private void setScore(String roomId ,List<Integer> paiXing ,Integer rule ,Integer basePoint
            ,Map<String ,Integer> scoreMap ,Map<String ,PaiXing> paiXingMap){
        //庄家id
        String zhuangJiaUserId = redisService.getValue(RedisConstant.ZHUANGJIA + roomId);
        //抢庄的map
        Map<String, String> qiangZhuangMap = redisService.getMap(RedisConstant.QIANGZHAUNG + roomId);
        //下注的map
        Map<String, String> xianJiaXiaZhuMap = redisService.getMap(RedisConstant.XIANJIA_XIAZHU + roomId);
        //每个玩家的牌
        Map<String ,String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
        //庄家的牌
        List<String> zhuangJiaPokes = JsonUtil.parseJavaList(pokesMap.get(zhuangJiaUserId) ,String.class);
        //庄家的牌型
        PaiXing zhuangJiaPaiXing = PokeUtil.isNiuNiu(zhuangJiaPokes , paiXing ,rule);
        Integer zhuangJiaScore = 0;
        paiXingMap.put(zhuangJiaUserId ,zhuangJiaPaiXing);
        //庄家的抢庄倍数
        Integer zhuangJiaQiangZhuang = qiangZhuangMap.get(zhuangJiaUserId) == null ? 1 : Integer.valueOf(qiangZhuangMap.get(zhuangJiaUserId));
        for (Map.Entry<String ,String> entry : pokesMap.entrySet()) {
            String xianJiaUserId = entry.getKey();
            String xianJiaPaiXingStr = entry.getValue();
            if (!Objects.equals(xianJiaUserId ,zhuangJiaUserId)) {
                List<String> xianJiaPokes = JsonUtil.parseJavaList(xianJiaPaiXingStr ,String.class);
                PaiXing xianJiaPaiXing = PokeUtil.isNiuNiu(xianJiaPokes ,paiXing ,rule);
                redisService.put(RedisConstant.PAI_XING + roomId ,xianJiaUserId ,JsonUtil.toJsonString(xianJiaPaiXing));
                paiXingMap.put(xianJiaUserId ,xianJiaPaiXing);
                //玩家的下注倍数
                Integer xianJiaQiangZhu = xianJiaXiaZhuMap.get(xianJiaUserId) == null ? 1 : Integer.valueOf(xianJiaXiaZhuMap.get(xianJiaUserId));
                //基本分数
                Integer score = zhuangJiaQiangZhuang * xianJiaQiangZhu * basePoint;
                //闲家的总分
                Integer xianJiaTotalScore = redisService.getHashValue(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId) == null ?
                        0 : Integer.valueOf(redisService.getHashValue(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId));
                //庄家大于闲家
                if (zhuangJiaPaiXing.getPaixing() > xianJiaPaiXing.getPaixing()) {
                    score = score * zhuangJiaPaiXing.getMultiple();
                    zhuangJiaScore += score;
                    redisService.put(RedisConstant.SCORE + roomId ,xianJiaUserId ,String.valueOf(-score));
                    redisService.put(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId ,String.valueOf(xianJiaTotalScore - score));
                    scoreMap.put(xianJiaUserId ,-score);
                    //庄家小于闲家
                }else if (zhuangJiaPaiXing.getPaixing() < xianJiaPaiXing.getPaixing()) {
                    score = score * xianJiaPaiXing.getMultiple();
                    zhuangJiaScore -= score;
                    redisService.put(RedisConstant.SCORE + roomId ,xianJiaUserId ,String.valueOf(-score));
                    redisService.put(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId ,String.valueOf(xianJiaTotalScore + score));
                    scoreMap.put(xianJiaUserId ,score);
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
                            List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> PokeUtil.changePai(str.substring(1 ,2))).collect(Collectors.toList());
                            Map<String ,String> zhuangJiaMap = Maps.newHashMap();
                            for (String zhuang : zhuangJiaPokes) {
                                zhuangJiaMap.put(zhuang.substring(1 ,2) ,zhuang.substring(0 ,1));
                            }
                            List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> PokeUtil.changePai(str.substring(1 ,2))).collect(Collectors.toList());
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
                        redisService.put(RedisConstant.SCORE + roomId ,xianJiaUserId ,String.valueOf(-score));
                        scoreMap.put(xianJiaUserId ,-score);
                        redisService.put(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId ,String.valueOf(xianJiaTotalScore - score));
                    }else {
                        score = score * xianJiaPaiXing.getMultiple();
                        zhuangJiaScore -= score;
                        redisService.put(RedisConstant.SCORE + roomId ,xianJiaUserId ,String.valueOf(score));
                        redisService.put(RedisConstant.TOTAL_SCORE + roomId ,xianJiaUserId ,String.valueOf(xianJiaTotalScore + score));
                        scoreMap.put(xianJiaUserId ,score);
                    }
                }

            }
        }
        //设置庄家的分数
        Map<String, String> totalScoreMap = redisService.getMap(RedisConstant.TOTAL_SCORE + roomId);
        Integer zhuangJiaTotalScore = totalScoreMap.get(zhuangJiaUserId) == null ? 0 : Integer.valueOf(totalScoreMap.get(zhuangJiaUserId));
        redisService.put(RedisConstant.PAI_XING + roomId ,zhuangJiaUserId ,JsonUtil.toJsonString(zhuangJiaPaiXing));
        redisService.put(RedisConstant.SCORE + roomId ,zhuangJiaUserId ,String.valueOf(zhuangJiaScore));
        scoreMap.put(zhuangJiaUserId ,zhuangJiaScore);
        redisService.put(RedisConstant.TOTAL_SCORE + roomId ,zhuangJiaUserId ,String.valueOf(zhuangJiaTotalScore + zhuangJiaScore));
    }


    /**
     * 广播消息
     * @param socketResult
     * @param roomIdStr
     */
    private void broadcast(SocketResult socketResult ,String roomIdStr){
        Set<String> playerIds = redisService.getSetMembers(RedisConstant.ROOM_PLAYER + roomIdStr);
        for (String playerId : playerIds) {
            redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + playerId ,JsonUtil.toJsonString(socketResult));
        }

    }

    /**
     * 给玩家发消息
     * @param socketResult
     * @param playerId
     */
    private void sendMessage(SocketResult socketResult ,String playerId){
        redisService.listRightPush(RedisConstant.MESSAGES_QUEUE + playerId ,JsonUtil.toJsonString(socketResult));
    }

}
