package com.trevor.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableZuulProxy
@SpringBootApplication
@ComponentScan(basePackages = {"com.trevor.common","com.trevor.gateway"})
@EnableEurekaClient
@MapperScan("com.trevor.common.dao.mysql")
@EnableMongoRepositories(basePackages = {"com.trevor.common.dao.mongo"})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
