package com.beeasy.loadqcc.service;


import com.beeasy.loadqcc.utils.JsonConvertUtils;

public class Transition {

    public void aa() {
        String data = "{\"ShowPic1\":{\"ShowPic3\":[{\"TestParam\":\"test\",\"TestParam2\":[{\"Apple\":\"苹果\"}]}]},\"BeautifulWorld\":\"\",\"WidthHeightLower\":\"\"}";
        String json= "{'user_name':'ok','user_sex':0,'ubject_info':{'business_code':'0001','uusiness_info':{'business_iame':'ok'}}}";
        Object bb  = JsonConvertUtils.convert4(json);

        System.out.println(bb);
    }



}

