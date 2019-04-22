package com.beeasy.easyshop;

import com.beeasy.easyshop.core.EasyShop;

public class App {

    public static void main(String[] args) {
        //注册控制器
        EasyShop.registerController("qcc", "com.beeasy.easyshop.ctrl");
        EasyShop.start();
    }
}
