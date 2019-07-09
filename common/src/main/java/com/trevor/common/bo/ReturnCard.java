package com.trevor.common.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 05/15/19 12:09
 */
@Data
public class ReturnCard {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 返回房卡数量
     */
    private Integer returnCardNum;
}
