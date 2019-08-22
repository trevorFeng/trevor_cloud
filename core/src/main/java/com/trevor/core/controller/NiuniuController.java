package com.trevor.core.controller;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.core.service.NiuniuService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class NiuniuController {

    @Resource
    private NiuniuService niuniuService;

    /**
     * 两个人开始游戏，注册发4张牌事件
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/api/niuniu/playTwo/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playTwo(@PathVariable String roomId){

        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);
    }

    /**
     * 超过2个人开始游戏，注册准备倒计时事件
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/api/niuniu/playOverTwo/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playOverTwo(@PathVariable String roomId){

        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);
    }

    /**
     * 若在准备倒计时中，所有人都已经准备好，删除准备倒计时事件并且注册发4张牌事件
     * @param roomId
     * @return
     */
    @RequestMapping(value = "/api/niuniu/allReady/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> allReady(@PathVariable String roomId){

        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);
    }
}
