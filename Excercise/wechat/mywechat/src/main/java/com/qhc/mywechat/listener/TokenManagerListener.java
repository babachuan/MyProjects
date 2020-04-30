package com.qhc.mywechat.listener;

import com.qhc.mywechat.config.WechatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import weixin.popular.support.TokenManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TokenManagerListener implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(TokenManagerListener.class);

    @Autowired
    WechatConfig wechatConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("------------TokenManagerListener----contextInitialized-----------------------------");
        TokenManager.init(wechatConfig.getAppID(),wechatConfig.getAppsecret());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("------------TokenManagerListener----contextDestroyed-----------------------------");
        TokenManager.destroyed();
    }
}
