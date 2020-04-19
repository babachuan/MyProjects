[toc]



# 1.基本的声明式调用

feign主要是构建微服务消费端。在这里构建一个简单的feign调用过程

**服务方**

服务方这里只提供一个简单的controller，方便演示，**配置文件**如下：

```
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka

server.port=81

eureka.instance.prefer-ip-address=true

spring.application.name=user-provider
```

注意这里注册的服务的名字是`user-provider`，后面消费方使用的时候用到了这个名词。提供方的**代码**如下

```
@RestController
@RequestMapping("/User")
public class UserController {

    @GetMapping("/alive")
    public String alive(){
        return "hello";
    }
}
```

这样搭建完springboot项目后可以使用`http://localhost:81/User/alive`进行访问。

**消费方**

**feign**一般是用在消费方，下面就是一个最简单的使用feign的例子，**配置文件**如下：

```
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
eureka.instance.prefer-ip-address=true
server.port=82
spring.application.name=user-consumer
```

配置文件只从eureka获取服务名，下面代码中进行注册，使用feign的话要将服务封装成接口，**接口代码**如下：

```
//@FeignClient(name="user-consumer",url="http://192.168.1.10:81/User")
@FeignClient("user-provider")
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();
}
```

在接口里使用了`@FeignClient`注解，告诉feign要进行拦截，这里`@FeignClient("user-provider")`的**user-provider**就是服务端注册的服务名。这样封装以后就可以把这个类注入到**controller**,这里也是方便演示，代码如下：

```
@RestController
public class ConsumerController{

    @Autowired
    UserInterface userInterface;

    @GetMapping("/myalive")
    public String alive(){
        return userInterface.alive();
    }
}
```

可以看到，通过`@Autowired`将接口注入了进来，这是因为在消费端springboot启动类里添加了`@EnableFeignClients`注解，如下：

```
@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```

**总结**:通过将服务方保留的接口，通过feign封装成可被调用的方法，可以简化后面的开发，如果接口进行统一抽取的话，只需要维护接口部分即可。

## 1.2 使用map接收参数

在我们的消费方和服务方的代码如下：

**消费方**

```
    @GetMapping("/postPersonByMap")
    public List<Person> postPersonByMap(@RequestParam Map<String,String> map){
        return userInterface.postPersonByMap(map);
    }
```

**提供方**

```
    @PostMapping("/postPersonByMap")
    public List<Person> postPersonByMap(@RequestParam Map<String,String> map){
        ArrayList<Person> persons = new ArrayList<Person>();
        System.out.println(map.entrySet());
        persons.add(new Person(map.get("name"),map.get("age")));
        return persons;
    }
```

这样，在浏览器端输入url`http://localhost:82/postPersonByMap?name=zhangsan&age=20`的形式，便可以使用map进行接收。返回结果：

```
[{"name":"zhangsan","age":"20"}]
```



# 2.超时

在feign中调用请求的时候，会有超时设置。比如访问提供者的服务的时候耗时过长，那么会再重试一次。（Feign默认支持Ribbon；Ribbon的重试机制和Feign的重试机制有冲突，所以源码中默认关闭Feign的重试机制,使用Ribbon的重试机制），复现的过程如下：

**服务提供方代码**

我们改造一下原来的逻辑，让服务提供方在提供服务的时候自己先休眠60s

```
 private AtomicInteger count = new AtomicInteger();

    @GetMapping("/alive")
    public String alive(){
        //打印调用次数
        int i = count.getAndIncrement();
        System.out.println("这是第："+i+"次调用");

        //模拟超时，自己先休眠60s
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
```

**消费方代码**

消费方的逻辑不变，还是使用feign进行调用，也没有在配置文件进行任何超时配置：

```
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
eureka.instance.prefer-ip-address=true
server.port=82
spring.application.name=user-consumer

#上面的配置文件中没有任何超时配置
```

代码如下：

```
    @GetMapping("/myalive")
    public String alive(){
        return userInterface.alive();
    }
```

**测试**

在浏览器中输入`http://localhost:82/myalive`即可得到超时报错

```
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Sun Apr 19 15:46:07 CST 2020
There was an unexpected error (type=Internal Server Error, status=500).
Read timed out executing GET http://user-provider/User/alive
```

那么在服务提供方可以看到如下日志：

```
这是第：0次调用
这是第：1次调用
```

说明**服务默认情况下被调用了2次，如果有负载均衡的话，会分别调用不同机器的服务**

## 2.1 超时配置

由于feign与ribbon的超时是冲突的，所以使用ribbon的配置

```
#连接超时时间(ms)
ribbon.ConnectTimeout=1000
#业务逻辑超时时间(ms)
ribbon.ReadTimeout=6000
```

当我们配置超时时间是70s时（这个是在**消费方**进行配置），就不会有报错问题

# 3.重试机制

# 3.1全局配置

一个消费方，两个服务提供方，配置的全局策略如下：

消费配置文件如下（这是对**全局**的设置）：

```
#重试机制
#同一台实例最大重试次数，不包括首次调用
ribbon.MaxAutoRetries=2
#重试负载均衡其他的实例最大重试次数,不包括首次调用
ribbon.MaxAutoRetriesNextServer=1
#是否所有操作都重试
ribbon.OkToRetryOnAllOperations=false
```

在两个服务方全都启动的情况下

```
Application	AMIs	Availability Zones	Status
USER-CONSUMER	n/a (1)	(1)	UP (1) - DESKTOP-IDDQ7KK:user-consumer:82
USER-PROVIDER	n/a (2)	(2)	UP (2) - DESKTOP-IDDQ7KK:user-provider:83 , DESKTOP-IDDQ7KK:user-provider:81
```

那么如果在一台机器上尝试了3次后没有响应，会去尝试另外一台服务，直到响应成功或再次失败并返回给浏览器。



# 3.2针对服务进行配置

```
### 针对单个服务的 Ribbon 配置
demo-goods:
  ribbon:
    # 基于配置文件形式的 针对单个服务的 Ribbon 负载均衡策略
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
    # http建立socket超时时间,毫秒
    ConnectTimeout: 2000
    # http读取响应socket超时时间
    ReadTimeout: 10000
    # 同一台实例最大重试次数,不包括首次调用
    MaxAutoRetries: 0
    # 重试负载均衡其他的实例最大重试次数,不包括首次server
    MaxAutoRetriesNextServer: 2
    # 是否所有操作都重试，POST请求注意多次提交错误。
    # 默认false，设定为false的话，只有get请求会重试
    OkToRetryOnAllOperations: true
```

