# 1.zuul简介

Zuul是Netflix开源的微服务网关，核心是一系列过滤器。这些过滤器可以完成以下功能。

1. 是所有微服务入口，进行分发。
2. 身份认证与安全。识别合法的请求，拦截不合法的请求。
3. 监控。在入口处监控，更全面。
4. 动态路由。动态将请求分发到不同的后端集群。
5. 压力测试。可以逐渐增加对后端服务的流量，进行测试。
6. 负载均衡。也是用ribbon。
7. 限流（望京超市）。比如我每秒只要1000次，10001次就不让访问了。
8. 服务熔断

网关和服务的关系：演员和剧场检票人员的关系。



zuul默认集成了：ribbon和hystrix。

# 2.搭建Zuul网关

新建项目，引入依赖

```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>
```

配置文件

```
eureka.client.service-url.defaultZone=http://euk1.com:7001/eureka/
spring.application.name=zuulserver
server.port=80
```

启动类添加注解

```
@EnableZuulProxy
```

这个注解是个组合注解，已经包含Eureka客户端

平时使用`http://172.16.28.152:82/myalive`直接访问服务，如果通过网关就要使用`http://172.16.28.152/user-consumer/myalive`进行访问

`user-consumer`url中的这个是指注册到Eureka中的服务的名字。

# 3.路由配置

## 3.1.重写微服务访问路径

Zuul提供了一套简单且强大路由配置策略，利用路由配置我们可以完成对微服务和URL更精确的控制。

在配置文件中将`user-consumer`服务的访问路径进行重写，配置如下：

```
zuul.routes.user-consumer=/consumperapi/**
```

这样，原来通过服务名进行访问`http://172.16.28.152/user-consumer/myalive`,现在就可以使用重写后的路径进行访问`http://172.16.28.152/consumperapi/myalive`

> [参考博客](https://blog.csdn.net/qq_27384769/article/details/82991261)

## 3.2 忽略指定微服务

```
#忽略指定的服务
zuul.ignored-services=user-provider,xxx-service #可以使用"*"来忽略所有，多个指定微服务用半角逗号分隔
```

使用“*”可忽略所有微服务，多个指定微服务以半角逗号分隔。这样原来可以通过网关`http://172.16.28.152/user-provider/User/alive`访问的url就不能再访问了。

**忽略所有，只路由指定微服务**

```
zuul:
  ignored-services: *
  routes:
    rest-demo: /rest/**
```

忽略所有，只对rest-demo服务做路由。

## 3.3 指定服务访问别名，同重写服务路径

```
#路由别名，与上面重写微服务的访问路径类似
zuul.routes.route-name.service-id=user-consumer
zuul.routes.route-name.path=/aname/**
```

这个用法和上面重写微服务访问路径相同

> [参考博客](https://blog.csdn.net/qq_27384769/article/details/82991261)

## 3.4 自定义映射路径和主机

配置文件如下

```
#指定path和url,比如有一个外网地址：http://www.geneplus.org.cn/trans/toAboutUs
zuul.routes.route-name.url=http://www.geneplus.org.cn
zuul.routes.route-name.path=/my/**
#这样配置后就要使用http://172.16.28.152/my/trans/toAboutUs进行访问
```

注意，这里讲`http://ZUULHOST:ZUULPORT/my/`映射到`http://www.geneplus.org.cn`，所有直接在后面添加对应的uri即可。

由于这种是自定义的名称，所有设定的负载策略对这个是不生效的，可以使用如下方式

```
zuul.routes.xx.path=/xx/**
zuul.routes.xx.service-id=cuid

cuid.ribbon.listOfServers=localhost:82,localhost:83
ribbon.eureka.enabled=false
```

## 3.5 路由前缀

```
zuul:
  prefix: /api
  strip-prefix: true
  routes:
    rest-demo: /rest/**
```

此时访问Zuul的/api/rest/user/xdlysk会被转发到/rest-demo/user/xdlysk。

如下是实际使用过的

```
zuul.prefix=/api
zuul.strip-prefix=true
zuul.routes.user-consumer=/rest/**
```

访问路径`http://172.16.28.152/api/rest/myalive`,之前使用`http://172.16.28.152/user-consumer/myalive`



> [参考博客](https://blog.csdn.net/qq_27384769/article/details/82991261)

# 4.Zuul的容错与回退

通过实现接口可以实现Zuul的容错与回退功能

```

@Component
public class DefaultFallbackProvider implements FallbackProvider {
    @Override
    public String getRoute() {
        //匹配微服务的名称,*标识匹配所有
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return this.getStatusCode().value();
            }


            @Override
            public String getStatusText() throws IOException {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream( "please refresh and try again".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                //设定headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return headers;

            }
        };
    }
}

```

这样再停掉consumer1后就会返回fallback的内容。

# 5.Sleuth链路追踪+zipkin

在微服务报错排查过程中，链路追踪会极大提高效率，配置和使用相对比较方面。

引入依赖

```
        <!--        链路追踪-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>

        <!-- zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```

加入zipkin的配置

```
#链路追踪
spring.zipkin.base-url=http://localhost:9411/
spring.sleuth.sampler.rate=1
```

上面注意`http://localhost:9411/`是一个独立的界面，从官网下载jar包`https://zipkin.io`

```
jar包下载：curl -sSL https://zipkin.io/quickstart.sh | bash -s
我放到了 目录：C:\github\online-taxi-demo  下面。


java -jar zipkin.jar

或者docker：
docker run -d -p 9411:9411 openzipkin/zipkin
```

