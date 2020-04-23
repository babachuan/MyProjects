package com.qhc.provider.controller;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/User")
public class UserController{


    @GetMapping("/alive")
    public String alive(){
        return "provider 83 hello2";
    }

}
