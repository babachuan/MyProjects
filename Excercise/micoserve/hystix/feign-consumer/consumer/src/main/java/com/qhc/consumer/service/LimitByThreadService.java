package com.qhc.consumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Service
public class LimitByThreadService {
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand( //对调用进行限流设置
        commandKey = "limitByThread", //缺省为方法名
        threadPoolKey =  "LimitByThreadService", //缺省为类名
        fallbackMethod = "myFallBackMethod", //指定降级方法，在熔断和降级时会走降级方法
        commandProperties = {
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1000") //超时时间
        },
            threadPoolProperties = {
                @HystrixProperty(name = "coreSize",value = "5") //并发量，这里设置为5
            }
    )
    @RequestMapping(value = "/limitmylive",method = RequestMethod.GET)
    public String limitByThread() throws Exception{
        String url = "http://user-provider/User/alive";
        String result = restTemplate.getForObject(url,String.class);
        Thread.sleep(500);  //默认休眠1s
        return result;
    }

    public String myFallBackMethod(){
        return "被hystrix进行限流降级了";
    }
}
