package com.qhc.oa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TescController {
    @RequestMapping("test")
    public String test(){
        System.out.println("访问/text......");
        return "test";
    }
}
