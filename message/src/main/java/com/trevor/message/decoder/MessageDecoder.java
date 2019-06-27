package com.trevor.message.decoder;

import com.alibaba.fastjson.JSON;
import com.trevor.websocket.bo.ReceiveMessage;

import javax.websocket.EndpointConfig;

/**
 * @author trevor
 * @date 03/30/19 16:01
 */
public class MessageDecoder implements javax.websocket.Decoder.Text<ReceiveMessage> {

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy(){

    }

    @Override
    public ReceiveMessage decode(String str) {
        String a = str;
        return JSON.parseObject(str ,ReceiveMessage.class);
    }

    @Override
    public boolean willDecode(String s){
        return true;
    }
}
