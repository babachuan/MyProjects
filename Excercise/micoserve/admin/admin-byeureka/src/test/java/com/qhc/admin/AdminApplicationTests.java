package com.qhc.admin;

import com.netflix.discovery.converters.Auto;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminApplicationTests {

    @Autowired
    StringEncryptor stringEncryptor;

    @Test
    void contextLoads() {
        String result = stringEncryptor.encrypt("xxx");
        System.out.println(result);
    }

}
