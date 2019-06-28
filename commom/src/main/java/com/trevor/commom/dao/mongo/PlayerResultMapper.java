package com.trevor.commom.dao.mongo;

import com.trevor.commom.domain.mongo.PlayerResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerResultMapper extends MongoRepository<PlayerResult,String> {

    @Query(value = "{'userId' : ?0 ,'entryTime' : {$gte : ?1}}")
    List<PlayerResult> findByUserId(Long userId ,Long entryTime);

}
