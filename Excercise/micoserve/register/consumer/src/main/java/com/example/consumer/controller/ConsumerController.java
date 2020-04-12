package com.example.consumer.controller;

import com.netflix.discovery.converters.Auto;
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
import java.util.concurrent.atomic.AtomicInteger;


@RestController
public class ConsumerController {
    @Autowired
    private DiscoveryClient discoveryClient;

    //负载均衡的抽象
    @Autowired
    LoadBalancerClient loadBalancerClient;

    //测试自定义负载策略时使用
    AtomicInteger count =  new AtomicInteger();


    //这里也可以使用注解的形式，因为RestTemplate是无状态的，在配置类里制作一个单例即可
        @Autowired
        RestTemplate restTemplate;

//    private RestTemplate restTemplate = new RestTemplate();

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

    //在代码中自定义负载策略
    @RequestMapping("/loadbalance2")
    public String loadbalance2(){
        //自定义的负载算法是每个服务分别被调用2次
        ServiceInstance instance;
        List<ServiceInstance> provider1 = discoveryClient.getInstances("PROVIDER");

        int i = count.incrementAndGet();
        System.out.println("i的值是："+i);
        //一支服务中有2个实例，所以策略写死，仅仅是测试使用
        if(i%4 == 0){
            instance = provider1.get(0);
        }else if(i%4 == 1){
            instance = provider1.get(0);
        }else {
            instance = provider1.get(1);
        }
        URI uri = instance.getUri();
        System.out.println("这次拿到的url是： "+uri.toString());
        String forObject = restTemplate.getForObject(uri.toString() + "/getHi", String.class);
        return forObject;
    }

    //自动处理URL
    @RequestMapping("/loadbalance3")
    public String loadbalance3(){
        //自动处理URL，后面的前面的provider是自动处理的,这时候RestTemplate要是自动注入的
        String url = "http://provider/getHi";
        System.out.println("这次拿到的url是： "+url);
        String forObject = restTemplate.getForObject(url, String.class);
        return forObject;
    }

}
