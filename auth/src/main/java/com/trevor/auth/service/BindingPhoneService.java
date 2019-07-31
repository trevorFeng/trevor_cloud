package com.trevor.auth.service;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/22/19 13:15
 */
@Service
public class BindingPhoneService{

    @Resource
    private UserService userService;

    /**
     * 绑定手机号
     * @param userId
     * @param phoneNum
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<String> bindingPhone(Long userId, String phoneNum) {
        User user = new User();
        user.setId(userId);
        userService.updatePhoneByUserId(userId ,phoneNum);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.BINDING_SUCCESS);
    }
}
