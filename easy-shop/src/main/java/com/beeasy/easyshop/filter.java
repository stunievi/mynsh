package com.beeasy.easyshop;


import com.beeasy.easyshop.core.AopInvoke;

public class filter{

    public Object around(AopInvoke invoke) throws Exception {
        System.out.println("rilegou before");
        Object ret = invoke.call();
        System.out.println("rilegou after");
        return ret;
    }
    
}
