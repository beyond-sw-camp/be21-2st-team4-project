package com.ohgiraffers.timedeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TimedealApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimedealApplication.class, args);
    }

}
