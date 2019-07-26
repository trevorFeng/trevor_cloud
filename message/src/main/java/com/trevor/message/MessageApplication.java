package com.trevor.message;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@ComponentScan(basePackages = {"com.trevor.common","com.trevor.message"})
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.trevor.common.dao.mysql")
@Configuration
@EnableScheduling
@EnableMongoRepositories(basePackages = {"com.trevor.common.dao.mongo"})
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }


    /**
     * 注入线程池
     * @return
     */
    @Bean(name = "executor")
    public Executor orderSolrSumExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("executor-");
        return executor;
    }

}
