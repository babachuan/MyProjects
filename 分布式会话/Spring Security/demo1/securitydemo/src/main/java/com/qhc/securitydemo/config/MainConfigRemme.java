package com.qhc.securitydemo.config;

import com.qhc.securitydemo.filter.CodeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class MainConfigRemme extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置编码器，配置这个就不用下面的@Bean方法了
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser("quhaichuan")
                .password(encoder.encode("123456"))
                .roles("ADMIN")
                .and()
                .withUser("lujinzhi")
                .password(encoder.encode("123456"))
                .roles("USER")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //图形验证码前置filter
        http.addFilterBefore(new CodeFilter(), UsernamePasswordAuthenticationFilter.class);



        http.authorizeRequests()
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/image/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/mylogin").permitAll()
                //自定义参数
                .usernameParameter("myusername")
                .passwordParameter("mypassowrd")
                //指定默认成功后的登录页面
                .defaultSuccessUrl("/index", true)
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        //打印异常信息，不然有的异常原因无法排查
                        e.printStackTrace();
                    }
                })
//                .and()
//                .rememberMe() //由于测试多用户登录，这里需要去掉
//                .sessionManagement()
//                .maximumSessions(4) //只允许一个用户登录，第二个会被踢掉
//                .maxSessionsPreventsLogin(true) //为true后禁止新的用户登录，这种情况下要及时清理seesion
        ;
    }

    //及时清理过期session
//    @Bean
//    HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }

    //权限继承,这样ADMIN的权限就囊括了USER的权限，这个控制粒度比较粗
//    @Bean
//    RoleHierarchy roleHierarchy() {
//        RoleHierarchyImpl impl = new RoleHierarchyImpl();
//        impl.setHierarchy("ROLE_ADMIN > ROLE_USER");
//        return impl;
//    }
}
