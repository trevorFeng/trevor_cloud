package com.trevor.auth.controller;

import com.trevor.auth.bo.PhoneCode;
import com.trevor.auth.service.BrowserLoginService;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.RedisService;
import com.trevor.common.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "浏览器登录相关" ,description = "浏览器登录相关")
@RestController
@Validated
public class
BrowserLoginController {

    @Resource
    private BrowserLoginService browserLoginService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Resource
    private RedisService redisService;

    @ApiOperation("生成验证码,给用户发送验证码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "phoneNum", dataType = "string", required = true, value = "phoneNum")})
    @RequestMapping(value = "/front/phone/code/{phoneNum}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> sendCode(@PathVariable("phoneNum") @Pattern (regexp = "^[0-9]{11}$" ,message = "手机号格式不正确") String phoneNum){
//        JsonEntity<String> stringJsonEntity = browserLoginService.generatePhoneCode(phoneNum);
//        if (stringJsonEntity.getCode() < 0) {
//            return stringJsonEntity;
//        }
//        String code = stringJsonEntity.getData();
        redisService.setValueWithExpire(phoneNum ,"123456" ,60*5L , TimeUnit.SECONDS);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.SEND_MESSAGE);
    }

    @ApiOperation("校验用户的验证码是否正确")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "phoneCode", dataType = "PhoneCode", required = true, value = "phoneCode")})
    @RequestMapping(value = "/front/phone/code/check", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> submit(@RequestBody @Validated PhoneCode phoneCode){
        //校验验证码是否正确
        String code = redisService.getValue(phoneCode.getPhoneNum());
        if (Objects.equals(code ,phoneCode.getCode())) {
            JsonEntity<User> result = browserLoginService.getUserHashAndOpenidByPhoneNum(phoneCode.getPhoneNum());
            User user = result.getData();
            Map<String, Object> claims = new HashMap<>(2<<4);
            claims.put("hash", user.getHash());
            claims.put("openid", user.getOpenid());
            claims.put("timestamp", System.currentTimeMillis());
            String token = TokenUtil.generateToken(claims);
            return ResponseHelper.createInstance(token ,MessageCodeEnum.AUTH_SUCCESS);
        }else {
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.CODE_ERROR);
        }
    }
}
