package com.trevor.core.service;

import com.trevor.core.listener.niuniu.FaPai_4Listener;
import org.springframework.stereotype.Service;

@Service
public class NiuniuService {

    /**
     * 房间里只有两个人，开始游戏，注册发4张牌事件
     * @param roomId
     */
    public void playTwo(String roomId){
        FaPai_4Listener faPai_4Listener = new FaPai_4Listener();
    }
}
