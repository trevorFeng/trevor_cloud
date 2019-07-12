package com.trevor.message.service;

import com.trevor.common.bo.RedisConstant;
import com.trevor.common.bo.SocketResult;
import com.trevor.message.socket.NiuniuSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author trevor
 * @date 06/27/19 18:01
 */
@Slf4j
@Service
public class RoomService {

    public static ConcurrentHashMap<String , NiuniuSocket> sockets = new ConcurrentHashMap<>(2<<11);

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private static StringRedisTemplate redisTemplate;

    @PreDestroy
    public void destory(){
        Iterator<NiuniuSocket> iterator = sockets.values().iterator();
        while (iterator.hasNext()) {
            NiuniuSocket socket = iterator.next();
            socket.flush();
            socket.stop();
        }
    }

    /**
     * fixedRate设置的上一个任务的开始时间到下一个任务开始时间的间隔
     * fixedDelay是设定上一个任务结束后多久执行下一个任务，也就是fixedDelay只关心上一任务的结束时间和下一任务的开始时间
     */
    @Scheduled(initialDelay = 1000 * 30 ,fixedDelay = 1000)
    public void checkRoom(){
        Iterator<NiuniuSocket> iterator = sockets.values().iterator();
        while (iterator.hasNext()) {
            NiuniuSocket socket = iterator.next();
            socket.flush();
        }
    }

    /**
     * 房间广播
     * @param roomId
     * @param res
     */
    public void broadcast(String roomId , SocketResult res){
        executor.execute(() -> {
            List<String> playerIds = getRoomPlayers(roomId);
            for (String playId : playerIds) {
                NiuniuSocket socket = sockets.get(playId);
                if (socket != null && socket.session != null && socket.session.isOpen()) {
                    socket.sendMessage(res);
                }else {
                    if (socket != null) {
                        leave(roomId ,socket);
                    }
                }
            }
        });
    }

    /**
     * 用户离开
     * @param roomId
     * @param socket
     */
    public void leave(String roomId , NiuniuSocket socket){
        if (sockets.containsKey(socket.userId)) {
            NiuniuSocket s = sockets.get(socket.userId);
            s.close(socket.session);
            sockets.remove(socket.userId);
            subRoomPlayer(roomId ,socket.userId);
        }
    }

    /**
     * 用户加入
     * @param roomId
     * @param socket
     */
    public void join(String roomId , NiuniuSocket socket){
        if (sockets.containsKey(socket.userId)) {
            NiuniuSocket s = sockets.get(socket.userId);
            s.sendMessage(new SocketResult(500));
            s.close(socket.session);
            sockets.remove(socket.userId);
        }
        addRoomPlayer(roomId , socket.userId);
        sockets.put(socket.userId , socket);
    }

    /**
     * 添加玩家
     * @param roomId
     * @param userId
     */
    public void addRoomPlayer(String roomId ,String userId) {
        BoundListOperations<String, String> ops = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER + roomId);
        ops.rightPush(userId);
    }

    /**
     * 减少玩家
     * @param roomId
     * @param userId
     */
    public void subRoomPlayer(String roomId ,String userId){
        //移除玩家id
        BoundListOperations<String, String> ops = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER + roomId);
        //移除指定个数的值
        ops.remove(1 ,userId);
        //删除消息通道
        redisTemplate.delete(RedisConstant.MESSAGES_QUEUE + userId);
    }

    /**
     * 得到玩家集合
     * @param roomId
     * @return
     */
    public List<String> getRoomPlayers(String roomId){
        BoundListOperations<String, String> ops = redisTemplate.boundListOps(RedisConstant.ROOM_PLAYER);
        List<String> range = ops.range(0, -1);
        return range;
    }
}
