package com.trevor.message.server;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 06/27/19 18:05
 */
@Component
public class BaseServer {

    protected static StringRedisTemplate redisTemplate;

    @Resource
    public void setStringRedisTemplate(StringRedisTemplate redisTemplate){
        BaseServer.redisTemplate = redisTemplate;
    }

}
