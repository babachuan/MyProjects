package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CMDUtils {

    public Object cmd(String cmds){
        System.out.println("执行的命令是："+cmds);
        String[] cmd = new String[]{"/bin/sh", "-c", cmds};
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = buffer.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            String result = stringBuffer.toString();
            System.out.println("执行的结果是：=================================");
            System.out.println(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }

    }
}
