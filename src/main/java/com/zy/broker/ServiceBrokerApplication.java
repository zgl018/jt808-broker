package com.zy.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;



@ComponentScan(basePackages = {"com.zy"})
@SpringBootApplication
public class ServiceBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBrokerApplication.class, args);
    }
}
