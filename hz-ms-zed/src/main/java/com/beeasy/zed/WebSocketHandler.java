//package com.beeasy.zed;
//
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.channel.group.DefaultChannelGroup;
//import io.netty.util.concurrent.GlobalEventExecutor;
//
//public class WebSocketHandler extends SimpleChannelInboundHandler {
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//
//    }
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        clients.add(ctx.channel());
//        System.out.println("fuck");
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fuck2");
//    }
//}
