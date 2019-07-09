package com.trevor.common.util;


import com.trevor.common.domain.mysql.User;

/**
 * @Auther: trevor
 * @Date: 2019\4\5 0005 03:03
 * @Description:
 */
public class ThreadLocalUtil {
    private ThreadLocal<User> userInfoThreadLocal = new ThreadLocal<>();

    private static final ThreadLocalUtil instance = new ThreadLocalUtil();

    private ThreadLocalUtil() {
    }

    public static ThreadLocalUtil getInstance() {
        return instance;
    }

    /**
     * 将用户对象绑定到当前线程中，键为userInfoThreadLocal对象，值为userInfo对象
     * @param user
     */
    public void bind(User user) {
        userInfoThreadLocal.set(user);
    }

    /**
     * 得到绑定的用户对象
     *
     * @return
     */
    public User getUserInfo() {
        User userInfo = userInfoThreadLocal.get();
        remove();
        return userInfo;
    }

    /**
     * 移除绑定的用户对象
     */
    public void remove() {
        userInfoThreadLocal.remove();
    }
}
