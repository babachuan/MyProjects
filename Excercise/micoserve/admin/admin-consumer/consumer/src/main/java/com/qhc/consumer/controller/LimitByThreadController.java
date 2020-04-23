package com.qhc.consumer.controller;

import com.qhc.consumer.service.LimitByThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LimitByThreadController {
    @Autowired
    LimitByThreadService limitByThreadService;

    @GetMapping("/limitByThread")
    public String limitByThread() throws Exception{
        return limitByThreadService.limitByThread();
    }
}
