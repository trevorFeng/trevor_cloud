package com.trevor.auth.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class SessionUtil {

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest().getSession();
    }

    /**
     * setToken
     */
    public static void setUser(String token) {
        getSession().setAttribute("token" ,token);
    }

    /**
     * setToken
     */
    public static String getToken() {
        return (String) getSession().getAttribute("token");
    }
}
