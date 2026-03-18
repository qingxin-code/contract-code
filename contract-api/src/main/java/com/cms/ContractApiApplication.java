package com.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cms.mapper")
public class ContractApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractApiApplication.class, args);
    }
}
