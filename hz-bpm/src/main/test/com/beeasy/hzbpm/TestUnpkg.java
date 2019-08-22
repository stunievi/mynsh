package com.beeasy.hzbpm;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestUnpkg {


    private ExecutorService executorService = Executors.newFixedThreadPool(16);
    @Test
    public void test() throws IOException, InterruptedException {
        String url = "https://unpkg.com/browse/mint-ui@2.2.13/";
        String target = "d:/mint-ui";

        File dir = new File(target);
        dir.mkdirs();
        executorService.submit(() -> getPage(dir, url));

        Thread.sleep(10000000000000L);
    }


    public void getPage(File dir, String url){
        String res = HttpUtil.get(url);
        org.jsoup.nodes.Document doc = Jsoup.parse(res);
        Elements as = doc.select("table tr a");
        for (Element a : as) {
            String href = a.attr("href");
            String name;
            if(href.endsWith("/")){
                name = href.substring(0, href.length() - 1);
                if(name.equals("..")){
                    continue;
                }
                if(name.equals(".")){
                    continue;
                }
                executorService.submit(() -> {
                    File target = new File(dir, name);
                    target.mkdirs();
                    System.out.println("fetching " + url + href);
                    getPage(target, url + href);
                });
            } else {
                executorService.submit(() -> {
                    File target = new File(dir, href);
                    System.out.println("downloading " + url + href);
                    try {
                        HttpUtil.download(url + href, new FileOutputStream(target), true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
