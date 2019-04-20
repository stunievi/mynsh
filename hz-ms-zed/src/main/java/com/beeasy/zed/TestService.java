package com.beeasy.zed;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.osgl.util.C;

import javax.jms.TextMessage;
import java.io.IOException;

public class TestService {

    public static void register(){
        HttpServerHandler.AddRoute(
            new Route("/qcc/ttt", new Route.IHandler() {
                @Override
                public Object run(ChannelHandlerContext ctx, FullHttpRequest request) {
                    HttpPostRequestDecoder req = new HttpPostRequestDecoder(request);
                    MixedFileUpload fileupload = (MixedFileUpload) req.getBodyHttpData("file");
                    MixedAttribute requestId = (MixedAttribute) req.getBodyHttpData("requestId");
                    MixedAttribute sourceRequest = (MixedAttribute) req.getBodyHttpData("sourceRequest");
                    try {
                        MQService.sendTopicMessage("qcc-deconstruct-request", new MQService.FileRequest(requestId.getString(), sourceRequest.getString(), fileupload.getFile()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            })
        );

        HttpServerHandler.AddRoute(new Route(
            "/qcc/ttt2", new Route.IHandler() {
            @Override
            public Object run(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception{
                HttpPostRequestDecoder req = new HttpPostRequestDecoder(request);
                MixedAttribute requestId = (MixedAttribute) req.getBodyHttpData("requestId");
                MixedAttribute progress = (MixedAttribute) req.getBodyHttpData("progress");
                MQService.sendTopicMessage("qcc-redeconstruct-request", JSON.toJSONString(C.newMap(
                    "requestId", requestId.getString(),
                    "progress", progress.getString()
                )));
                return null;
            }
        }));

        MQService.listenMessage("qcc-deconstruct-response", m -> {
            if(m instanceof TextMessage){
                for (Channel client : HttpServerHandler.clients) {
                    client.writeAndFlush(new TextWebSocketFrame(((TextMessage) m).getText()));
                }
            }
        });

        MQService.listenMessage("qcc-redeconstruct-response", m -> {
            if(m instanceof TextMessage){
                for (Channel client : HttpServerHandler.clients) {
                    client.writeAndFlush(new TextWebSocketFrame(((TextMessage) m).getText()));
                }
            }
        });
    }
}
