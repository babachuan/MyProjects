package com.qhc.mywechat.wxauth;

import com.alibaba.fastjson.JSONObject;
import com.qhc.mywechat.config.WechatConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/weixin/auth")
public class WeixinAuthController {

    @Autowired
    WechatConfig wechatConfig;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/login")
    public String authorize() throws UnsupportedEncodingException {
        String recirectUrl = URLEncoder.encode("http://2kyvhg.natappfree.cc/weixin/auth/calback","UTF-8");

        //授权地址
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        url = url.replace("APPID",wechatConfig.getAppID()).replace("REDIRECT_URI",recirectUrl);

        System.out.println("=====url:"+url);
        return "redirect:"+url;
    }

    @RequestMapping("/calback")
    @ResponseBody
    public String calback(String code,String state) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url=url.replace("APPID",wechatConfig.getAppID()).replace("SECRET",wechatConfig.getAppsecret()).replace("CODE",code);
        String doGet = HttpUtils.HttpUtils(url);

        if(StringUtils.isNotBlank(doGet)){
            JSONObject jsonObject = JSONObject.parseObject(doGet);
            System.out.println(jsonObject);

            String accessToken = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            String getUserInfoURL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
            getUserInfoURL = getUserInfoURL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
            String result = HttpUtils.HttpUtils(getUserInfoURL);
            System.out.println(result);
            return result;
        }

        return "hello";
    }
}
