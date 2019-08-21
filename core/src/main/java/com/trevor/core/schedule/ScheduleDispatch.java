package com.trevor.core.schedule;

import com.trevor.core.listener.TaskListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 总调度
 */
@Service
public class ScheduleDispatch {


    private ConcurrentHashMap<String ,TaskListener> listeners = new ConcurrentHashMap<>(2<<7);

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
        listeners.remove(listener.getKey());
    }

    /**
     * 每一秒执行一次
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void loopRedPacketBySec(){
        Iterator<TaskListener> iterator = listeners.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().onCountDown();
        }

    }
}
