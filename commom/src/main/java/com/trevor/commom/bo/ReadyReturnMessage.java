package com.trevor.commom.bo;

import com.trevor.websocket.bo.SocketUser;
import lombok.Data;

import java.util.List;

/**
 * @author trevor
 * @date 05/20/19 11:07
 */
@Data
public class ReadyReturnMessage {

    /**
     * 房间状态，0-在打牌中(不可以参与本局打牌)，1-在等人打牌（可以参与本局打牌）
     */
    private Integer roomStatus;

    /**
     * 当前局数
     */
    private Integer runingNum;

    /**
     * 总局数
     */
    private Integer totalNum;

    /**
     * 玩家列表
     */
    private List<SocketUser> socketUserList;
}
