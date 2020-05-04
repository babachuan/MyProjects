package com.qhc.consumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RefreshScope
public class ConsumerController{

    @Value("${name}")
    String name="jack";

    @Value("${age}")
    String age;

    @GetMapping("/post")
    public String postPerson(){
       return name+"今年"+age+"岁了";
    }
}
