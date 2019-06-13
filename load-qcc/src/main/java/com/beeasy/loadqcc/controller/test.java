package com.beeasy.loadqcc.controller;

import com.beeasy.loadqcc.service.Get1;
import com.beeasy.loadqcc.service.Get2;
import com.beeasy.loadqcc.service.Get3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.beetl.sql.core.SQLManager;

@RestController
public class test {

//    @Autowired
//    SQLManager sqlManager;

    @Autowired
    Get1 get1;

    @Autowired
    Get2 get2;

    @Autowired
    Get3 get3;

    @RequestMapping(value = "/test")
    public void test(){
        get1.getData("惠州市昌氏实业有限公司");
        get2.getData("澳达树熊涂料（惠州）有限公司");
        get3.getData("澳达树熊涂料（惠州）有限公司");
    }
}
