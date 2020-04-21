# 1.Hystrix与Feign简单整合

**引入依赖**

Hystrix和Feign都是消费者端的，引入Hystrix依赖

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
```

**启动类添加注解**

```
消费者端启动类添加如下注解
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class ConsumerApplication {...}
```

**在接口上引入回调方法**

```
@FeignClient(value = "user-provider",fallback = UserProviderBack.class)
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();
    }
```

通过`fallback = UserProviderBack.class`引入回调函数

**编写回调类**

```
@Component
public class UserProviderBack implements UserInterface {
    @Override
    public String alive(){
        return "降级处理了";
    }
    }
```

回调类除了通过`@Component`交给spring托管之外，还要实现`UserInterface`接口，然后再针对接口中的方法分别实现回调。

通过以上配置即可实现降级处理。

# 2.通过fallbackFactory实现更细粒度的异常检查

通过上面的实现的降级处理，粒度比较粗，可以使用fallbackFactory实现更细粒度的控制。

与上面的用法基本一致，不同点如下

在引入回退方法的时候需要使用`fallbackFactory`

```

@FeignClient(value = "user-provider",fallbackFactory = UserProviderBackFacktory.class)  //细粒度的降级
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();
    }
```

`UserProviderBackFacktory`是实现了`FallbackFactory`接口

```

@Component //交给spring托管
public class UserProviderBackFacktory implements FallbackFactory<UserInterface> { //泛型是要监控的接口
    @Override
    public UserInterface create(Throwable throwable) {
        return new UserInterface() {
            @Override
            public String alive() {
                return "通过FallBackFactory降级";
            }

            @Override
            public Person postPerson(String name, String age) {
                return null;
            }

            @Override
            public List<Person> postPersonByMap(Map<String, String> map) {
                return null;
            }
        };
    }
}
```

这个类除了交给spring托管之外，使用的泛型类是要拦截的接口`UserInterface`,这样就可以进行更细粒度的控制了。如下：

```
            @Override
            public String alive() {
                System.out.println(throwable);
                if(throwable instanceof FeignException.InternalServerError){
                    System.out.println("InternalServerError");
                    System.out.println(throwable.getLocalizedMessage());
                    return "远程服务报错";
                }
                return "通过FallBackFactory降级";
            }
```

服务端代码如下：

```
    @GetMapping("/alive")
    public String alive(){
        int i = 1/0;
        return "hello";
    }
```

这样就针对更细粒度的错误进行精准的匹配，做到控制更精准。通过服务端打印出来的错误

```
feign.FeignException$InternalServerError: [500] during [GET] to [http://user-provider/User/alive] [UserInterface#alive()]: [{"timestamp":"2020-04-20T06:04:04.539+0000","status":500,"error":"Internal Server Error","message":"/ by zero","path":"/User/alive"}]
InternalServerError
```

# 3.Hystrix整合template

这个也是细粒度的控制。

依赖同上面，同时启动类需要添加注解

```
@EnableCircuitBreaker
```

**配置类**

```
@Configuration
public class MainConfiguration {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
```

**Service类**

```

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
```

这里使用`@HystrixCommand(fallbackMethod = "aliveFallback")`对方法进行降级，同个类下面的`aliveFallback`就是降级方法。

**Controller类**

```
@RestController
public class ConsumerRestControler  {
    @Autowired
    RestService restService;

    @RequestMapping("/myalive2")
    public String myalive2(){
        String alive = restService.alive();
        return alive;
    }
}
```

这样在服务端报错后服务会做降级处理，并打印`被HystrixCommand降级了`



# 4.Hystrix开启Dashboard

**引入依赖**

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>
                spring-cloud-starter-netflix-hystrix-dashboard
            </artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

这里需要说明的是要引入健康检查，在开启监测点的时候需要开启hystrix.stream，或者全部开启

**配置文件开启监测点**

```
management.endpoints.web.exposure.include=*
```

**启动类开启dashboard**

在启动类添加如下注解即可

```
@EnableHystrixDashboard
```

上述配置完成后使用url`http://172.16.28.152:82/actuator`进行测试访问下，其中返回的一个监测点如下

```
"hystrix.stream":{"href":"http://172.16.28.152:82/actuator/hystrix.stream","templated":false}
```

然后使用hystrix的文本链接进行访问**http://172.16.28.152:82/actuator/hystrix.stream**，可以获取对应的监测信息。

**访问图形页面**

使用url`http://localhost:82/hystrix`进行访问，得到一个图形界面，在输入url的位置输入上面的访问链接`http://172.16.28.152:82/actuator/hystrix.stream`可以看到图形化的监控信息



# 5.Hystrix限流测试

hystrix一个典型的应用就是进行限流，这里列举一个使用线程池限流的例子。

Hystrix把一个分布式系统的某一个服务打造成一个高可用的服务最重要的手段之一就是对**资源隔离**，即通过限流来限制对某一服务的访问量，比如说对mysql的访问，为了避免过大的流量直接请求mysql服务，hystrix通过线程池或者信号量技术进行限流访问。

Hystrix的两种隔离技术：**线程池和信号量**

应用场景：

- 使用线程池和信号量，通常来说线程池资源隔离技术一般用于对依赖服务的网络请求访问，需要解决timeout问题。
- 信号量则适合对内部的一些比较复杂的业务逻辑访问，不涉及任何的网络请求，当并发量超过计数器指定值时，直接拒绝

优缺点：

**线程池隔离的最大优点**在于：任何一个依赖服务都可以被隔离在自己的线程池内，即使自己的线程池资源填满了，也不会影响任何其他的服务调用。**最大缺点**在于：增加了cpu的开销，除了tomcat本身的调用线程之外，还有hystrix自己管理的线程池。每个command的执行都依托一个独立的线程，会进行排队，调度，还有上下文切换。

**实战**

在消费端的`limitbythread`目录下，分别创建对应的类和测试类

`ThreadCommant`

```

public class ThreadCommant  extends HystrixCommand<String> {
    private String flag;

    //构造方法，对其进行分许
    public ThreadCommant(String flag){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ThreadCommant"))
        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withCoreSize(5) //设置线程池大小
                .withMaxQueueSize(2)) //设置最大等待队列大小
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionTimeoutInMilliseconds(3000)  //设置timeout时长，默认1000， 一个command运行超出这个时间，被认为timeout
        .withFallbackIsolationSemaphoreMaxConcurrentRequests(100))////防止并发量过大报错：HystrixRuntimeException: Command fallback execution rejected
        );
        this.flag = flag;
    }
    @Override
    protected String run() throws Exception {  //run里是真正执行的服务
        //这里执行的时间暂定设置为1s
        Thread.sleep(1000);
//        int i=1/0;
        return flag;
    }

    //默认会执行的降级方法
    @Override
    protected String getFallback() {
        return "被getFallback降级处理了";
    }
}

```



`测试类ThreadCommandTest`

```

public class ThreadCommandTest {
    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            new ThreadTest(i).start();
        }
    }

    //这里使用多线程，模拟并发操作
    private static class ThreadTest extends Thread{
        private int index;
        //构造方法
        public ThreadTest(int index){
            this.index = index;
        }

        @Override
        public void run() {
            //每启动一个线程，都调用一次服务
            ThreadCommant threadCommant = new ThreadCommant("success");
            System.out.println("第 "+(index + 1) + " 次请求"+ threadCommant.execute());
        }
    }
}
```

这里使用线程模拟并发操作，打印的结果如下：

```
第 6 次请求被getFallback降级处理了
第 4 次请求被getFallback降级处理了
第 8 次请求被getFallback降级处理了
第 3 次请求success
第 9 次请求success
第 1 次请求success
第 7 次请求success
第 5 次请求success
第 10 次请求success
第 2 次请求success
```

**分析总结**

我们在上面设置线程池为5，会最大等待两个，也就是7个，所以有7个会成功，3个会直接做降级处理。

**异常情况**

比如上面设置的线程池的大小是5，那么如果突然有100个并发过来了(上面没有添加`withFallbackIsolationSemaphoreMaxConcurrentRequests(100)`配置)，我们看下报错情况

```
com.netflix.hystrix.exception.HystrixRuntimeException: ThreadCommant fallback execution rejected.
	at com.netflix.hystrix.AbstractCommand.handleFallbackRejectionByEmittingError(AbstractCommand.java:1043)
	at com.netflix.hystrix.AbstractCommand.getFallbackOrThrowException(AbstractCommand.java:875)
	at com.netflix.hystrix.AbstractCommand.handleThreadPoolRejectionViaFallback(AbstractCommand.java:993)
```

如果在执行服务的过程中抛出的异常是以下，就会执行失败，并不会进入fallback降级方法：

- StackOverflowError
- VirtualMachineError
- ThreadDeath
- LinkageError

Hystrix不会进入fallback方法，而会直接抛出HystrixRuntimeException，因为这几个Error被认为是**不可恢复错误**

执行错误了，本应该去执行fallback方法，可是却被reject了，为什么呢？

这种情况下，一般来说是**已经熔断了**，所有请求都进入fallback导致的，因为fallback默认是有个并发最大处理的限制，fallback.isolation.semaphore.maxConcurrentRequests，默认是10，这个方法及时很简单，处理很快，可是QPS如果很高，还是很容易达到10这个阈值，导致后面的被拒绝。
**解决方法也很简单**：

- fallback尽可能的简单，不要有耗时操作，如果用一个http接口来作为另一个http接口的降级处理，那你必须考虑这个http是不是也会失败；
- 可以适当增大**fallback.isolation.semaphore.maxConcurrentRequests**(就是上面添加的配置)

# 6.限流集成到web

首先创建service

**LimitByThreadService**

```

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
```

service里面还是使用restTemplate进行实现的。

**LimitByThreadController**

```

@RestController
public class LimitByThreadController {
    @Autowired
    LimitByThreadService limitByThreadService;

    @GetMapping("/limitByThread")
    public String limitByThread() throws Exception{
        return limitByThreadService.limitByThread();
    }
}
```

经过以上后可以直接使用url`http://172.16.28.152:82//limitByThread`进行访问并得到最后的结果

**测试**

测试时使用jemeter进行测试，用10个用户并发登录,结果有**5**个降级了，**5**个成功了

> [参考博客](https://blog.csdn.net/qq_17522211/article/details/84559987)

