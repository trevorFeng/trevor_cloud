package com.trevor.message.core;

import com.trevor.message.core.actuator.Actuator;
import com.trevor.message.core.listener.AbstractTaskListener;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class CoreConfig {

    @Resource
    public void setRedisService(Actuator actuator){
        AbstractTaskListener.actuator = actuator;
    }



}
