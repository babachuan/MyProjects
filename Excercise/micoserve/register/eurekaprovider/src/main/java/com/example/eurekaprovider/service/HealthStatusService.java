package com.example.eurekaprovider.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusService implements HealthIndicator {
    private Boolean status = true;

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public Health health() {
        if(status){
            //开启服务
            return new Health.Builder().up().build();
        }
        //否则的话就down服务
        return new Health.Builder().down().build();
    }
}
