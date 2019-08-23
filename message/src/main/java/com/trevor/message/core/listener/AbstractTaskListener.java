package com.trevor.message.core.listener;

import com.trevor.message.core.actuator.Actuator;

public abstract class AbstractTaskListener implements TaskListener{

    public static Actuator actuator;

    /**
     * 得到roomId
     * @return
     */
    protected String getRoomIdByKey(){
        String str[] = getKey().split("_");
        return str[str.length-1];
    }

}
