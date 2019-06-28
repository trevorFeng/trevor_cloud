package com.trevor.message.service;

import com.trevor.message.bo.SocketResult;
import com.trevor.message.server.NiuniuServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trevor
 * @date 06/27/19 18:01
 */
@Slf4j
@Service
public class RoomService {

    public static ConcurrentHashMap<String , NiuniuServer> sockets = new ConcurrentHashMap<>(2<<11);

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
    public void broadcast(Long roomId , SocketResult res){

    }

    public void leave(Long roomId ,SocketResult socketResult){

    }
}
