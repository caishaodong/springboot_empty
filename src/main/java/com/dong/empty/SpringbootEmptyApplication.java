package com.dong.empty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.dong.empty.mapper")
public class SpringbootEmptyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootEmptyApplication.class, args);
    }

}
