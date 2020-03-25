package com.qhc.oa.controller;

import com.github.pagehelper.PageInfo;
import com.qhc.oa.RespState;
import com.qhc.oa.entity.Account;
import com.qhc.oa.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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


    //退出登陆
    @RequestMapping("logOut")
    public String logOut(HttpServletRequest request){
        request.getSession().removeAttribute("account");
        return "account/login";
    }

    //用户列表
    @RequestMapping("list")
    public String accountList(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model){
        PageInfo<Account> page = accountService.findByPage(pageNum,pageSize);
//        System.out.println("==============="+page);
        model.addAttribute("accountList", page);
        return "account/list";
    }

    //删除记录
    @RequestMapping("deleteById")
    @ResponseBody
    public RespState accountList(@RequestParam int id){
        RespState respState = accountService.deleteById(id);
        return respState;
    }
}
