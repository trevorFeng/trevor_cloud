package com.trevor.general.init;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.dao.mongo.NiuniuRoomParamMapper;
import com.trevor.common.dao.mysql.RoomMapper;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.Room;
import com.trevor.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author trevor
 * @date 05/14/19 17:58
 */
@Component
@Slf4j
public class Init implements ApplicationRunner {


    @Resource
    private RoomMapper roomMapper;

    @Resource
    private NiuniuRoomParamMapper niuniuRoomParamMapper;

    @Resource
    private RedisService redisService;
    
    


    /**
     * 初始化roomPoke到roomPokeMap中,初始化sessionsMap
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        statusList.add(1);
        List<Room> rooms = roomMapper.findStatus(statusList);
        if (rooms.isEmpty()) {
            return;
        }
        List<Long> roomIds = rooms.stream().map(room -> room.getId()).collect(Collectors.toList());
        List<NiuniuRoomParam> niuniuParams = niuniuRoomParamMapper.findByRoomIds(roomIds);
        Map<Long, Integer> totalNumMap = rooms.stream().collect(Collectors.toMap(Room::getId, Room::getTotalNum));
        Map<Long, Integer> runingNumMap = rooms.stream().collect(Collectors.toMap(Room::getId, Room::getRuningNum));

        for (NiuniuRoomParam niuniuRoomParam : niuniuParams) {
            String roomId = niuniuRoomParam.getRoomId().toString();

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

            Map<String ,String> baseRoomInfoMap = niuniuRoomParam.generateBaseRoomInfoMap();
            baseRoomInfoMap.put(RedisConstant.TOTAL_NUM ,totalNumMap.get(Long.valueOf(roomId)).toString());
            baseRoomInfoMap.put(RedisConstant.RUNING_NUM ,runingNumMap.get(Long.valueOf(roomId)).toString());
            redisService.putAll(RedisConstant.BASE_ROOM_INFO + roomId ,baseRoomInfoMap);
        }
    }
}
