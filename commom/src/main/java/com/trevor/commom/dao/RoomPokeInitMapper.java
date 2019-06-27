package com.trevor.commom.dao;

import com.trevor.commom.domain.RoomPokeInit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author
 * @date 05/14/19 18:05
 */
@Repository
public interface RoomPokeInitMapper {

    /**
     * 生成一条roomPoke记录
     * @param roomPokeInit
     */
    void insertOne(@Param("roomPokeInit") RoomPokeInit roomPokeInit);

    /**
     * 查询status为0的roomPoke（未激活）
     * @return
     */
    List<RoomPokeInit> findStatus_0();

    /**
     * 根据status为0（未激活）和roomRecord的Ids查询
     * @param roomRecordIds
     * @return
     */
    List<Long> findRoomRecordIdsStatus_0AndRoomRecordIds(@Param("roomRecordIds") List<Long> roomRecordIds);

    /**
     * 批量将roomPoke的状态设置为3（为房间未使用关闭）
     * @param roomRecordIds
     */
    void updateStatus_3(@Param("roomRecordIds") List<Long> roomRecordIds);

    /**
     * 每局打完牌更新
     * @param roomPokeInit
     */
    void updateRoomPoke(@Param("roomPokeInit") RoomPokeInit roomPokeInit);

}
