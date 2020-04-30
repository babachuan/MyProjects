package com.qhc.mywechat.popular;

import com.qhc.mywechat.config.WechatConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

@RequestMapping("/popular")
@Controller
public class PopularController {
    private Logger logger = LoggerFactory.getLogger(PopularController.class);

    @Autowired
    WechatConfig wechatConfig;

    @RequestMapping("/login")
    public String login(){
        String redirectUrl = "http://2kyvhg.natappfree.cc/popular/callback";
        String url = SnsAPI.connectOauth2Authorize(wechatConfig.getAppID(), redirectUrl, true, "STATE");
        logger.info("my url=========="+url);
        return "redirect:"+url;  //这里前缀要加"redirect:"
    }

    @RequestMapping("/callback")
    public String callback(String code,String state){
        SnsToken snsToken = SnsAPI.oauth2AccessToken(wechatConfig.getAppID(), wechatConfig.getAppsecret(), code);
        User userInfo = SnsAPI.userinfo(snsToken.getAccess_token(), snsToken.getOpenid(), "zh_CN");
        logger.info("======="+ ToStringBuilder.reflectionToString(userInfo));
        return "welcom";
    }
}
