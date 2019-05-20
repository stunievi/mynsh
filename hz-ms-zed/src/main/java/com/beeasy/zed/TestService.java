package com.beeasy.zed;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.osgl.util.C;

import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

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
                        File file = Files.createTempFile("","").toFile();
                        RandomAccessFile raf = new RandomAccessFile(file, "rw");
                        ByteBuf buf = fileupload.content();
                        buf.readBytes(raf.getChannel(), 0, buf.readableBytes());
                        MQService.getInstance().sendMessage("queue", "qcc-deconstruct-request", new MQService.FileRequest(requestId.getString(), sourceRequest.getString(), file));
                        raf.close();
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
                MQService.getInstance().sendMessage("queue", "qcc-redeconstruct-request", JSON.toJSONString(C.newMap(
                    "requestId", requestId.getString(),
                    "progress", progress.getString()
                )));
                return null;
            }
        }));

        MQService.getInstance().listenMessage("queue", "qcc-deconstruct-response", m -> {
            if(m instanceof TextMessage){
                for (Channel client : HttpServerHandler.clients) {
                    client.writeAndFlush(new TextWebSocketFrame(((TextMessage) m).getText()));
                }
            }
        });

        MQService.getInstance().listenMessage("queue", "qcc-redeconstruct-response", m -> {
            if(m instanceof TextMessage){
                for (Channel client : HttpServerHandler.clients) {
                    client.writeAndFlush(new TextWebSocketFrame(((TextMessage) m).getText()));
                }
            }
        });
    }
}
