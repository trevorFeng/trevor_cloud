package com.trevor.general.controller;

import com.trevor.common.bo.JsonEntity;
import com.trevor.common.domain.mongo.NiuniuRoomParam;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.util.ThreadLocalUtil;
import com.trevor.general.service.CreateRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/8 16:51
 */
@Api(value = "创建房间" ,description = "创建房间接口")
@RestController
@Validated
public class CreateRoomController {

    @Resource
    private CreateRoomService createRoomService;

    /**
     * 创建一个房间
     * @param niuniuRoomParam 房间参数
     * @return 房间的唯一id
     */
    @ApiOperation("创建一个房间")
    @RequestMapping(value = "/api/room/create/niuniu", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Long> createRoom(@RequestBody @Validated NiuniuRoomParam niuniuRoomParam){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Long> jsonEntity = createRoomService.createRoom(niuniuRoomParam ,user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }
}
