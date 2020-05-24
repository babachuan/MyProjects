package com.qhc.securitydemo.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class IndexController {

    //指定角色发访问
    @GetMapping("/test")
    @Secured("ROLE_ADMIN")  //只允许ADMIN角色访问，注意，前面需要加ROLE_前缀
    public String test(){
        //通过这段代码可以在任意位置获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        return "test";
    }
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/mylogin")
    public String myLogin(){
        return "login";
    }

    @GetMapping("/user/index")
    public String user(){
        return "users";
    }

    @GetMapping("/admin/index")
    public String admin(){
        return "admin";
    }

    //验证码的controller
    @Autowired
    Producer producer;

    @GetMapping("/image")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        HttpSession session = request.getSession();
        response.setDateHeader("Expires",0);
        response.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control","post-check=0, pre-check=0");
        response.setHeader("Pragma","no-chache");
        response.setContentType("image/jpeg");
        String capTest = producer.createText();
        System.out.println("声明的验证码==============："+capTest);

        session.setAttribute(Constants.KAPTCHA_SESSION_CONFIG_KEY,capTest);
        BufferedImage bufferedImage = producer.createImage(capTest);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(bufferedImage,"jpg",outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
