package com.qhc.consumer.controller;

import com.qhc.consumer.entity.Person;
import com.qhc.consumer.iface.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ConsumerController{

    @Autowired
    UserInterface userInterface;

    @GetMapping("/myalive")
    public String alive(){
        return userInterface.alive()+" consumer2 84";
    }

    @GetMapping("/postPerson")
    public Person postPerson(@RequestParam("name") String name,@RequestParam("age") String age){
       return userInterface.postPerson(name,age);
    }

    //用map接收参数
    @GetMapping("/postPersonByMap")
    public List<Person> postPersonByMap(@RequestParam Map<String,String> map){
        return userInterface.postPersonByMap(map);
    }
}
