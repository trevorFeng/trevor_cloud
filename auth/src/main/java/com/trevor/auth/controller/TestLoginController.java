package com.trevor.auth.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.auth.bo.TestLogin;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.dao.mysql.PersonalCardMapper;
import com.trevor.common.domain.mysql.PersonalCard;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.UserService;
import com.trevor.common.util.RandomUtils;
import com.trevor.common.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "测试用暂时登录" ,description = "测试用暂时登录")
@RestController
@Slf4j
public class TestLoginController {

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @ApiOperation("只需点一下就可以登录了，转到/api/login/user获取用户信息")
    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<TestLogin> weixinAuth(){
        String openid = System.currentTimeMillis() + "";
        String hash = RandomUtils.getRandomChars(20);

        List<String> tupianList = Lists.newArrayList();
        tupianList.add("http://hbimg.b0.upaiyun.com/fc4285d30a2d667304b9ef3c0d820b97a6e402933669d-QfsNiI_fw658");
        tupianList.add("http://pic31.nipic.com/20130725/2929309_105611417128_2.jpg");
        tupianList.add("http://img3.imgtn.bdimg.com/it/u=4098886459,2746584588&fm=26&gp=0.jpg");
        tupianList.add("http://img3.imgtn.bdimg.com/it/u=809705136,1148759487&fm=26&gp=0.jpg");
        tupianList.add("http://img5.imgtn.bdimg.com/it/u=3275347102,446490913&fm=26&gp=0.jpg");

        User user = new User();
        user.setOpenid(openid);
        user.setHash(hash);
        user.setAppName(RandomUtils.getRandNum());
        user.setAppPictureUrl(tupianList.get(RandomUtils.getRandNumMax(tupianList.size())));

        user.setType(1);
        user.setFriendManageFlag(0);
        userService.insertOne(user);
        log.info("测试登录成功 ，hash值---------" + hash);

        PersonalCard personalCard = new PersonalCard();
        personalCard.setUserId(user.getId());
        personalCard.setRoomCardNum(0);

        personalCardMapper.insertOne(personalCard);


        Map<String, Object> claims = Maps.newHashMap();
        claims.put("openid" ,openid);
        claims.put("hash" ,hash);
        claims.put("timestamp" ,System.currentTimeMillis());
        String token = TokenUtil.generateToken(claims);

        TestLogin testLogin = new TestLogin();
        testLogin.setToken(token);
        testLogin.setUserId(user.getId());
        return ResponseHelper.createInstance(testLogin , MessageCodeEnum.HANDLER_SUCCESS);
    }
}
