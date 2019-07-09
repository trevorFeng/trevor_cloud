package com.trevor.common.service;


import com.trevor.common.dao.mysql.CardConsumRecordMapper;
import com.trevor.common.domain.mysql.CardConsumRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 16:23
 */
@Service
public class CardConsumRecordService {

    private CardConsumRecordMapper cardConsumRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long insertOne(CardConsumRecord cardConsumRecord){
        return cardConsumRecordMapper.insertOne(cardConsumRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoomRecordIds(List<Long> roomIds){
        cardConsumRecordMapper.deleteByRoomRecordIds(roomIds);
    }


}
