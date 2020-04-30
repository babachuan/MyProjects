package com.qhc.mywechat;

import com.qhc.mywechat.config.WechatConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import weixin.popular.api.MenuAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLImageMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.support.ExpireKey;
import weixin.popular.support.TokenManager;
import weixin.popular.support.expirekey.DefaultExpireKey;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/wechat")
public class MyController {

    //过滤重复通知
    private static ExpireKey expireKey = new DefaultExpireKey();

    @RequestMapping("/hello")
    @ResponseBody
    public String test() {
        return "hello";
    }

    @Autowired
    WechatConfig wechatConfig;

    Logger logger = LoggerFactory.getLogger(MyController.class);

    //接收消息
    private String msg;


    @RequestMapping("createMenu")
    @ResponseBody
    public BaseResult createMenu() {
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


        BaseResult baseResult = MenuAPI.menuCreate(TokenManager.getDefaultToken(), menuString2);
        return baseResult;
    }


    @RequestMapping("/sig")
    @ResponseBody
    public void sig(@RequestParam Map<String, String> param, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletInputStream inputStream = request.getInputStream();
        ServletOutputStream outputStream = response.getOutputStream();

        //计算签名
        String signature = param.get("signature");
        String echostr = param.get("echostr");
        String timestamp = param.get("timestamp");
        String nonce = param.get("nonce");
        logger.info(signature + " | " + echostr + " | " + timestamp + " | " + nonce);

        //对称加密
        String token = wechatConfig.getToken();

        //进行非空判断
        if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp)) {
            outputStreamWrite(outputStream, "fail request");
            return;
        }


        //验证请求签名，核心代码
        if (signature.equals(SignatureUtil.generateEventMessageSignature(token, timestamp, nonce))) {
            logger.info("--------------> 签名生效");
        }

        //验证请求签名，核心代码
        if (!signature.equals(SignatureUtil.generateEventMessageSignature(token, timestamp, nonce))) {
            logger.info("--------------> The request signature is invalid");
            return;
        }


        if (echostr != null) {
            outputStreamWrite(outputStream, echostr);
            logger.info("--------------->认证成功");
            return;
        }


        //============处理消息================
        if (inputStream != null) {
            //转换XML
            EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, inputStream);
            logger.info("------>eventMessage:" + ToStringBuilder.reflectionToString(eventMessage));
            String key = eventMessage.getFromUserName() + "_" + eventMessage.getToUserName() + "_" + eventMessage.getMsgId() + "_" + eventMessage.getCreateTime();

//            switch (eventMessage.getContent()) {
//                case "hello":
//                    msg = "你好";
//                    break;
//                case "添加好友":
//                    msg = "已是好友";
//                    break;
//            }


            //根据key去重，如果重复不处理
            if (expireKey.exists(key)) {
                logger.info("重复通知，不做处理");
                return;
            } else {
                expireKey.add(key);
            }

            //发送消息
//            XMLTextMessage xmlTextMessage = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), msg);
//            xmlTextMessage.outputStreamWrite(outputStream);

            //发送图片
            XMLImageMessage xmlImageMessage = new XMLImageMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), eventMessage.getMediaId());
            xmlImageMessage.outputStreamWrite(outputStream);
            return;

        }

    }

    private boolean outputStreamWrite(OutputStream outputStream, String text) {
        try {
            outputStream.write(text.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
