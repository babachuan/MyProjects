package com.example.demo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public  class CMDUtils {
    private String cmds;

    public CMDUtils(String cmds) {
        this.cmds = cmds;
    }

    public String getCmds() {
        return cmds;
    }

    public void setCmds(String cmds) {
        this.cmds = cmds;
    }

    public Object cmd(){
        System.out.println("执行的命令是："+this.cmds);
        String[] cmd = new String[]{"/bin/sh", "-c", this.cmds};
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
