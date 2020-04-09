package com.qhc.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DubboServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboServiceApplication.class, args);
    }

}
