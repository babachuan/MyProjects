package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyTest {
    public static void main(String args[]) {
        System.out.println("hello world");
        String[] cmd = new String[]{"/bin/sh", "-c", " df -h "};
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
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
