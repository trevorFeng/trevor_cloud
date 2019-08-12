package com.trevor.auth.controller;


import com.trevor.auth.service.XianliaoService;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.RandomUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "闲聊登陆相关" ,description = "闲聊登陆相关")
@RestController
public class XianliaoLoginController {

    @Resource
    private XianliaoService xianliaoService;

    @Resource
    private RedisService redisService;

    @ApiOperation("得到用户临时凭证uuid")
    @RequestMapping(value = "/front/xianliao/login/uuid", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> xianliaoForward()  {
        //用户临时凭证
        String uuid = RandomUtils.getRandomChars(40);
        redisService.setValueWithExpire(uuid ,uuid ,60L, TimeUnit.SECONDS);
        return ResponseHelper.createInstance(uuid , MessageCodeEnum.CREATE_SUCCESS);
    }

    @ApiOperation("根据code码请求用户信息")
    @RequestMapping(value = "/front/xianliao/login/user", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> checkAuth(@RequestParam("uuid") String uuid , @RequestParam("code") String code) throws IOException {
        if (redisService.getValue(uuid) == null) {
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.ERROR_NUM_MAX);
        }
        JsonEntity<String> jsonEntity = xianliaoService.weixinAuth(code);
        //授权成功
        if(jsonEntity.getCode() > 0){
            //SessionUtil.setToken(jsonEntity.getData());
            return jsonEntity;
        }else {
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.AUTH_FAILED);
        }
    }

}
