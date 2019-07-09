package com.trevor.general.service;

import com.trevor.common.bo.BizKeys;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.dao.mysql.CardTransMapper;
import com.trevor.common.dao.mysql.PersonalCardMapper;
import com.trevor.common.domain.mysql.CardTrans;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author trevor
 * @date 2019/3/8 17:05
 */
@Service
public class CardTransService{

    @Resource
    private CardTransMapper cardTransMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 生成房卡包
     * @param cardNum
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<String> createCardPackage(Integer cardNum , User user) {
        //判断玩家房卡数量是否大于交易的房卡数
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(user.getId());
        if (cardNumByUserId < cardNum) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
        }
        //生成房卡交易，插入数据库
        CardTrans cardTrans = new CardTrans();
        cardTrans.generateCardTransBase(user,cardNum);
        cardTransMapper.insertOne(cardTrans);
        //减去玩家拥有的房卡数量
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId - cardNum);
        return ResponseHelper.createInstance(cardTrans.getTransNum() ,MessageCodeEnum.CREATE_SUCCESS);
    }

    /**
     * 领取房卡包
     * @param transNum
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Object> receiveCardPackage(String transNum , User user) {
        //将交易关闭
        Long termNum = cardTransMapper.closeTrans(transNum ,System.currentTimeMillis() , user.getId() , user.getAppName());
        if (!Objects.equals(BizKeys.ONE_UPDATE ,termNum)) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.TRANS_CLOSE);
        }
        //更新玩家房卡
        Integer cardNum = cardTransMapper.findCardNumByTransNo(transNum);
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(user.getId());
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId + cardNum);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.RECEIVE_SUCCESS);
    }

    /**
     * 查询发出的房卡
     * @return
     */
    public JsonEntity<List<CardTrans>> findSendCardRecord(User user) {
        List<CardTrans> cardTrans = this.cardTransMapper.findSendCardRecord(user.getId());
        return ResponseHelper.createInstance(cardTrans ,MessageCodeEnum.QUERY_SUCCESS);
    }

    /**
     * 查询收到的房卡
     * @return
     */
    public JsonEntity<List<CardTrans>> findRecevedCardRecord(User user) {
        List<CardTrans> recevedCardRecord = this.cardTransMapper.findRecevedCardRecord(user.getId());
        return ResponseHelper.createInstance(recevedCardRecord ,MessageCodeEnum.QUERY_SUCCESS);
    }
}
