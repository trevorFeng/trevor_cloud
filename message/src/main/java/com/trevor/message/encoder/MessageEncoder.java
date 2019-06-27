package com.trevor.message.encoder;

import com.alibaba.fastjson.JSON;
import com.trevor.message.bo.ReturnMessage;

import javax.websocket.EndpointConfig;

/**
 * @author trevor
 * @date 03/30/19 16:01
 */
public class MessageEncoder implements javax.websocket.Encoder.Text<ReturnMessage> {


    @Override
    public void init(EndpointConfig config){

    }

    @Override
    public void destroy(){

    }

    @Override
    public String encode(ReturnMessage returnMessage){
        return JSON.toJSONString(returnMessage);
    }

}
