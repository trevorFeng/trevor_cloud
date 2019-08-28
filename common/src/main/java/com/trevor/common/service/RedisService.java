package com.trevor.common.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 删除键值对
     * @param key
     */
    public void delete(String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除键值对
     * @param keys
     */
    public void deletes(List<String> keys){
        stringRedisTemplate.delete(keys);
    }

    /**
     * 得到value
     * @param key
     * @return
     */
    public String getValue(String key){
        return stringRedisTemplate.boundValueOps(key).get();
    }

    /**
     * set VALUE
     * @param key
     * @param value
     */
    public void setValue(String key ,String value){
        stringRedisTemplate.boundValueOps(key).set(value);
    }

    /**
     * set VALUE
     * @param key
     * @param value
     */
    public void setValueWithExpire(String key , String value , Long l , TimeUnit timeUnit){
        stringRedisTemplate.boundValueOps(key).set(value ,l ,timeUnit);
    }

    /**
     * 得到set全部元素
     * @param key
     * @return
     */
    public Set<String> getSetMembers(String key) {
        BoundSetOperations<String, String> bo = stringRedisTemplate.boundSetOps(key);

        return bo.members();
    }

    /**
     * 是否是set成员
     * @param key
     * @param value
     * @return
     */
    public Boolean jugeSetMember(String key ,String value){
        return stringRedisTemplate.boundSetOps(key).isMember(value);
    }

    /**
     *  添加set元素
     * @param key
     * @param value
     */
    public void setAdd(String key ,String ... value){
        BoundSetOperations<String, String> bo = stringRedisTemplate.boundSetOps(key);
        bo.add(value);
    }

    /**
     * 删除set中元素
     * @param key
     * @param value
     */
    public void setDeleteMember(String key ,String...value){
        BoundSetOperations<String, String> bo = stringRedisTemplate.boundSetOps(key);
        bo.remove(value);
    }

    /**
     * 得到set大小
     * @param key
     * @return
     */
    public Integer getSetSize(String key){
        BoundSetOperations<String, String> bo = stringRedisTemplate.boundSetOps(key);
        return Math.toIntExact(bo.size());
    }

    /**
     * 向list右侧添加元素
     * @param key
     * @param value
     */
    public void listRightPush(String key ,String...value){
        BoundListOperations<String, String> bl = stringRedisTemplate.boundListOps(key);
        bl.rightPushAll(value);
    }

    /**
     * 得到list元素并删除list
     * @param key
     * @return
     */
    public List<String> getListMembersAndDelete(String key){
        BoundListOperations<String ,String> ops = stringRedisTemplate.boundListOps(key);
        List<String> list = ops.range(0 ,-1);
        delete(key);
        return list;
    }

    /**
     * 得到list大小
     * @param key
     * @return
     */
    public Integer getListSize(String key){
        return Math.toIntExact(stringRedisTemplate.boundListOps(key).size());
    }


    /**
     * 得到hash的value
     * @param redisKey
     * @param hashKey
     * @return
     */
    public String getHashValue(String redisKey ,String hashKey){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(redisKey);
        return bh.get(hashKey);
    }

    /**
     * 得到map
     * @param key
     * @return
     */
    public Map<String ,String> getMap(String key){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(key);
        return bh.entries();
    }

    /**
     * 存入map
     * @param key
     * @param map
     */
    public void putAll(String key ,Map<String ,String> map){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(key);
        bh.putAll(map);
    }

    /**
     * 存入map
     * @param redisKey
     * @param hashKey
     * @param hashValue
     */
    public void put(String redisKey ,String hashKey ,String hashValue){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(redisKey);
        bh.put(hashKey ,hashValue);
    }

    /**
     * map的大小
     * @param redisKey
     * @return
     */
    public Integer getMapSize(String redisKey){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(redisKey);
        return Math.toIntExact(bh.size());
    }

    /**
     * 得到map的key的集合
     * @param redisKey
     * @return
     */
    public Set<String> getMapKeys(String redisKey){
        BoundHashOperations<String, String, String> bh = stringRedisTemplate.boundHashOps(redisKey);
        return bh.keys();
    }






}
