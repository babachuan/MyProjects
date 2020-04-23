# 1.SpringCloud Admin健康检查

## 1.1 服务端搭建

引入依赖

```
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-server</artifactId>
        </dependency>
```

在启动类添加注解

```
@EnableAdminServer
```

配置文件

```
server.port=8080
spring.application.name=admin-server
```

配置完后启动即可，客户端搭建完成。

## 1.2 客户端搭建

引入依赖

```
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
            <version>2.2.1</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

添加添加配置

```
#spring.boot.admin.client.url=http://localhost:8080/
#management.endpoints.web.exposure.include=*
#management.endpoint.health.show-details=always
```

这样客户端在启动后会自动注册到Spring cloud admin

# 2.服务端的权限认证

上面搭建的是一个最简单的admin服务端和客户端使用模型，生产环境中需要对访问的资源做权限认证。

## 2.1 服务端权限认证搭建

引入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

新创建两个类

**SecuritySecureConfig**

```
package com.qhc.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
    private final String adminContextPath;

    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/monitor");

        http.authorizeRequests()
                .antMatchers(adminContextPath + "/assets/**").permitAll()
                .antMatchers(adminContextPath + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
                .logout().logoutUrl(adminContextPath + "/logout").and()
                .httpBasic().and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers(
                        adminContextPath + "/instances",
                        adminContextPath + "/actuator/**"
                );
        // @formatter:on
    }
}


```

**WebSecurityConfig**

```
package com.qhc.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username("root").password("root").roles("administrator").build());
        return manager;
    }
}


```

上面就配置了账号和密码

> [官网文档](https://codecentric.github.io/spring-boot-admin/2.1.6/#_securing_spring_boot_admin_server)

## 2.2客户端使用

客户端在使用的时候只需要添加对应的账号密码配置即可

```
spring.boot.admin.client.username=root
spring.boot.admin.client.password=root
```

# 3.通过Eureka搭建

上面的模式是每个客户端都需要部署和配置一遍，那么如果是通过Eureka的话，只需要连接Eureka注册中心即可

参见`admin-byeureka`

admin引入依赖

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.2.2.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.jolokia</groupId>
            <artifactId>jolokia-core</artifactId>
        </dependency>
```

添加访问Eureka的配置

```
eureka.client.service-url.defaultZone=http://quhaichuan:123456@eurk1.com:7001/eureka
```

这样就方便了部署和配置，省去了客户端的重复配置

# 4.Admin发送邮件

在admin端监控服务，如果有服务下线了，那么最好有个通知，邮件通知是一种形式。配置如下：

引入依赖

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.2</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/junit/junit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13</version>
            <scope>test</scope>
        </dependency>
```

添加配置：

```

#邮件配置
spring.mail.host=smtp.mxhichina.com
spring.mail.port=25
spring.mail.username=quhc@geneplus.org.cn
spring.mail.password=ENC(加密后的密码)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

spring.boot.admin.notify.mail.to=quhc@geneplus.org.cn
spring.boot.admin.notify.mail.from=quhc@geneplus.org.cn

jasypt.encryptor.password=qhc
```

工具类加密密码

```

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminApplicationTests {

    @Autowired
    StringEncryptor stringEncryptor;

    @Test
    void contextLoads() {
        String result = stringEncryptor.encrypt("明文密码");
        System.out.println(result);
    }
}

```

将这里生成密码放到`spring.mail.password`，这样在服务下线的时候会收到一个邮件，内容如下：

```
USER-CONSUMER (38dd03fc607c) is OFFLINE
Instance 38dd03fc607c changed status from UP to OFFLINE

Status Details
exception
io.netty.channel.AbstractChannel$AnnotatedConnectException
message
Connection refused: no further information: /172.16.28.152:82
Registration
Service Url	http://172.16.28.152:82/
Health Url	http://172.16.28.152:82/actuator/health
Management Url	http://172.16.28.152:82/actuator
```

