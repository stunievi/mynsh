package com.example.springcloudzoo;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class Test implements SayHello{

    public String test(){
        return "hellow";
    }

    @Override
    public String sayHello(String name) {
        return "rilegouhahaha";
    }
}
