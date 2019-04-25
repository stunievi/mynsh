package com.beeasy.zed;

import java.text.ParseException;
import static com.beeasy.zed.DBService.config;


public class App {

    public static ZedService zedService = new ZedService();
    public static DeconstructService deconstructService = new DeconstructService();

    public static void main(String[] args) throws ParseException {
        DBService.init(false);
        //消息监听服务
        MQService.init();

        //routes
        HttpServerHandler.AddRoute(new Route(("^/zed"), (ctx, req) -> {
            return zedService.doNettyRequest(ctx, req);
        }));


        String workMode = config.getString("workmode");
        if(workMode.equals("search") || workMode.equals("all")){
            //注册查询接口
            QccService.register();
        }
        if(workMode.equals("deconstruct") || workMode.equals("all")){
            //注册解构接口
            DeconstructService.register();
        }


//        TestService.register();

        //起动netty
        NettyService.start();
    }

}
