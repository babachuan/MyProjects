package com.qhc.dubbo.demo;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service(version = "1.0.0", timeout = 10000, interfaceClass = DemoService.class)
@Component
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        System.out.println("hello:"+name);
        return "hello:"+name;
    }
}
