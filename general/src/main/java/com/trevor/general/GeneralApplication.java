package com.trevor.general;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "com.trevor")
@EnableEurekaClient
public class GeneralApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeneralApplication.class, args);
    }

}
