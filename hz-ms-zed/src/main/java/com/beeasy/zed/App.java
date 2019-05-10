package com.beeasy.zed;

import cn.hutool.core.util.StrUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static com.beeasy.zed.DBService.config;


public class App {

    public static ZedService zedService = new ZedService();
    public static DeconstructService deconstructService = new DeconstructService();

    public static void main(String[] args) throws ParseException, InterruptedException, ExecutionException, FileNotFoundException {
        //test
        if(args.length > 1){
            if(StrUtil.equals(args[0],"test")){
                DBService.init(true);
                DBService.await();
                deconstructService = DeconstructService.register();
                deconstructService.autoCommit = false;
                deconstructService.onDeconstructRequest("1","2", new FileInputStream(args[1]));

                return;
            }
        }

        DBService.init(false);


        //routes
        HttpServerHandler.AddRoute(new Route(("^/zed"), (ctx, req) -> {
            return zedService.doNettyRequest(ctx, req);
        }));

//        TestService.register();

        String workMode = config.getString("workmode");
        if(workMode.equals("deconstruct") || workMode.equals("all")){
            //消息监听服务
            MQService.init();

            //注册解构接口
            DeconstructService.register();
        }
        if(workMode.equals("search") || workMode.equals("all")){
            //注册查询接口
            QccService.register();
            //起动netty
            NettyService.start();
        } else {
            new CountDownLatch(1).await();
        }

    }

}
