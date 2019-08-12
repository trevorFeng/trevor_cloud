package com.trevor.general.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 05/14/19 17:24
 */
@Component
@Slf4j
public class CheckRoomOverDateTask {

    @Resource
    private TaskService taskService;

    /**
     * 超过12小时未使用的房间会被自动关闭
     */
    @Scheduled(initialDelay = 1000 * 60 * 60 ,fixedRate = 5000 * 60 * 30)
    public void checkRoom(){
        log.info("检查房间开始");
        //房间半小时内未使用会被关闭
        try {
            taskService.checkRoomRecord();
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
