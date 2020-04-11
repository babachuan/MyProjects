package com.example.eurekaprovider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/getHi")
    public String getHi(){

        return "hello from provide2";
    }
}
