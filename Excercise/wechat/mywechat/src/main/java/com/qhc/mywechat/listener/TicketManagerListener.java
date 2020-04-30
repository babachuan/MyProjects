package com.qhc.mywechat.listener;

import com.qhc.mywechat.config.WechatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.rmi.runtime.Log;
import weixin.popular.support.TicketManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TicketManagerListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(TicketManagerListener.class);

    @Autowired
    WechatConfig wechatConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("-----------------TicketManagerListener--------contextInitialized------------------");
        TicketManager.init(wechatConfig.getAppID(),15,60*119);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("-----------------TicketManagerListener--------contextDestroyed------------------");
        TicketManager.destroyed();

    }
}
