package com.trevor.common.bo;

import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\16 0016 22:41
 * @Description:
 */
@Data
public class FriendInfo {

    private Long userId;

    private String appName;

    private String pictureUrl;

    /**
     * 1通过 ，0为未通过
     */
    private Integer allowFlag;
}
