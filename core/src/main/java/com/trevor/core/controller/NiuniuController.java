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

    @RequestMapping(value = "/api/niuniu/playTwo/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playTwo(@PathVariable String roomId){

        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);
    }

    @RequestMapping(value = "/api/niuniu/playOverTwo/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playOverTwo(@PathVariable String roomId){

        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);
    }
}
