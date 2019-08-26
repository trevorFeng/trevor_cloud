package com.trevor.message.core.schedule;

import com.trevor.message.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计时器总调度
 */
@Service
public class ScheduleDispatch {


    private ConcurrentHashMap<String , TaskListener> listeners = new ConcurrentHashMap<>(2<<7);

    /**
     * 添加事件
     * @param listener
     */
    public void addListener(TaskListener listener) {
        listeners.put(listener.getKey() ,listener);
    }

    /**
     * 移除事件
     * @param key
     */
    public void removeListener(String key){
        listeners.remove(key);
    }

    /**
     * 移除事件
     * @param listener
     */
    public void removeListener(TaskListener listener){
        removeListener(listener.getKey());
    }

    /**
     * 每一秒执行一次
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void loopRedPacketBySec(){
        //如果移除监听器时，如果变化发生在已经遍历的桶，则迭代过程不会再感知这个变化，直接删除调即可，移除发生在未遍历的segement，则本次迭代会感知到这个元素。
        Iterator<TaskListener> iterator = listeners.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().onCountDown();
        }

    }
}
