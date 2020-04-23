package com.qhc.provider.controller;

import com.qhc.provider.entity.Person;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/User")
public class UserController{


    @GetMapping("/alive")
    public String alive(){
//        int i = 1/0;
        return "provider 83 hello2";
    }

    @PostMapping("/postPerson")
    public Person postPerson(@RequestParam("name") String name,@RequestParam("age") String age){
        Person person1 = new Person(name,age);
        System.out.println(ToStringBuilder.reflectionToString(person1));
        return person1;
    }

    @PostMapping("/postPersonByMap")
    public List<Person> postPersonByMap(@RequestParam Map<String,String> map){
        ArrayList<Person> persons = new ArrayList<Person>();
        System.out.println(map.entrySet());
        persons.add(new Person(map.get("name"),map.get("age")));
        return persons;
    }
}
