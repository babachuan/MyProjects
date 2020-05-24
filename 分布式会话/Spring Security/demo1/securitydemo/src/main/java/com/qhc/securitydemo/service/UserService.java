package com.qhc.securitydemo.service;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService { //需要实现这个接口，里面就可以扩展真正的操作了
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("=============="+s);
        if("quhaichuan".equals(s)){
            System.out.println("账号已经存在");
            //如果用户登录的时候，可以查询具体的原因，使用指定的异常
//            throw new LockedException("用户已锁定");
        }else {
            System.out.println("账号不存在");
        }

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String passEncoder = encoder.encode("123456");
        User user = new User("abc", passEncoder, true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("USER")));
        return user;
    }
}
