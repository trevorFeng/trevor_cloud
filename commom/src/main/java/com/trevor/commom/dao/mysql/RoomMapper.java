package com.trevor.commom.dao.mysql;

import com.trevor.commom.domain.mysql.Room;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/7 12:50
 */
@Repository
public interface RoomMapper {

    List<Room> findStatus_0();

    /**
     * 根据主键查询一条记录
     * @param id
     * @return
     */
    Room findOneById(@Param("id") Long id);

    /**
     * 插入一条记录并返回主键
     * @param room
     * @return
     */
    Long insertOne(@Param("room") Room room);

    /**
     * 根据开放记录id查询开房人的id
     * @param roomId
     * @return
     */
    Long findRoomAuthIdByRoomId(@Param("roomId") Long roomId);

    /**
     * 超过半小时未使用的房间ids
     * @param time
     * @return
     */
    List<Long> findByGetRoomTimeAndState_1(@Param("time") Long time);

    /**
     * 关闭房间，将房间状态置位0（已过期）
     * @param ids
     */
    void updateState_0(@Param("ids") List<Long> ids);

    /**
     * 根据ids查询房间，只包含
     *          id,
     *         roomAuth,
     *         roomConfig
     * @param ids
     * @return
     */
    List<Room> findByIds(@Param("ids") List<Long> ids);
}
