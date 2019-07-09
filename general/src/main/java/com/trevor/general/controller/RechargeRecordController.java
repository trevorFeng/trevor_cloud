package com.trevor.general.controller;


import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.RechargeCard;
import com.trevor.general.service.RechargeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Api(value = "为玩家充值房卡" ,description = "为玩家充值房卡")
@RestController
public class RechargeRecordController {

    @Resource
    private RechargeRecordService rechargeRecordService;

    @ApiOperation("为玩家充值房卡")
    @RequestMapping(value = "/api/recharge/card", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> rechargeCard(@RequestBody RechargeCard rechargeCard){
        return rechargeRecordService.rechargeCard(rechargeCard);
    }
}
