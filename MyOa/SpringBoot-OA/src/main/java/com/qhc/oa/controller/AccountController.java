package com.qhc.oa.controller;

import com.qhc.oa.entity.Account;
import com.qhc.oa.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    //登录后首页跳转
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    //跳转到登录页面
    @RequestMapping("login")
    public String login(){
        return "account/login";
    }

    //登录时权限校验
    @RequestMapping("/validataAccount")
    @ResponseBody
    public String validataAccount(String loginName,
                                  String password,
                                  HttpServletRequest request){
        System.out.println("loginName:"+loginName);
        System.out.println("password:"+password);
        Account account = accountService.findByLoginNameAndPassword(loginName,password);
        if(account == null){
            return "登录失败";
        }else{
            //通过请求获取session
            request.getSession().setAttribute("account",account);
            return "success";
        }
    }
}
