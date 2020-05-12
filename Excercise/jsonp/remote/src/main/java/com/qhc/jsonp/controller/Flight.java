package com.qhc.jsonp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qhc.jsonp.beans.FlightBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/flight")
public class Flight {
    @Autowired
    FlightBean flightBean;

    @RequestMapping("/result")
    public String check(String code,String callback){

        flightBean.setCode(code);
        flightBean.setPrice("1200");
        flightBean.setTickets("50");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(flightBean);

        String result=callback+"("+jsonObject+")";
        System.out.println(result);

        return result;

    }


}
