package com.trevor.general.controller;

import com.trevor.common.bo.FriendInfo;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ResponseHelper;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.enums.MessageCodeEnum;
import com.trevor.common.util.ThreadLocalUtil;
import com.trevor.general.service.FriendManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 一句话描述该类作用:【好友管理】
 *
 * @author: trevor
 * @create: 2019-03-03 23:05
 **/
@Api(value = "好友管理" ,description = "好友管理相关接口")
@RestController
public class FriendManageController {

    @Resource
    private FriendManagerService friendManagerService;

    /**
     * 查询好友（申请通过和未通过的）
     * @return
     */
    @ApiOperation(value = "查询好友（申请通过和未通过的）")
    @RequestMapping(value = "/api/friend/manager/query", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<FriendInfo>> queryFriends(){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        List<FriendInfo> friendInfos = friendManagerService.queryFriends(user);
        ThreadLocalUtil.getInstance().remove();
        return ResponseHelper.createInstance(friendInfos , MessageCodeEnum.QUERY_SUCCESS);
    }

    /**
     * 申请成为房主的好友
     * @return
     */
    @ApiOperation(value = "申请成为房主的好友")
    @RequestMapping(value = "/api/friend/manager/query/{roomId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> applyFriend(@PathVariable("roomId") Long roomId){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> jsonEntity = friendManagerService.applyFriend(roomId ,user.getId());
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    /**
     * 踢出好友
     * @return
     */
    @ApiOperation(value = "踢出好友")
    @RequestMapping(value = "/api/friend/manager/remove/{removeUserId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> removeFriend(@PathVariable("removeUserId") Long removeUserId){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> jsonEntity = friendManagerService.removeFriend(user.getId(),removeUserId);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    /**
     * 通过好友申请
     * @return
     */
    @ApiOperation(value = "通过好友申请")
    @RequestMapping(value = "/api/friend/manager/pass/{passUserId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> passFriend(@PathVariable("passUserId") Long passUserId){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> jsonEntity = friendManagerService.passFriend(user.getId(),passUserId);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }


}
