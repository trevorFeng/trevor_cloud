package com.trevor.general.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.trevor.bo.ReturnCard;
import com.trevor.commom.bo.ReturnCard;
import com.trevor.commom.dao.mysql.CardConsumRecordMapper;
import com.trevor.commom.dao.mysql.PersonalCardMapper;
import com.trevor.commom.dao.mysql.RoomMapper;
import com.trevor.commom.dao.mysql.RoomPokeInitMapper;
import com.trevor.commom.domain.mongo.NiuniuRoomParam;
import com.trevor.commom.domain.mysql.PersonalCard;
import com.trevor.commom.domain.mysql.Room;
import com.trevor.domain.PersonalCard;
import com.trevor.domain.Room;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.MapUtil;
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
    private RoomPokeInitMapper roomPokeInitMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Transactional(rollbackFor = Exception.class)
    public void checkRoomRecord() {
        Long currentTime = System.currentTimeMillis();
        Long halfHourBefore = currentTime - 1000 * 60 * 30;
        //超过半小时未使用的房间ids
        List<Long> overDayRoomRecordIds = roomMapper.findByGetRoomTimeAndState_1(halfHourBefore);
        log.info("超过半小时未使用的房间ids：" + overDayRoomRecordIds.toString() );
        if (overDayRoomRecordIds.isEmpty()) {
            return;
        }
        //超过半小时并且还没有激活的roomPoke的roomRecord的ids
        // todo 如何保证这个代码在执行时一定在开始打牌任务前执行
        List<Long> overDayAndStatus_0RoomRecordIds = roomPokeInitMapper.findRoomRecordIdsStatus_0AndRoomRecordIds(overDayRoomRecordIds);
        log.info("超过半小时并且还没有激活的roomPoke的roomRecord的ids：" + overDayAndStatus_0RoomRecordIds.toString() );
        if (overDayAndStatus_0RoomRecordIds.isEmpty()) {
            return;
        }

        //关闭房间,将状态置位0
        roomMapper.updateState_0(overDayAndStatus_0RoomRecordIds);
        //将roomPokeInit的status置为3
        roomPokeInitMapper.updateStatus_3(overDayRoomRecordIds);
        //返回房卡
        List<Room> rooms = roomMapper.findByIds(overDayAndStatus_0RoomRecordIds);
        //删除房卡消费记录
        cardConsumRecordMapper.deleteByRoomRecordIds(overDayAndStatus_0RoomRecordIds);
        //返回房卡
        List<Long> userIds = rooms.stream().map(r -> r.getRoomAuth()).collect(Collectors.toList());
        List<PersonalCard> personalCards = personalCardMapper.findByUserIds(userIds);
        Map<Long, Integer> personalCardMap = personalCards.stream().collect(Collectors.toMap(PersonalCard::getUserId, PersonalCard::getRoomCardNum));
        List<ReturnCard> returnCards = Lists.newArrayList();
        rooms.forEach(roomRecord -> {
            ReturnCard returnCard = new ReturnCard();
            returnCard.setUserId(roomRecord.getRoomAuth());
            NiuniuRoomParam niuniuRoomParameter = JSON.parseObject(roomRecord.getRoomConfig() ,NiuniuRoomParameter.class);
            if (Objects.equals(niuniuRoomParameter.getConsumCardNum() ,1)) {
                returnCard.setReturnCardNum(personalCardMap.get(roomRecord.getRoomAuth()) + 3);
            }else {
                returnCard.setReturnCardNum(personalCardMap.get(roomRecord.getRoomAuth()) + 6);
            }
            returnCards.add(returnCard);
        });
        personalCardMapper.updatePersonalCardNumByUserIds(returnCards);
    }
}
