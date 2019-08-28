package com.trevor.message.core.event.niuniu;

import com.trevor.common.bo.PaiXing;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.common.domain.mongo.PlayerResult;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.GameStatusEnum;
import com.trevor.common.util.JsonUtil;
import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.event.Event;

import java.util.*;
import java.util.stream.Collectors;

public class StopOrContinueEvent extends Event {

    public StopOrContinueEvent(String roomId) {
        super.roomId = roomId;
    }

    @Override
    protected void executeEvent() {
        Map<String, String> baseRoomInfoMap = redisService.getMap(RedisConstant.BASE_ROOM_INFO + roomId);
        //保存结果
        List<PlayerResult> playerResults = generatePlayerResults(roomId, baseRoomInfoMap);
        playerResultMapper.saveAll(playerResults);
        //删除本局的key
        deleteKeys();
        Integer runingNum = NumberUtil.stringFormatInteger(baseRoomInfoMap.get(RedisConstant.RUNING_NUM));
        Integer totalNum = NumberUtil.stringFormatInteger(baseRoomInfoMap.get(RedisConstant.TOTAL_NUM));
        Boolean isOver = Objects.equals(runingNum, totalNum);
        //结束
        if (isOver) {
            roomService.updateStatus(Long.valueOf(roomId), 2, runingNum);
            SocketResult socketResult = new SocketResult(1013);
            List<String> keyList = new ArrayList<>();
            keyList.add(RedisConstant.TOTAL_SCORE + roomId);
            keyList.add(RedisConstant.BASE_ROOM_INFO + roomId);
            keyList.add(RedisConstant.REAL_ROOM_PLAYER + roomId);
            keyList.add(RedisConstant.ROOM_PLAYER + roomId);
            redisService.deletes(keyList);
            messageHandle.broadcast(socketResult, roomId);
        } else {
            Integer next = runingNum + 1;
            roomService.updateRuningNum(Long.valueOf(roomId), runingNum);
            SocketResult socketResult = new SocketResult();
            socketResult.setHead(1016);
            socketResult.setRuningAndTotal(next + "/" + totalNum);
            redisService.put(RedisConstant.BASE_ROOM_INFO + roomId, RedisConstant.GAME_STATUS, GameStatusEnum.READY.getCode());
            redisService.put(RedisConstant.BASE_ROOM_INFO + roomId, RedisConstant.RUNING_NUM, String.valueOf(next));
            messageHandle.broadcast(socketResult, roomId);
        }
    }

    private List<PlayerResult> generatePlayerResults(String roomId, Map<String, String> baseRoomInfoMap) {
        Long entryDatetime = System.currentTimeMillis();
        Map<String, String> scoreMap = redisService.getMap(RedisConstant.SCORE + roomId);
        Set<String> readyPlayerStr = redisService.getSetMembers(RedisConstant.READY_PLAYER + roomId);
        List<Long> readyPlayerLong = readyPlayerStr.stream().map(s -> Long.valueOf(s)).collect(Collectors.toList());
        List<User> users = userService.findUsersByIds(readyPlayerLong);
        String zhuangJiaId = redisService.getValue(RedisConstant.ZHUANGJIA + roomId);
        Map<String, String> totalScoreMap = redisService.getMap(RedisConstant.TOTAL_SCORE + roomId);
        Map<String, String> pokesMap = redisService.getMap(RedisConstant.POKES + roomId);
        Map<String, String> paiXingMap = redisService.getMap(RedisConstant.PAI_XING + roomId);
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
            if (Objects.equals(zhuangJiaId, userIdStr)) {
                playerResult.setIsZhuangJia(Boolean.TRUE);
            } else {
                playerResult.setIsZhuangJia(Boolean.FALSE);
            }
            //设置总分
            playerResult.setTotalScore(Integer.valueOf(totalScoreMap.get(userIdStr)));
            //设置牌
            playerResult.setPokes(JsonUtil.parseJavaList(pokesMap.get(userIdStr), String.class));
            //设置牌型
            PaiXing paiXing = JsonUtil.parseJavaObject(paiXingMap.get(userIdStr), PaiXing.class);
            playerResult.setPaiXing(paiXing.getPaixing());
            //设置倍数
            playerResult.setPaiXing(paiXing.getMultiple());
            //设置时间
            playerResult.setEntryTime(entryDatetime);
            playerResults.add(playerResult);
        }
        return playerResults;
    }

    private void deleteKeys() {
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
    }
}
