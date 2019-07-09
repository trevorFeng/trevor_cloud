package com.trevor.common.bo;

import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\16 0016 23:20
 * @Description:
 */
@Data
public class LoginUser {

    private Long id;

    /**
     * 用户昵称
     */
    private String appName;

    /**
     * 用户头像地址
     */
    private String appPictureUrl;

    /**
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;

    /**
     * 房卡数量
     */
    private Integer cardNum;
}
