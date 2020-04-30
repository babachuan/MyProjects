package com.qhc.mywechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.qhc.mywechat.listener")
public class MywechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MywechatApplication.class, args);
    }

}
