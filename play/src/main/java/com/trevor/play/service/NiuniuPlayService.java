package com.trevor.play.service;

import com.trevor.commom.bo.RedisConstant;
import com.trevor.commom.bo.RoomPoke;
import com.trevor.commom.bo.SocketResult;
import com.trevor.commom.domain.mongo.NiuniuRoomParam;
import com.trevor.commom.domain.mysql.Room;
import com.trevor.commom.enums.GameStatusEnum;
import com.trevor.commom.service.RoomParamService;
import com.trevor.commom.service.RoomService;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Set;

/**
 * @author trevor
 * @date 06/28/19 14:39
 */
@Service
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
        roomBaseInfoOps.put(RedisConstant.GAME_STATUS , GameStatusEnum.BEFORE_FAPAI_4.getCode());
        SocketResult socketResult = new SocketResult()

    }

    private void excute(String roomId){
        BoundListOperations<String, String> ops = redisTemplate.boundListOps(RedisConstant.BASE_ROOM_INFO + roomId);
        ops.rightPush()
    }

    /**
     * 倒计时
     */
    protected void countDown(Set<Session> sessions , RoomPoke roomPoke , Integer messageCode , Integer gameStatus) {


        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,messageCode);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }

        leaderReadLock.unlock();
    }


}
