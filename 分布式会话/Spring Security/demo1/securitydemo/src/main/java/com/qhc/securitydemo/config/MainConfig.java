package com.qhc.securitydemo.config;

import com.qhc.securitydemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;

//@Configuration
//@EnableWebSecurity
public class MainConfig extends WebSecurityConfigurerAdapter {

    //    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //设置编码器，配置这个就不用下面的@Bean方法了
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    String passEncoder = encoder.encode("123456");
//
//
//        auth.inMemoryAuthentication()
//                .withUser("lujinzhi")
//                .password(passEncoder)
//                .roles("USER")
//                .and()//这里使用and终结上面的配置，开始一行新的配置。
//                .withUser("quhaichuan")
//                .password(encoder.encode("123456"))
//                .roles("ADMIN");
//    }
    //需要指定一个对于密码的加密器，不然在这个版本里会报错
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }

    //资源控制


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/mylogin").permitAll()
                //自定义参数
                .usernameParameter("myusername")
                .passwordParameter("mypassowrd")
                //指定默认成功后的登录页面
                .defaultSuccessUrl("/index", true)
                //添加异常原因处理器
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        //打印异常信息，不然有的异常原因无法排查
                        e.printStackTrace();
                    }
                })
                .and()
                .rememberMe()

        ;

        //创建用户形式二：
//        User userBuilder = (User) User.withDefaultPasswordEncoder()
//                .username("zhangsan")
//                .password("123456")
//                .build();
    }

    //忽略静态请求
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/img/**","/js/**","/css/**");
//    }

    //通过UserDetailsService(SpringSecurity内建)创建对象，为后面jdbc存储对象打基础，使用这个的话，上面的configure就可以不写了
//    @Bean
//    public UserDetailsService userDetailsService(){
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        User user = new User("a",passEncoder,true,true,true,true,
//                Collections.singletonList(new SimpleGrantedAuthority("USER")));
//        manager.createUser(user);
//        manager.createUser(User.withUsername("quhaichuan").password(passEncoder).roles("ADMIN").build());
//
//        return manager;
//    }

    //创建JdbcUserDetailsManager的时候要用到DataSource
    @Autowired
    DataSource dataSource;

    //将账号密码添加到mysql，系统启动后会自动创建用户到mysql数据库
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        User user = new User("a", passEncoder, true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("USER")));
        if(!manager.userExists("quhaichuan")) {
            System.out.println("用户不存在，需要创建");
            manager.createUser(User.withUsername("quhaichuan").password(passEncoder).roles("ADMIN").build());
        }

//            manager.createUser(user);

        return manager;
    }

    //使用mybatis/jpa查询用户，可以实现自定义用户查询（框架）
    @Autowired
    UserService userSrv;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里使用自定义的UserService,里面需要实现指定的接口
        auth.userDetailsService(userSrv);
    }


}
