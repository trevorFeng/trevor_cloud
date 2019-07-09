package com.trevor.general.init;

import com.alibaba.fastjson.JSON;
import com.trevor.common.bo.RedisConstant;
import com.trevor.common.dao.mongo.NiuniuRoomParamMapper;
import com.trevor.common.dao.mysql.RoomMapper;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private StringRedisTemplate redisTemplate;
    
    


    /**
     * 初始化roomPoke到roomPokeMap中,初始化sessionsMap
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        List<Room> rooms = roomMapper.findStatus_0();
        List<Long> roomIds = rooms.stream().map(room -> room.getId()).collect(Collectors.toList());
        List<NiuniuRoomParam> niuniuParams = niuniuRoomParamMapper.findByRoomIds(roomIds);
        Map<Long, Integer> collect = rooms.stream().collect(Collectors.toMap(Room::getId, Room::getTotalNum));

        for (NiuniuRoomParam niuniuRoomParam : niuniuParams) {
            String roomId = niuniuRoomParam.getRoomId().toString();
            redisTemplate.delete(RedisConstant.BASE_ROOM_INFO + roomId);

            BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(RedisConstant.BASE_ROOM_INFO + roomId);
            ops.put(RedisConstant.ROOM_TYPE ,String.valueOf(niuniuRoomParam.getRoomType()));
            ops.put(RedisConstant.ROB_ZHUANG_TYPE ,String.valueOf(niuniuRoomParam.getRobZhuangType()));
            ops.put(RedisConstant.BASE_POINT ,String.valueOf(niuniuRoomParam.getBasePoint()));
            ops.put(RedisConstant.RULE ,String.valueOf(niuniuRoomParam.getRule()));
            ops.put(RedisConstant.XIAZHU ,String.valueOf(niuniuRoomParam.getXiazhu()));
            ops.put(RedisConstant.SPECIAL , JSON.toJSONString(niuniuRoomParam.getSpecial()));
            ops.put(RedisConstant.PAIXING ,JSON.toJSONString(niuniuRoomParam.getPaiXing()));

            ops.put(RedisConstant.GAME_STATUS ,"1");
            ops.put(RedisConstant.RUNING_NUM ,"0");
            ops.put(RedisConstant.TOTAL_NUM ,collect.get(roomId).toString());
        }
    }
}
