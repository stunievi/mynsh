package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.nio.charset.Charset;

import static com.beeasy.zed.DBService.config;

public class NettyService {

    public static void start(){
        System.out.println(String.format("port is on %d", config.getInteger("port")));
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)

//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline pipeline = ch.pipeline();
//
//                            pipeline.addLast(new HttpServerCodec());
//                            pipeline.addLast( new HttpObjectAggregator(1024 * 1024));
//                            pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
//                            pipeline.addLast(new HttpServerHandler());
//                        }
//                    })
                .childHandler(new ChannelInitializer<SocketChannel>(){


                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //将请求和应答消息编码或解码为HTTP消息
                        pipeline.addLast(new HttpServerCodec());
                        //将HTTP消息的多个部分组合成一条完整的HTTP消息
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpStaticHandleAdapter());
                        pipeline.addLast(new HttpServerHandler());

                    }
                });
            ThreadUtil.execAsync(() -> {
                System.out.println("boot success");
            });
            ChannelFuture f = b.bind(config.getInteger("port")).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
