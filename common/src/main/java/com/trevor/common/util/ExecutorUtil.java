package com.trevor.common.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

public class ExecutorUtil {

    public static Executor getExecutor(Integer corePoolSize ,Integer maxPoolSize ,Integer queueCacity){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCacity);
        executor.setThreadNamePrefix("executor-");
        return executor;
    }
}
