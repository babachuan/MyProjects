package com.qhc.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface UserApi {

    @GetMapping("/isAlive")
    public String isAlive();
}
