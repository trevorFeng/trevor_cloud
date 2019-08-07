package com.trevor.general.service;

import com.alibaba.fastjson.JSON;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.dao.mongo.NiuniuRoomParamMapper;
import com.trevor.common.dao.mysql.CardConsumRecordMapper;
import com.trevor.common.dao.mysql.PersonalCardMapper;
import com.trevor.common.dao.mysql.RoomMapper;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.CardConsumRecord;
import com.trevor.common.domain.mysql.Room;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.ConsumCardEnum;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.RedisService;
import com.trevor.general.exception.BizException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author trevor
 * @date 2019/3/8 16:53
 */
@Service
public class CreateRoomService{

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NiuniuRoomParamMapper niuniuRoomParamMapper;

    @Resource
    private RedisService redisService;


    /**
     * 创建一个房间,返回主键,将房间放入Map中
     * @param niuniuRoomParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Long> createRoom(NiuniuRoomParam niuniuRoomParam , User user) {
        checkParm(niuniuRoomParam);
        //判断玩家拥有的房卡数量是否超过消耗的房卡数
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(user.getId());
        Integer consumCardNum;
        if (Objects.equals(niuniuRoomParam.getConsumCardNum() , ConsumCardEnum.GAME_NUM_12_CARD_3.getCode())) {
            consumCardNum = ConsumCardEnum.GAME_NUM_12_CARD_3.getConsumCardNum();
            if (cardNumByUserId < ConsumCardEnum.GAME_NUM_12_CARD_3.getConsumCardNum()) {
                return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
            }
        }else {
            consumCardNum = ConsumCardEnum.GAME_NUM_24_CARD_6.getConsumCardNum();
            if (cardNumByUserId < ConsumCardEnum.GAME_NUM_24_CARD_6.getConsumCardNum()) {
                return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
            }
        }
        //生成房间，将房间信息存入数据库
        Integer totalNum;
        if (Objects.equals(consumCardNum ,ConsumCardEnum.GAME_NUM_12_CARD_3.getConsumCardNum())) {
            totalNum = 12;
        }else {
            totalNum = 24;
        }
        Long currentTime = System.currentTimeMillis();
        Room room = new Room();
        room.generateRoomBase(niuniuRoomParam.getRoomType() ,user.getId() ,currentTime ,totalNum);
        roomMapper.insertOne(room);

        //插入mongoDB
        niuniuRoomParam.setRoomId(room.getId());
        niuniuRoomParamMapper.save(niuniuRoomParam);

        //存入redis
        Map<String ,String> baseRoomInfoMap = niuniuRoomParam.generateBaseRoomInfoMap();
        baseRoomInfoMap.put(RedisConstant.TOTAL_NUM ,totalNum.toString());
        redisService.putAll(RedisConstant.BASE_ROOM_INFO + room.getId() ,baseRoomInfoMap);

        //生成房卡消费记录
        CardConsumRecord cardConsumRecord = new CardConsumRecord();
        cardConsumRecord.generateCardConsumRecordBase(room.getId() , user.getId() ,consumCardNum);
        cardConsumRecordMapper.insertOne(cardConsumRecord);

        //更新玩家的房卡数量信息
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId - consumCardNum);
        return ResponseHelper.createInstance(room.getId() , MessageCodeEnum.CREATE_SUCCESS);
    }

    private void checkParm(NiuniuRoomParam niuniuRoomParameter){
        Integer roomType = niuniuRoomParameter.getRoomType();
        if (!Objects.equals(roomType ,1) && !Objects.equals(roomType ,2) && !Objects.equals(roomType ,3)) {
            throw new BizException(-200 ,"roomType 错误");
        }
        Integer robZhuangType = niuniuRoomParameter.getRobZhuangType();
        if (!Objects.equals(robZhuangType ,1) && !Objects.equals(robZhuangType ,2) &&
                !Objects.equals(robZhuangType ,3) && !Objects.equals(robZhuangType ,4)) {
            throw new BizException(-200 ,"robZhuangType 错误");
        }
        Integer basePoint = niuniuRoomParameter.getBasePoint();

    }


}
