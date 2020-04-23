package com.qhc.consumer.iface;

import com.qhc.consumer.entity.Person;
import com.qhc.consumer.fallback.UserProviderBack;
import com.qhc.consumer.fallback.UserProviderBackFacktory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(name="user-consumer",url="http://192.168.121.1:81")
//@FeignClient(value = "user-provider",fallback = UserProviderBack.class)  //粗粒度的降级
@FeignClient(value = "user-provider",fallbackFactory = UserProviderBackFacktory.class)  //细粒度的降级
public interface UserInterface {
    @GetMapping("/User/alive")
    public String alive();

    @PostMapping("/User/postPerson")
    public Person postPerson(@RequestParam("name") String name,@RequestParam("age") String age);

    //用map接收参数
    @PostMapping("/User/postPersonByMap")
    public List<Person> postPersonByMap(@RequestParam Map<String,String> map);
}
