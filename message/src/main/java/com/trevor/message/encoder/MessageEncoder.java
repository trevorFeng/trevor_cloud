package com.trevor.message.encoder;

import com.trevor.common.bo.SocketResult;
import com.trevor.common.util.JsonUtil;

import javax.websocket.EndpointConfig;

/**
 * @author trevor
 * @date 03/30/19 16:01
 */
public class MessageEncoder implements javax.websocket.Encoder.Text<SocketResult> {


    @Override
    public void init(EndpointConfig config){

    }

    @Override
    public void destroy(){

    }

    @Override
    public String encode(SocketResult s){
        return JsonUtil.toJsonString(s);
    }

}
