package com.qhc.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "aliveFallback")
    public String alive(){
        String url = "http://user-provider/User/alive";
        String result = restTemplate.getForObject(url,String.class);
        return  result;
    }

    //降级方法
    public String aliveFallback(){
        return "被HystrixCommand降级了";
    }
}
