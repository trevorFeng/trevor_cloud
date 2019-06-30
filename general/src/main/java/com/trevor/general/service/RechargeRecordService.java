package com.trevor.general.service;

import com.trevor.commom.bo.JsonEntity;
import com.trevor.commom.bo.RechargeCard;
import com.trevor.commom.bo.ResponseHelper;
import com.trevor.commom.dao.mysql.PersonalCardMapper;
import com.trevor.commom.dao.mysql.RechargeRecordMapper;
import com.trevor.commom.domain.mysql.RechargeRecord;
import com.trevor.commom.enums.MessageCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
@Service
@Slf4j
public class RechargeRecordService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 为玩家充值
     * @param rechargeCard
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Object> rechargeCard(RechargeCard rechargeCard) {
        Long userId = rechargeCard.getUserId();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUserId(userId);
        rechargeRecord.setRechargeCard(rechargeCard.getCardNum());
        rechargeRecord.setUnitPrice(rechargeCard.getUnitPrice());
        rechargeRecord.setTotalPrice(rechargeCard.getTotalPrice());
        rechargeRecord.setTime(System.currentTimeMillis());
        rechargeRecordMapper.insertOne(rechargeRecord);

        Integer hasCardNum = personalCardMapper.findCardNumByUserId(userId);
        personalCardMapper.updatePersonalCardNum(userId ,hasCardNum + rechargeCard.getCardNum());

        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }
}
