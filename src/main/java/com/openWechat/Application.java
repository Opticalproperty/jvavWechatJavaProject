package com.openWechat;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
