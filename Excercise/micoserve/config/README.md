# 1.配置中心搭建

搭建一个配置中心，从github上读取配置文件。服务端的搭建过程如下。首先你需要一个github仓库，我这里的测试仓库为：`https://github.com/babachuan/config.git`

在仓库里面提交了一个名为`consumer-dev.properties`的文件，内容如下：

```
name=lisi
age=20
```

下面开始搭建配置中心的服务端

**引入依赖**

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

**启动类添加注意**

```
@EnableConfigServer
```

**application.properties配置文件**

```
spring.cloud.config.server.git.uri=https://github.com/babachuan/config.git
spring.cloud.config.label=master

eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
eureka.instance.prefer-ip-address=true
server.port=83
spring.application.name=configServer
```

其中`spring.cloud.config.server.git.uri`是配置仓库的地址，`spring.cloud.config.label`配置使用仓库的 哪个分支。

这样配置完，启动后就可以

**注意仓库名称的设置**

这个是有要求的,匹配规则如下

```
获取配置规则：根据前缀匹配
/{name}-{profiles}.properties
/{name}-{profiles}.yml
/{name}-{profiles}.json
/{label}/{name}-{profiles}.yml

name 服务名称
profile 环境名称，开发、测试、生产：dev qa prd
lable 仓库分支、默认master分支

匹配原则：从前缀开始。
```

配置的服务端启动后可以使用`http://172.16.28.152:83/consumer-dev.properties`这样的url去浏览器访问下，可以获取到对应的数据说明配置没问题

```
age: 20
name: lisi
```

上面的没有添加分支，默认就是主干`http://172.16.28.152:83/master/consumer-dev.properties`

# 2.客户端使用配置

引入依赖

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```

**application.properties**

```
server.port=833
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
spring.application.name=consumer
```

由于上面文件的名字使用的是`consumer-dev.properties`,所以服务的名称定义为`consumer`。

新建一个**bootstrap.properties**的文件

```
spring.cloud.config.uri=http://localhost:83/
spring.cloud.config.profile=dev
spring.cloud.config.label=master
```

这里就要配置上面配置中心的url，指定profile和label.

**ConsumerController**

```
package com.qhc.consumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ConsumerController{

    @Value("${name}")
    String name="jack";

    @Value("${age}")
    String age;

    @GetMapping("/post")
    public String postPerson(){
       return name+"今年"+age+"岁了";
    }
}

```

这样就可以把配置中心的配置拉回来进行使用了

# 3.通过Eureka获取配置（高可用）

上面的客户端的配置是写死的，如果有多个配置中心或端口号改变，那么在客户端修改是很麻烦的，可用通过从Eureka获取注册服务来实现。

需要引入pom

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

配置从Eureka发现配置server端的服务（注意：这里建议都配置到bootstrap.application这个约定文件里，不然会报错）

```
#spring.cloud.config.uri=http://localhost:83/
spring.cloud.config.profile=dev
spring.cloud.config.label=master
server.port=833
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
spring.application.name=consumer
#通过注册中心查找
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.service-id=configServer
```

添加如下配置

```
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.service-id=configServer
```

这样，即使注释掉了上面的uri,一样可以获取服务。



# 4.手动配置热更新

引入健康检查Pom

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

并在配置端开启所有检查点

```
#开启所有检查点
management.endpoints.web.exposure.include=*
```

开启后有如下的一个接口

```
"refresh":{"href":"http://desktop-iddq7kk:833/actuator/refresh","templated":false}
```

热更新的时候只需要使用`POST`方法调用这个接口即可，可以使用postman工具进行post请求的发送`http://172.16.28.152:833/actuator/refresh`。再次访问可以发现已经更新。

这样有个缺点就是如果有多个服务，那就要手动一个一个刷新，会麻烦，后面会有解决方案。

# 5.使用bus批量更新服务配置

上面的服务配置是一个一个手动进行设置，会麻烦而且容易遗漏，可以增加bus功能，进行批量配置。

分别在消费服务端、配置中心服务端添加如下依赖

```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bus-amqp</artifactId>
		</dependency>
```

在配置中心服务端添加如下配置

```
#启动bus
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

management.endpoints.web.exposure.include=*
```

在github上的配置文件添加如下配置

```
#启动bus
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

management.endpoints.web.exposure.include=*
```

如此，启动所有的服务后，使用postman工具只需要访问一次配置中心服务端的一个接口即可批量刷新

```
健康检查开启的服务：
"bus-refresh":{"href":"http://172.16.28.152:83/actuator/bus-refresh","templated":false}

使用Postman用Post请求访问如下配置中心服务端的接口，即可批量刷新
http://172.16.28.152:83/actuator/bus-refresh
```



