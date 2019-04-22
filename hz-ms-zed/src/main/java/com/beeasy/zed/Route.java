package com.beeasy.zed;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;

public class Route {
    public String regexp;
    public IHandler handler;

    public Route(String regexp, IHandler request){
        this.regexp = (regexp);
        this.handler = request;
    }

    public interface IHandler{
        Object run(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception;
    }
}
