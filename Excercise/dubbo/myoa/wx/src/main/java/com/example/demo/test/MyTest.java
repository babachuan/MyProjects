package com.example.demo.test;


public class MyTest {
    public static void main(String args[]) {
        System.out.println("hello world");
        String cmds = "df -h";
        CMDUtils cmdUtils = new CMDUtils(cmds);
        String result = (String) cmdUtils.cmd();
        System.out.println("我拿到的结果是：============================");
        System.out.println(result);
    }
}
