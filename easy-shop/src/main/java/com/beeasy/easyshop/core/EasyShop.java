package com.beeasy.easyshop.core;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.nio.charset.Charset;

import static com.beeasy.easyshop.core.Config.config;
public class EasyShop {

    public static void start() {

//        new FileWatcher().start();
        DBService.start();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        long stime = System.currentTimeMillis();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {


                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //将请求和应答消息编码或解码为HTTP消息
                        pipeline.addLast(new HttpServerCodec());
                        //将HTTP消息的多个部分组合成一条完整的HTTP消息
                        pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
//                        pipeline.addLast(new HttpStaticHandleAdapter());
                        pipeline.addLast(new HttpServerHandler());

                    }
                });

            ChannelFuture f = b.bind(config.port).sync();
            System.out.println(String.format("port is on %d", config.port));
            System.out.printf("boot success takes %d ms\n", System.currentTimeMillis() - stime);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void registerController(String prefix, String packageName){
        HttpServerHandler.ctrls.put(prefix, packageName);
    }
}
