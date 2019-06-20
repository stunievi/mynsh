package com.beeasy.hzlink;

import org.junit.Test;

import java.math.BigDecimal;

public class NormalTest {

    @Test
    public void tttt(){
        var a = new BigDecimal("0.25");
        var b = a.multiply(new BigDecimal(100));

        var percent = convertToMoney("400.5万").divide(convertToMoney("4005万元人民币"), 2);

        var c = 1;

    }

    private static BigDecimal convertToMoney(String str) {
        var sstr = str.replaceAll("人民币", "");
        BigDecimal bg = null;
        if (sstr.contains("万")) {
            sstr = sstr.replaceAll("万港?美?元?|\\s+", "");
            bg = new BigDecimal(sstr);
            bg = bg.multiply(new BigDecimal(1000));
        } else {
            bg = new BigDecimal(sstr);
        }
        return bg;
    }
}
