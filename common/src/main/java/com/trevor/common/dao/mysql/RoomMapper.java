package com.trevor.common.dao.mysql;

import com.trevor.common.domain.mysql.Room;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/7 12:50
 */
@Repository
public interface RoomMapper {


    List<Room> findStatus(@Param("statusList") List<Integer> statusList);


    /**
     * 查询状态为0的room
     * @return
     */
    List<Long> findByEntryTimeAndStatus_0(@Param("entryTime") Long entryTime);

    /**
     * 将状态改为3
     * @param roomIds
     */
    void updateStatus_3(@Param("roomIds") List<Long> roomIds);

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
     * 根据ids查询房间，只包含
     *          id,
     *         roomAuth,
     *         roomConfig
     * @param ids
     * @return
     */
    List<Room> findByIds(@Param("ids") List<Long> ids);

    void updateRuningNum(@Param("roomId") Long roomId ,@Param("runingNum") Integer runingNum);

    void updateStatus(@Param("roomId") Long roomId ,@Param("status") Integer status ,@Param("runingNum") Integer runingNum);
}
