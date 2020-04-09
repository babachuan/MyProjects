package com.qhc.dubbo.controller;

import com.qhc.dubbo.demo.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Reference(version = "1.0.0")
    DemoService demoService;

    @RequestMapping("say")
    public String demo() {
        return demoService.sayHello("tom");
    }
}
