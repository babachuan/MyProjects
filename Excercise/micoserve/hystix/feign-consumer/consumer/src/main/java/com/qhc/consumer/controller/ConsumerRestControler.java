package com.qhc.consumer.controller;

import com.qhc.consumer.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerRestControler  {
    @Autowired
    RestService restService;

    @RequestMapping("/myalive2")
    public String myalive2(){
        String alive = restService.alive();
        return alive;
//        return "hello";
    }
}
