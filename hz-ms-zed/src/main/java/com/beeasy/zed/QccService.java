package com.beeasy.zed;

import cn.hutool.json.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class QccService {

    public static void register(){
        QccService service = new QccService();
        registerRoute("^\\/CourtV4/SearchShiXin", service::SearchShiXin);
//        registerRoute("^\\/CourtV4/SearchShiXin", service::ttt);
        
//        HttpServerHandler.AddRoute(new Route(, service::SearchShiXin));
    }


    private Object SearchShiXin(ChannelHandlerContext channelHandlerContext, FullHttpRequest request, JSONObject object) {
        return null;
    }

//    public Object SearchShiXin(ChannelHandlerContext ctx, FullHttpRequest request){
//        JSONObject params = HttpServerHandler.decodeProxyQuery(request);
//
////        App.zedService.sqlManager
//        return null;
//    }

    public static void registerRoute(String url, Test o){
        
    }

    public void pageQuery(String url, Test o){
//        return
    }
    
    public interface Test{
        Object call(ChannelHandlerContext ctx, FullHttpRequest request, JSONObject object);
    }

//    HttpServerHandler.decodeProxyQuery(request)
}
