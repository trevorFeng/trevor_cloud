package com.trevor.general.interceptor;


import com.trevor.commom.bo.WebKeys;
import com.trevor.commom.domain.mysql.User;
import com.trevor.commom.service.UserService;
import com.trevor.commom.util.ThreadLocalUtil;
import com.trevor.commom.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Auther: trevor
 * @Date: 2019\3\28 0028 01:22
 * @Description:
 */
@Slf4j
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserService userService;

    private final static String redirectUrl = "www.knave.top/wechat/";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String token = request.getHeader(WebKeys.TOKEN);
        Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
        String openid = (String) claims.get("openid");
        User user = userService.findUserByOpenid(openid);
        ThreadLocalUtil.getInstance().bind(user);
        return Boolean.TRUE;
    }


}
