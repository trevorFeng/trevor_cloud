package com.trevor.general.task;

import com.google.common.collect.Lists;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.ReturnCard;
import com.trevor.common.dao.mongo.NiuniuRoomParamMapper;
import com.trevor.common.dao.mysql.CardConsumRecordMapper;
import com.trevor.common.dao.mysql.PersonalCardMapper;
import com.trevor.common.dao.mysql.RoomMapper;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.PersonalCard;
import com.trevor.common.domain.mysql.Room;
import com.trevor.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author trevor
 * @date 05/16/19 17:51
 */
@Service
@Slf4j
public class TaskService{

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private NiuniuRoomParamMapper niuniuRoomParamMapper;

    @Resource
    private RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    public void checkRoomRecord() {
        Long currentTime = System.currentTimeMillis();
        Long Hour_12_Before = currentTime - 1000 * 60 * 60 * 12;
        //超过12小时未使用的房间ids
        List<Long> overDayRoomIds = roomMapper.findByEntryTimeAndStatus_0(Hour_12_Before);
        log.info("超过12小时未使用的房间ids：" + overDayRoomIds.toString() );
        roomMapper.updateStatus_3(overDayRoomIds);
        //删除redis中的key
        for (Long roomId : overDayRoomIds) {
            for (Long redisUnUserRoomId : overDayRoomIds) {
                if (Objects.equals(roomId ,redisUnUserRoomId)) {
                    redisService.delete(RedisConstant.BASE_ROOM_INFO + roomId);
                }
            }
        }

        //返回房卡
        List<Room> rooms = roomMapper.findByIds(overDayRoomIds);
        //删除房卡消费记录
        cardConsumRecordMapper.deleteByRoomRecordIds(overDayRoomIds);
        //返回房卡
        List<Long> userIds = rooms.stream().map(r -> r.getRoomAuth()).collect(Collectors.toList());
        List<PersonalCard> personalCards = personalCardMapper.findByUserIds(userIds);
        Map<Long, Integer> personalCardMap = personalCards.stream().collect(Collectors.toMap(PersonalCard::getUserId, PersonalCard::getRoomCardNum));
        List<ReturnCard> returnCards = Lists.newArrayList();
        rooms.forEach(room -> {
            ReturnCard returnCard = new ReturnCard();
            returnCard.setUserId(room.getRoomAuth());
            NiuniuRoomParam niuniuRoomParameter = niuniuRoomParamMapper.findByRoomId(room.getId());
            if (Objects.equals(niuniuRoomParameter.getConsumCardNum() ,1)) {
                returnCard.setReturnCardNum(personalCardMap.get(room.getRoomAuth()) + 3);
            }else {
                returnCard.setReturnCardNum(personalCardMap.get(room.getRoomAuth()) + 6);
            }
            returnCards.add(returnCard);
        });
        personalCardMapper.updatePersonalCardNumByUserIds(returnCards);
    }
}
