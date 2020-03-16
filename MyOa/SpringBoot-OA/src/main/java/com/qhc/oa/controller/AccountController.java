package com.qhc.oa.controller;

import com.qhc.oa.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("login")
    public String login(){
        return "account/login";
    }

    @RequestMapping("/validataAccount")
    @ResponseBody
    public String validataAccount(@RequestParam("loginName") String loginName,@RequestParam("password") String password){
        System.out.println("loginName:"+loginName);
        System.out.println("password:"+password);
        return "success";
    }
}
