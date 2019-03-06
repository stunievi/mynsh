package com.beeasy.autoapi;

import lombok.Getter;

public class T {

    public String gougou(Test t){
        Test.aaa++;
        t.bbb++;
        return "xs" + Test.aaa + " | " + t.bbb + " | " + R.c + new R().getD();
    }

@Getter
    public static class R{
        public static int c = 3;
        int d = 222;
    }


}
