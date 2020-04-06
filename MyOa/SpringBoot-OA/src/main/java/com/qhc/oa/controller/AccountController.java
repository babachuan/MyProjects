package com.qhc.oa.controller;

import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qhc.oa.RespState;
import com.qhc.oa.entity.Account;
import com.qhc.oa.service.AccountService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    FastFileStorageClient fastFileStorageClient;

    //登录后首页跳转
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    //无权限后跳转到错误页面
    @RequestMapping("/errorPage")
    public String errorPage(){
        return "errorPage";
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

        //这里有个bug，只返回权限列表里的第一个permission的uri
        Account account = accountService.findByLoginNameAndPasswordAll(loginName,password);
//        System.out.println("获取到的值是："+ ToStringBuilder.reflectionToString(account));
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

    @RequestMapping("profile")
    public String profile(HttpServletRequest request){

        Account account = (Account) request.getSession().getAttribute("account");
        System.out.println(ToStringBuilder.reflectionToString(account));
        return "account/profile";
    }

    //上传头像
    @RequestMapping("fileUploadController")
    public String fileUpload(MultipartFile filename,HttpServletRequest request){
        Set<MetaData> metaData = new HashSet<MetaData>();
        metaData.add(new MetaData("Author","quhaichuan"));
        metaData.add(new MetaData("CreateDate","2020-04-06"));
        try {
//            StorePath uploadFile = fastFileStorageClient.uploadFile(filename.getInputStream(),filename.getSize(),
//                    FilenameUtils.getExtension(filename.getOriginalFilename()),metaData);//            StorePath uploadFile = fastFileStorageClient.uploadFile(filename.getInputStream(),filename.getSize(),
            StorePath uploadFile = fastFileStorageClient.uploadImageAndCrtThumbImage(filename.getInputStream(),filename.getSize(),
                    FilenameUtils.getExtension(filename.getOriginalFilename()),metaData);
            System.out.println(uploadFile.getFullPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "account/profile";
    }
}
