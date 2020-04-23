package com.qhc.consumer.limitbythread;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class ThreadCommant  extends HystrixCommand<String> {
    private String flag;

    //构造方法，对其进行分许
    public ThreadCommant(String flag){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ThreadCommant"))
        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withCoreSize(5) //设置线程池大小
                .withMaxQueueSize(2)) //设置最大等待队列大小
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionTimeoutInMilliseconds(3000))  //设置timeout时长，默认1000， 一个command运行超出这个时间，被认为timeout
//        .withFallbackIsolationSemaphoreMaxConcurrentRequests(100))//防止并发量过大报错：HystrixRuntimeException: Command fallback execution rejected
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
