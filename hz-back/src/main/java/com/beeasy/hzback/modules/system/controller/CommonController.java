package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.service.CommonService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试公共controller
 */
@RestController
@RequestMapping(value = "/api")
public class CommonController {

    @Autowired
    CommonService commonService;

    /**
     * 往表中插入数据
     */
    @Test
    @RequestMapping(value = "/insert/cusCom", method = RequestMethod.GET)
    public void Test(){
        commonService.delete();
        commonService.insert();
    }


}
