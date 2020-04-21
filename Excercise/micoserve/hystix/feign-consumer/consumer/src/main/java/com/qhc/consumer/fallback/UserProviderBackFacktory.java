package com.qhc.consumer.fallback;

import com.qhc.consumer.entity.Person;
import com.qhc.consumer.iface.UserInterface;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component //交给spring托管
public class UserProviderBackFacktory implements FallbackFactory<UserInterface> { //泛型是要监控的接口
    @Override
    public UserInterface create(Throwable throwable) {
        return new UserInterface() {
            @Override
            public String alive() {
                System.out.println(throwable);
                if(throwable instanceof FeignException.InternalServerError){
                    System.out.println("InternalServerError");
                    System.out.println(throwable.getLocalizedMessage());
                    return "远程服务报错";
                }
                return "通过FallBackFactory降级";
            }

            @Override
            public Person postPerson(String name, String age) {
                return null;
            }

            @Override
            public List<Person> postPersonByMap(Map<String, String> map) {
                return null;
            }
        };
    }
}
