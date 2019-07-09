package com.trevor.general.controller;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.domain.mysql.CardTrans;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.util.ThreadLocalUtil;
import com.trevor.general.service.CardTransService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 16:50
 */
@Api(value = "房卡相关接口" ,description = "房卡相关接口(领取，创建，查询收到或发出)")
@RestController("/admin")
@Validated
public class CardTransController {

    @Resource
    private CardTransService cardTransService;

    @ApiOperation(value = "创建房卡包")
    @ApiImplicitParam(name = "cardNum" ,value = "房卡的数量" , required = true ,paramType = "path" ,dataType = "int")
    @RequestMapping(value = "/api/cardTrans/create/package/{cardNum}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> createCardPackage(@PathVariable("cardNum") @Min(value = 1 ,message = "最小值为1") Integer cardNum){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<String> jsonEntity = cardTransService.createCardPackage(cardNum , user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    @ApiOperation(value = "领取房卡包")
    @ApiImplicitParam(name = "transNo" ,value = "交易号" , required = true ,paramType = "path" ,dataType = "string")
    @RequestMapping(value = "/api/cardTrans/receive/package/{transNo}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> createCardPackage(@PathVariable("transNo") @NotBlank(message = "交易号不能为空") String transNo){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> jsonEntity = cardTransService.receiveCardPackage(transNo , user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    @ApiOperation(value = "查询发出的房卡")
    @RequestMapping(value = "/api/cardTrans/send/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findSendCardRecord(){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<List<CardTrans>> jsonEntity = cardTransService.findSendCardRecord(user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    @ApiOperation(value = "查询收到的房卡")
    @RequestMapping(value = "/api/cardTrans/query/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findRecevedCardRecord(){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<List<CardTrans>> jsonEntity = cardTransService.findRecevedCardRecord(user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

}
