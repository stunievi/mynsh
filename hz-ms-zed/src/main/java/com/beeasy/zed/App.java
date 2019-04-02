package com.beeasy.zed;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;

import javax.net.ssl.HttpsURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


public class App {

    public static ZedService zedService = new ZedService();
    public static DeconstructService deconstructService = new DeconstructService();

    public static void main(String[] args) throws ParseException {
        zedService.initConfig();
        zedService.initDB(false);

        //routes
        HttpServerHandler.AddRoute(new Route(Pattern.compile("^\\/zed"), (ctx, req) -> {
            return zedService.doNettyRequest(ctx, req);
        }));



        //注册查询接口
        QccService.register(zedService);
        //注册解构接口
        DeconstructService.register(zedService);

        //起动netty
        zedService.initNetty();
    }
}
