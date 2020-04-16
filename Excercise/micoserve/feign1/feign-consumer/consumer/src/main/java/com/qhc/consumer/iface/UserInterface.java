package com.qhc.consumer.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(name="user-consumer",url="http://192.168.1.10:81/User")
@FeignClient("user-provider")
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();
}
