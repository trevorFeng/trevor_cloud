package com.trevor.common.service;

import com.trevor.common.dao.mongo.PlayerResultMapper;
import com.trevor.common.domain.mongo.PlayerResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerResultService {

    @Resource
    private PlayerResultMapper playerResultMapper;

    /**
     * 查询玩家15天内的记录
     * @param userId
     * @return
     */
    public List<PlayerResult> findByUserId(String userId){
        Long entryTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 15;
        List<PlayerResult> playerResults = playerResultMapper.findByUserId(Long.valueOf(userId) ,entryTime);
        List<PlayerResult> sorted = playerResults.stream().sorted((p1, p2) -> p2.getEntryTime().compareTo(p1.getEntryTime()))
                .collect(Collectors.toList());
        return sorted;
    }

}
