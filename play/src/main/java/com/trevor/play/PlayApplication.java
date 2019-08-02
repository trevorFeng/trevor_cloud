package com.trevor.play;

import com.trevor.common.util.ExecutorUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@ComponentScan(basePackages = {"com.trevor.common","com.trevor.play"})
@EnableEurekaClient
@MapperScan("com.trevor.common.dao.mysql")
@EnableMongoRepositories(basePackages = {"com.trevor.common.dao.mongo"})
public class PlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayApplication.class, args);
    }


    @Bean(name = "executor")
    public Executor getExecutor() {
        return ExecutorUtil.getExecutor(200 ,10 ,1000);
    }

}
