package com.qhc.consumer.fallback;

import com.qhc.consumer.entity.Person;
import com.qhc.consumer.iface.UserInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserProviderBack implements UserInterface {
    @Override
    public String alive(){
        return "降级处理了";
    }

    @Override
    public Person postPerson(String name, String age) {
        return null;
    }

    @Override
    public List<Person> postPersonByMap(Map<String, String> map) {
        return null;
    }
}
