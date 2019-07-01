package com.trevor.play.controller;

import com.trevor.commom.bo.JsonEntity;
import com.trevor.commom.bo.ResponseHelper;
import com.trevor.commom.enums.MessageCodeEnum;
import com.trevor.play.service.NiuniuPlayService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Controller
public class NiuNiuPlayController {

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private NiuniuPlayService niuniuPlayService;

    public JsonEntity<Object> play(String roomId){
        executor.execute(() -> {
            niuniuPlayService.play(roomId);
        });
        return ResponseHelper.createInstance(null , MessageCodeEnum.HANDLER_SUCCESS);

    }
}
