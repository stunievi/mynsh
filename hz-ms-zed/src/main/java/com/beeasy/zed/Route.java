package com.beeasy.zed;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.Objects;
import java.util.regex.Pattern;

public class Route {
    public String regexp;
    public IHandler handler;

    public Route(String regexp, IHandler request){
        this.regexp = (regexp);
        this.handler = request;
    }

    public interface IHandler{
        Object run(ChannelHandlerContext ctx, FullHttpRequest request);
    }
}
