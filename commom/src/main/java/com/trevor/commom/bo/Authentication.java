package com.trevor.commom.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author trevor
 * @date 2019/3/1 11:40
 */
@Data
@ApiModel
public class Authentication {

    @ApiModelProperty("真实名字")
    private String realName;

    @ApiModelProperty("身份证号码")
    private String idCard;
}
