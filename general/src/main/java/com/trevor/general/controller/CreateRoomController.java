package com.trevor.general.controller;

import com.trevor.bo.JsonEntity;
import com.trevor.domain.User;
import com.trevor.service.createRoom.CreateRoomService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.ThreadLocalUtil;
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
     * @param niuniuRoomParameter 房间参数
     * @return 房间的唯一id
     */
    @ApiOperation("创建一个房间")
    @RequestMapping(value = "/api/room/create/niuniu", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Long> createRoom(@RequestBody @Validated NiuniuRoomParameter niuniuRoomParameter){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Long> jsonEntity = createRoomService.createRoom(niuniuRoomParameter ,user);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }
}
