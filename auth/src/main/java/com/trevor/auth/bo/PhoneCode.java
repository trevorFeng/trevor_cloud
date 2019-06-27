package com.trevor.auth.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-22 23:52
 **/
@Data
@ApiModel
public class PhoneCode {

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @Pattern(regexp = "^[0-9]{11}$" ,message = "手机号格式不正确")
    private String phoneNum;

    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    @Pattern(regexp = "^[0-9]{6}$" ,message = "验证码格式不正确")
    private String code;
}
