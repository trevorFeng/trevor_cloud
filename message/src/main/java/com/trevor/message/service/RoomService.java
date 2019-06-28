package com.trevor.message.service;

import com.trevor.commom.bo.SocketResult;
import com.trevor.message.bo.Player;
import com.trevor.message.bo.SocketResult;
import com.trevor.message.server.NiuniuServer;
import lombok.extern.slf4j.Slf4j;
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

    public static ConcurrentHashMap<String , NiuniuServer> sockets = new ConcurrentHashMap<>(2<<11);

    @Resource(name = "executor")
    private Executor executor;

    @PreDestroy
    public void destory(){
        Iterator<NiuniuServer> iterator = sockets.values().iterator();
        while (iterator.hasNext()) {
            NiuniuServer niuniuServer = iterator.next();
            //niuniuServer.f
        }
    }

    /**
     * 房间广播
     * @param roomId
     * @param res
     */
    public void broadcast(String roomId , SocketResult res){
        executor.execute(() -> {
            leave(roomId ,new NiuniuServer());
        });
    }

    /**
     * 用户离开
     * @param roomId
     * @param socket
     */
    public void leave(String roomId ,NiuniuServer socket){
        if (sockets.containsKey(socket.userId)) {
            NiuniuServer s = sockets.get(socket.userId);
            s.close(socket.session);
            sockets.remove(socket.userId);
            subRoomPlayer(roomId ,socket.userId);
        }
    }

    /**
     * 用户加入
     * @param roomId
     * @param niuniuServer
     */
    public void join(String roomId ,NiuniuServer niuniuServer){
        if (sockets.containsKey(niuniuServer.roomId)) {
            NiuniuServer s = sockets.get(niuniuServer.roomId);
            s.sendMessage(new SocketResult(500));
            s.close(niuniuServer.session);
            sockets.remove(niuniuServer.userId);
        }
        addRoomPlayer(roomId ,niuniuServer.userId);
        sockets.put(niuniuServer.userId ,niuniuServer);
    }

    public void addRoomPlayer(String roomId ,String userId) {
        Player player = new Player();
        //player.set
    }

    public void subRoomPlayer(String roomId ,String userId){

    }

    public List<Player> getRoomPlayers(String roomId){

        return null;
    }
}
