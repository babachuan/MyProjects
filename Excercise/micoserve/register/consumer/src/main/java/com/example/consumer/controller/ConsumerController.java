package com.example.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;


@RestController
public class ConsumerController {
    @Autowired
    private DiscoveryClient discoveryClient;

    //负载均衡的抽象
    @Autowired
    LoadBalancerClient loadBalancerClient;


    private RestTemplate restTemplate = new RestTemplate();

    //先使用写死的方式测试一下提供方的
    @GetMapping("/testByStaticUrl")
    public String testByStaticUrl(){
        String url = "http://eurk2.com:8080/getHi";
        return restTemplate.getForObject(url,String.class);
    }

    //通过API获取
    @RequestMapping("/getAPIByInstance")
    public List<ServiceInstance> getAPIByInstance(){
        List<ServiceInstance> instances = discoveryClient.getInstances("PROVIDER");
        /**可以得到以下信息
         * org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient$EurekaServiceInstance@1e3fc91b
         * [instance=InstanceInfo [instanceId = qhc-main-102:consumer:90, appName = CONSUMER, hostName = qhc-main-102,
         * status = UP, ipAddr = 192.168.1.10, port = 90, securePort = 443,
         * dataCenterInfo = com.netflix.appinfo.MyDataCenterInfo@392cd6b9]
         */
        //System.out.println(ToStringBuilder.reflectionToString(instances.get(0)));

        //获取url
        URI url = instances.get(0).getUri();
        System.out.println("拿到的uri：  "+url);

        //通过获取到的url进行远程调用
        System.out.println(restTemplate.getForObject(url.toString()+"/getHi",String.class));
        return instances;

    }

    //最简单的负载均衡实现
    @RequestMapping("/loadbalance")
    public String loadbalance(){
        // ribbon 完成客户端的负载均衡，过滤掉down了的节点
        ServiceInstance provider = loadBalancerClient.choose("PROVIDER");
        URI uri = provider.getUri();
        System.out.println("这次拿到的url是： "+uri.toString());
        String forObject = restTemplate.getForObject(uri.toString() + "/getHi", String.class);
        return forObject;
    }

}
