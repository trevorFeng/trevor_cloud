package com.trevor.commom.dao.mongo;

import com.trevor.commom.domain.mongo.NiuniuRoomParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author trevor
 * @date 07/01/19 11:03
 */
@Repository
public interface NiuniuRoomParamMapper extends MongoRepository<NiuniuRoomParam,String> {
}
