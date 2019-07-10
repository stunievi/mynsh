package com.beeasy.hzlink;

public class ConnDemo {
    // 测试 请求外网
    public static void main(String[] args){
        Thread thread=new Thread(new connTest());
        thread.start();
    }
}
