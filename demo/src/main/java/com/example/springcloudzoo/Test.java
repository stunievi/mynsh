package com.example.springcloudzoo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.springcloudzoo.SayHello;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@ImportResource("dubbo-consumer.xml")
@Component
public class Test {
    public String bb = "ff";

    @Reference
    public SayHello sayHello;
}
