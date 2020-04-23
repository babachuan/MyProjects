package com.qhc.consumer.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "user-provider")
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();

}
