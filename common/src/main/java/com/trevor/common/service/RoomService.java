package com.trevor.common.service;

import com.trevor.common.dao.mysql.RoomMapper;
import com.trevor.common.domain.mysql.Room;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/7 15:57
 */
@Service
public class RoomService  {

    @Resource
    private RoomMapper roomMapper;

    /**
     * 根据主键查询一条记录
     * @param id
     * @return
     */
    public Room findOneById(Long id) {
        return roomMapper.findOneById(id);
    }


    /**
     * 根据开放记录id查询开房人的id
     * @param roomId
     * @return
     */
    public Long findRoomAuthIdByRoomId(Long roomId) {
        return roomMapper.findRoomAuthIdByRoomId(roomId);
    }

    public void updateRuningNum(Long roomId ,Integer runingNum){
        roomMapper.updateRuningNum(roomId ,runingNum);
    }

    public void updateStatus(Long roomId ,Integer status ,Integer runingNum){
        roomMapper.updateStatus(roomId ,status ,runingNum);
    }
}
