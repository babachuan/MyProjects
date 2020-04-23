package com.qhc.consumer.limitbythread;

public class ThreadCommandTest {
    public static void main(String[] args) {
        for(int i=0;i<100;i++){
            new ThreadTest(i).start();
        }
    }

    //这里使用多线程，模拟并发操作
    private static class ThreadTest extends Thread{
        private int index;
        //构造方法
        public ThreadTest(int index){
            this.index = index;
        }

        @Override
        public void run() {
            //每启动一个线程，都调用一次服务
            ThreadCommant threadCommant = new ThreadCommant("success");
            System.out.println("第 "+(index + 1) + " 次请求"+ threadCommant.execute());
        }
    }
}
