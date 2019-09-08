package com.trevor.message.game;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameCore {

    /**
     * 全部房间的游戏数据
     */
    private static Map<String ,RoomData> map = Maps.newConcurrentMap();

    public void putRoomData(RoomData roomData ,String roomId){
        map.put(roomId ,roomData);
    }

    public void removeRoomData(String roomId){
        map.remove(roomId);
    }

    public RoomData getRoomData(String roomId){
        return map.get(roomId);
    }

    public void execut(Task task){

    }

    public void executNiuniu(){

    }
}
