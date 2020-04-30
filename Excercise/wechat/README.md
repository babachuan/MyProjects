# 1.微信签名

由于微信开发需要使用外网，这里使用natapp(https://natapp.cn/)外网穿透工具进行测试。



## 1.1 接口配置信息

这里使用的是微信测试号进行测试，`公众号首页->开发->开发者工具->公众平台测试账号`进入，在这里输入

- url:这个接口是给微信调用的，需要开发外网，下面会介绍
- token:这个是对称加密用的，可以自定义

点击`提交`后提示配置成功（需要url对应的服务开启）

*注意*：这个页面的**测试号信息**中的appID和appsecret要在url对应发服务中认证签名时使用。

## 1.2 外网映射配置

**web端配置**

在NAPAPP中的`我的隧道->我的隧道->配置`页面进行配置

本地地址：是在终端使用的，这里就配置成127.0.0.1即可

本地端口：就是映射终端的本地端口，这里设置成8080

之后保存即可。

**终端启动**

下载的终端不用配置，直接启动即可

```
  E:\download>natapp -authtoken=5b38789fdca473cb
```

这里的authtoken是上面web页面的参数，直接拷贝过来使用即可。启动后会随机分配一个域名，直接使用这个域名就可以映射本地的8080端口了。



## 1.3 本地服务

本地服务为了简化开发，使用第三方微信框架

```
        <dependency>
            <groupId>com.github.liyiorg</groupId>
            <artifactId>weixin-popular</artifactId>
            <version>2.8.28</version>
        </dependency>
```

github地址：https://github.com/liyiorg/weixin-popular

**配置文件**

```
server.port=8080

wechate.appID=wxad989a166409ccd9
wechate.appsecret=22ad0a9990885f83a4a67ab993b2317c
wechate.token=luoye
```

配置文件的appIDh和appsecret和token跟微信服务端的配置保持一致即可。



**WechatConfig类**

```
package com.qhc.mywechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WechatConfig {
    @Value(value = "${wechate.appID}")
    private String appID;

    @Value(value = "${wechate.appsecret}")
    private String appsecret;

    @Value(value = "${wechate.token}")
    private String token;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

```

**MyController类**

```
@Controller
@RequestMapping("/wechat")
public class MyController {

    @Autowired
    WechatConfig wechatConfig;

    Logger logger = LoggerFactory.getLogger(MyController.class);

    @RequestMapping("/sig")
    @ResponseBody
    public void sig(@RequestParam Map<String, String> param, HttpServletRequest request, HttpServletResponse response) throws Exception{
        ServletInputStream inputStream = request.getInputStream();
        ServletOutputStream outputStream = response.getOutputStream();

        //计算签名
        String signature = param.get("signature");
        String echostr = param.get("echostr");
        String timestamp = param.get("timestamp");
        String nonce = param.get("nonce");
        logger.info(signature+" | "+echostr+" | "+timestamp+" | "+nonce);

        //对称加密
        String token = wechatConfig.getToken();

        //进行非空判断
        if(StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp)){
            outputStreamWrite(outputStream,"fail request");
            return;
        }
        //验证请求签名，核心代码
        if(signature.equals(SignatureUtil.generateEventMessageSignature(token,timestamp,nonce))){
            logger.info("--------------> 签名生效");
        }

        //验证请求签名，核心代码
        if(!signature.equals(SignatureUtil.generateEventMessageSignature(token,timestamp,nonce))){
            logger.info("--------------> The request signature is invalid");
            return;
        }

        if(echostr != null){
            outputStreamWrite(outputStream,echostr);
            logger.info("--------------->认证成功");
            return;
        }
    }

    private boolean outputStreamWrite(OutputStream outputStream,String text){
        try {
            outputStream.write(text.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
```

这里保留的接口`IP:PORT/wechat/sig`就是给微信签名认证用的，直接将这个url添加到微信即可。

这样每次操作公众号的时候都会来认证一次。

# 2.自定义菜单

除了上面的签名认证外，与微信公众号交互还需要token 验证。

**TicketManagerListener**

```
package com.qhc.mywechat.listener;

import com.qhc.mywechat.config.WechatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sun.rmi.runtime.Log;
import weixin.popular.support.TicketManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TicketManagerListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(TicketManagerListener.class);

    @Autowired
    WechatConfig wechatConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("-----------------TicketManagerListener--------contextInitialized------------------");
        TicketManager.init(wechatConfig.getAppID(),15,60*119);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("-----------------TicketManagerListener--------contextDestroyed------------------");
        TicketManager.destroyed();

    }
}

```

**TokenManagerListener**

```
package com.qhc.mywechat.listener;

import com.qhc.mywechat.config.WechatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import weixin.popular.support.TokenManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TokenManagerListener implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(TokenManagerListener.class);

    @Autowired
    WechatConfig wechatConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("------------TokenManagerListener----contextInitialized-----------------------------");
        TokenManager.init(wechatConfig.getAppID(),wechatConfig.getAppsecret());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("------------TokenManagerListener----contextDestroyed-----------------------------");
        TokenManager.destroyed();
    }
}

```

需要在启动类上添加如下注解

```
@ServletComponentScan(basePackages = "com.qhc.mywechat.listener")
```

在服务启动时就会进行获取。

**MyController**

```

    @RequestMapping("createMenu")
    @ResponseBody
    public BaseResult createMenu(){
        String MenuString = "{\r\n" +
                "     \"button\":[\r\n" +
                "     {    \r\n" +
                "          \"type\":\"click\",\r\n" +
                "          \"name\":\"今日歌曲\",\r\n" +
                "          \"key\":\"V1001_TODAY_MUSIC\"\r\n" +
                "      },\r\n" +
                "      {\r\n" +
                "           \"name\":\"菜单\",\r\n" +
                "           \"sub_button\":[\r\n" +
                "           {    \r\n" +
                "               \"type\":\"view\",\r\n" +
                "               \"name\":\"百度\",\r\n" +
                "               \"url\":\"http://www.baidu.com/\"\r\n" +
                "            },\r\n" +
                "           {    \r\n" +
                "               \"type\":\"view\",\r\n" +
                "               \"name\":\"搜狗\",\r\n" +
                "               \"url\":\"http://www.soso.com/\"\r\n" +
                "            },\r\n" +
                "            {\r\n" +
                "               \"type\":\"view\",\r\n" +
                "               \"name\":\"腾讯视频\",\r\n" +
                "               \"url\":\"http://v.qq.com/\"\r\n" +
                "            }]\r\n" +
                "       }]\r\n" +
                " }";


        BaseResult baseResult = MenuAPI.menuCreate(TokenManager.getDefaultToken(),MenuString);
        return baseResult;
    }
```

如果调试过程中因为缓存问题不能看到效果，可以尝试用浏览器访问链接清楚缓存

```
http://q7aa43.natappfree.cc/wechat/createMenu?111112
## 随便参数后面加什么参数，就能清掉原来的缓存，要取关重新关注下
```

# 3.处理文本消息

微信处理消息，在上面的`/sig`接口上继续完善

添加接收信息的变量

```
    //过滤重复通知
    private static ExpireKey expireKey = new DefaultExpireKey();
    
        //接收消息
    private String msg;
```

```

        //============处理消息================
        if(inputStream != null){
            //转换XML
            EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class,inputStream);
            logger.info("------>eventMessage:"+ ToStringBuilder.reflectionToString(eventMessage));
            String key = eventMessage.getFromUserName()+"_"+eventMessage.getToUserName()+"_"+eventMessage.getMsgId()+"_"+eventMessage.getCreateTime();

            switch(eventMessage.getContent()){
                case "hello":
                    msg="你好";
                    break;
                case "添加好友":
                    msg="已是好友";
                    break;
            }

            //根据key去重，如果重复不处理
            if(expireKey.exists(key)){
                logger.info("重复通知，不做处理");
                return;
            }else {
                expireKey.add(key);
            }

            //发送消息
            XMLTextMessage xmlTextMessage = new XMLTextMessage(eventMessage.getFromUserName(),eventMessage.getToUserName(),msg);
            xmlTextMessage.outputStreamWrite(outputStream);
            return;
        }
```

这样，在公众号上编辑消息`hello`,微信公众号会回复`你好`，哈哈

# 4.发送图片消息

在上面文本消息的基础上进行改造,添加一个上传图片的按钮

```

        String menuString2 = "{\r\n" +
                "    \"button\": [\r\n" +
                "        {\r\n" +
                "            \"name\": \"扫码\",\r\n" +
                "            \"sub_button\": [\r\n" +
                "                {\r\n" +
                "                    \"type\": \"scancode_waitmsg\",\r\n" +
                "                    \"name\": \"扫码带提示\",\r\n" +
                "                    \"key\": \"rselfmenu_0_0\",\r\n" +
                "                    \"sub_button\": []\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"type\": \"scancode_push\",\r\n" +
                "                    \"name\": \"扫码推事件\",\r\n" +
                "                    \"key\": \"rselfmenu_0_1\",\r\n" +
                "                    \"sub_button\": []\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"name\": \"发图\",\r\n" +
                "            \"sub_button\": [\r\n" +
                "                {\r\n" +
                "                    \"type\": \"pic_sysphoto\",\r\n" +
                "                    \"name\": \"系统拍照发图\",\r\n" +
                "                    \"key\": \"rselfmenu_1_0\",\r\n" +
                "                    \"sub_button\": []\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"type\": \"pic_photo_or_album\",\r\n" +
                "                    \"name\": \"拍照或者相册发图\",\r\n" +
                "                    \"key\": \"rselfmenu_1_1\",\r\n" +
                "                    \"sub_button\": []\r\n" +
                "                },\r\n" +
                "                {\r\n" +
                "                    \"type\": \"pic_weixin\",\r\n" +
                "                    \"name\": \"微信相册发图\",\r\n" +
                "                    \"key\": \"rselfmenu_1_2\",\r\n" +
                "                    \"sub_button\": []\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"name\": \"发送位置\",\r\n" +
                "            \"type\": \"location_select\",\r\n" +
                "            \"key\": \"rselfmenu_2_0\"\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}";
```

然后再`sig`接口里面配置

```
//发送图片
XMLImageMessage xmlImageMessage = new XMLImageMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), eventMessage.getMediaId());
xmlImageMessage.outputStreamWrite(outputStream);
```

这样就可以实现，手机给微信公众号发送一张图片，微信公众号将图片返回给用户。

# 5.登录授权

如果用户在微信客户端中访问第三方网页，公众号可以通过微信网页授权机制，来获取用户基本信息，进而实现业务逻辑。

总的来说，分为四部：

1、引导用户进入授权页面同意授权，获取code

2、通过code换取网页授权access_token（与基础支持中的access_token不同）

3、如果需要，开发者可以刷新网页授权access_token，避免过期

4、通过网页授权access_token和openid获取用户基本信息（支持UnionID机制）

参见微信官方文档：https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html

这部分参见`com.qhc.mywechat.wxauth`目录下的文件

首先编写一个工具类，通过url获取Json数据

```

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtils {

    public static String HttpUtils(String url) throws IOException {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(),"utf-8"));//防止乱码
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }
}
```

编写授权代码

```

import com.alibaba.fastjson.JSONObject;
import com.qhc.mywechat.config.WechatConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/weixin/auth")
public class WeixinAuthController {

    @Autowired
    WechatConfig wechatConfig;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/login")
    public String authorize() throws UnsupportedEncodingException {
        String recirectUrl = URLEncoder.encode("http://8whwrz.natappfree.cc/weixin/auth/calback","UTF-8");

        //授权地址
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        url = url.replace("APPID",wechatConfig.getAppID()).replace("REDIRECT_URI",recirectUrl);

        System.out.println("=====url:"+url);
        return "redirect:"+url;
    }

    @RequestMapping("/calback")
    @ResponseBody
    public String calback(String code,String state) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url=url.replace("APPID",wechatConfig.getAppID()).replace("SECRET",wechatConfig.getAppsecret()).replace("CODE",code);
        String doGet = HttpUtils.HttpUtils(url);

        if(StringUtils.isNotBlank(doGet)){
            JSONObject jsonObject = JSONObject.parseObject(doGet);
            System.out.println(jsonObject);

            String accessToken = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            String getUserInfoURL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
            getUserInfoURL = getUserInfoURL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
            String result = HttpUtils.HttpUtils(getUserInfoURL);
            System.out.println(result);
            return result;
        }

        return "hello";
    }
}
```

编写完成后，需要在微信测试账号的`网页帐号->网页授权获取用户基本信息->修改`进行域名填写，不用添加http前缀。

配置完成后直接使用`域名//weixin/auth/index`进行访问（在微信端）可以弹出授权信息。

当然也可以通过第三方框架实现

```
        <!--        微信第三方框架-->
        <dependency>
            <groupId>com.github.liyiorg</groupId>
            <artifactId>weixin-popular</artifactId>
            <version>2.8.28</version>
        </dependency>
```

下面用框架实现了一遍，更简单

```
package com.qhc.mywechat.popular;

import com.qhc.mywechat.config.WechatConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

@RequestMapping("/popular")
@Controller
public class PopularController {
    private Logger logger = LoggerFactory.getLogger(PopularController.class);

    @Autowired
    WechatConfig wechatConfig;

    @RequestMapping("/login")
    public String login(){
        String redirectUrl = "http://2kyvhg.natappfree.cc/popular/callback";
        String url = SnsAPI.connectOauth2Authorize(wechatConfig.getAppID(), redirectUrl, true, "STATE");
        logger.info("my url=========="+url);
        return "redirect:"+url;  //这里前缀要加"redirect:"
    }

    @RequestMapping("/callback")
    public String callback(String code,String state){
        SnsToken snsToken = SnsAPI.oauth2AccessToken(wechatConfig.getAppID(), wechatConfig.getAppsecret(), code);
        User userInfo = SnsAPI.userinfo(snsToken.getAccess_token(), snsToken.getOpenid(), "zh_CN");
        logger.info("======="+ ToStringBuilder.reflectionToString(userInfo));
        return "welcom";
    }
}

```

用户访问到`http://2kyvhg.natappfree.cc/popular/login`连接的时候会让用户选择是否授权，可以获取用户信息，原理跟上面是一样的，只不过框架帮我们做了一些封装。

> [参考博客](https://www.cnblogs.com/qlqwjy/p/11795260.html)

