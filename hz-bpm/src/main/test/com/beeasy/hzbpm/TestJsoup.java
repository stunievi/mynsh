package com.beeasy.hzbpm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class TestJsoup {


    @Test
    public void test(){
        Document doc = Jsoup.parse("<html><div>123</div></html>");
        String a = doc.selectFirst("div").html();
        int d = 2;
    }
}
