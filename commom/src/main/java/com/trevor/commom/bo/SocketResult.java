package com.trevor.commom.bo;

/**
 * @author trevor
 * @date 06/27/19 18:23
 */
public class SocketResult {

    /**
     * 400--表示token错误
     * 404--根据token找不到user
     * 500--表示重复登陆了，需要下线一个客户端
     * 501--不在准备的时间内
     * 1000--新人加入，发给所有人
     * 2002--房间内情况，发给新人
     */
    private Integer head;



    public SocketResult(Integer head){
        this.head = head;
    }

}
