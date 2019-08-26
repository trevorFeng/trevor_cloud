package com.trevor.message.core.listener;

import com.trevor.common.util.NumberUtil;
import com.trevor.message.core.ListenerKey;
import com.trevor.message.core.actuator.Actuator;

public abstract class AbstractTaskListener implements TaskListener{

    public static Actuator actuator;

    /**
     * 得到roomId
     * @return
     */
    protected String getRoomIdByKey(){
        String str[] = getKey().split(ListenerKey.SPLIT);
        return str[str.length-2];
    }

    /**
     * 得到time
     * @return
     */
    protected Integer getTimeByKey(){
        String str[] = getKey().split(ListenerKey.SPLIT);
        return NumberUtil.stringFormatInteger(str[str.length-1]);
    }

}
