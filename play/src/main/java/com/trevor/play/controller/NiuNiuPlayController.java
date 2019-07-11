package com.trevor.play.controller;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.play.service.NiuniuPlayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@RestController
public class NiuNiuPlayController {

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private NiuniuPlayService niuniuPlayService;


    @RequestMapping(value = "/api/niuniu/two/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playEqualsTwo(@PathVariable String roomId){
        executor.execute(() -> {
            niuniuPlayService.playEqualTwo(roomId);
        });
        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);

    }

    @RequestMapping(value = "/api/niuniu/over/two/{roomId}", method = RequestMethod.GET)
    public JsonEntity<Object> playOverTwo(@PathVariable String roomId){
        executor.execute(() -> {
            niuniuPlayService.playOverTwo(roomId);
        });
        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);

    }
}
