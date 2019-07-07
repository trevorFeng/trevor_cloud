package com.trevor.commom.dao.mongo;

import com.trevor.commom.domain.mongo.NiuniuRoomParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author trevor
 * @date 07/01/19 11:03
 */
@Repository
public interface NiuniuRoomParamMapper extends MongoRepository<NiuniuRoomParam,String> {

    @Override
    <S extends NiuniuRoomParam> S save(S s);

    @Query("{'roomId':{$in:?0}}")
    List<NiuniuRoomParam> findByRoomIds(List<Long> roomIdS);
}
