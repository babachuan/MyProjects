package com.example.consumer.config;

import com.example.consumer.my.MyIRules;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RibbonClient(name = "provider",configuration =com.example.consumer.my.MyIRules.class )
public class MainConfiguration {
    //配置类，主要是为了加载restTemplate和ribbon的负载策略


    /**
     *

    //让spring托管RestTemplate
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
     */

    /**
    //配置负载策略
    @Bean
    public IRule ribbonRule() {
        return new RandomRule(); //随机
        //return new RoundRobinRule();  //轮训
        // return new WeightedResponseTimeRule();  //加权权重
        //return new RetryRule();          //带有重试机制的轮训
        //return new TestRule();           //自定义规则
    }
    */

//    //使用自定义的负载策略
//    @Bean
//    public IRule myRule(){
//        return new MyIRules();
//    }

    //让ribbon自动处理URL
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
