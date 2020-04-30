package com.qhc.mywechat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import weixin.popular.util.SignatureUtil;

@SpringBootTest
class MywechatApplicationTests {

    String token = "luoye";
    String timestamp="1587795229";
    String nonce="1533670961";

    @Test
    void contextLoads() {

        String s = SignatureUtil.generateEventMessageSignature(token, timestamp, nonce);
        System.out.println(s);
    }

}
