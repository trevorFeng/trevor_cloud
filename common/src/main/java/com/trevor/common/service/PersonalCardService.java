package com.trevor.common.service;

import com.trevor.common.dao.mysql.PersonalCardMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Auther: trevor
 * @Date: 2019\4\16 0016 23:16
 * @Description:
 */
@Service
public class PersonalCardService {

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 根据玩家查询玩家拥有的房卡数量
     * @param userId
     * @return
     */
    public Integer findCardNumByUserId(Long userId) {
        return personalCardMapper.findCardNumByUserId(userId);
    }
}
