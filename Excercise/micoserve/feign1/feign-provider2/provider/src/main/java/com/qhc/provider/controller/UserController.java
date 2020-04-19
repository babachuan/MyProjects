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

    private AtomicInteger count = new AtomicInteger();

    @GetMapping("/alive")
    public String alive(){
        //打印调用次数
        int i = count.getAndIncrement();
        System.out.println("这是第："+i+"次调用");

        //模拟超时
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
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
