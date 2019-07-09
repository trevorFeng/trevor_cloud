package com.trevor.general.controller;

import com.trevor.common.bo.Authentication;
import com.trevor.common.bo.JsonEntity;
import com.trevor.common.bo.ProposalContent;
import com.trevor.common.domain.mysql.User;
import com.trevor.common.service.UserService;
import com.trevor.common.util.ThreadLocalUtil;
import com.trevor.general.service.ProposalsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 16:39
 **/
@Api(value = "用户异常举报及实名认证" ,description = "用户异常举报及实名认证")
@RestController
@Validated
public class ProposalsController {


    @Resource
    private ProposalsService proposalsService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @ApiOperation("上传文件")
    @PostMapping(value = "/api/proposals/file")
    public JsonEntity<String> uploadProposalsFile(@RequestParam("file") MultipartFile file){
        return proposalsService.loadMaterial(file);
    }


    @ApiOperation("提交异常举报")
    @RequestMapping(value = "/api/proposals/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> submitProposals(@RequestBody @Validated ProposalContent proposalContent){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> objectJsonEntity = proposalsService.submitProposals(proposalContent, user.getId());
        ThreadLocalUtil.getInstance().remove();
        return objectJsonEntity;
    }

    /**
     * 实名认证
     * @param authentication
     * @return
     */
    @ApiOperation("实名认证")
    @RequestMapping(value = "/api/proposals/auth", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> realNameAuth(@RequestBody Authentication authentication){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        JsonEntity<Object> jsonEntity = proposalsService.realNameAuth(authentication ,user.getId());
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

}
