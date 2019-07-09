package com.trevor.auth.service;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.UserService;
import com.trevor.common.util.GetMessageCodeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author trevor
 * @date 03/22/19 13:15
 */
@Service
public class BrowserLoginService{

    @Resource
    private UserService userService;

    /**
     * 生成验证码发给用户
     * @param phoneNum
     * @return
     */
    public JsonEntity<String> generatePhoneCode(String phoneNum) {
        //检查手机号是否已经注册
        Boolean existByPhoneNum = userService.isExistByPhoneNum(phoneNum);
        if (!existByPhoneNum) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.PHONE_NOT_EXIST);
        }
        String code = GetMessageCodeUtil.getCode(phoneNum);
        if (Objects.equals(code ,"000000")) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.CODE_FILED);
        }
        return ResponseHelper.createInstance(code ,MessageCodeEnum.CREATE_SUCCESS);
    }

    /**
     * 查询用户
     * @param phoneNum
     * @return
     */
    public JsonEntity<User> getUserHashAndOpenidByPhoneNum(String phoneNum) {
        User user = userService.getUserByPhoneNumContainOpenidAndHash(phoneNum);
        return ResponseHelper.createInstance(user ,MessageCodeEnum.QUERY_SUCCESS);
    }
}
