package com.nies.microservice.nies_retry;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableMPP
@SpringBootApplication
@MapperScan("com.nies.microservice.nies_retry.mapper")
@EnableRetry
public class NiesRetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(NiesRetryApplication.class, args);
    }

}
