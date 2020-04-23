package com.qhc.consumer.controller;

import com.qhc.consumer.iface.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ConsumerController{

    @Autowired
    UserInterface userInterface;

    @GetMapping("/myalive")
    public String alive(){
        return userInterface.alive()+" consumer 82";
    }

}
