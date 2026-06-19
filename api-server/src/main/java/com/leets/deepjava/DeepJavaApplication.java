package com.leets.deepjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DeepJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeepJavaApplication.class, args);
    }

}
