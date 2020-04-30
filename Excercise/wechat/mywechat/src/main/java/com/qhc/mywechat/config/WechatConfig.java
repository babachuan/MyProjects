package com.qhc.mywechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WechatConfig {
    @Value(value = "${wechate.appID}")
    private String appID;

    @Value(value = "${wechate.appsecret}")
    private String appsecret;

    @Value(value = "${wechate.token}")
    private String token;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
