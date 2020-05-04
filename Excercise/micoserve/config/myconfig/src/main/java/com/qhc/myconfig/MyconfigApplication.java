package com.qhc.myconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MyconfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyconfigApplication.class, args);
    }

}
