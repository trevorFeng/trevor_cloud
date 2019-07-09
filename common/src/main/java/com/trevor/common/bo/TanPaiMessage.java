package com.trevor.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class TanPaiMessage {
    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 牌
     */
    private List<String> pokes;

    /**
     * 牌型
     */
    private Integer paiXing;
}
