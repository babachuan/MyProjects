package com.qhc.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/User")
public class UserController {

    @GetMapping("/alive")
    public String alive(){
        return "hello";
    }
}
