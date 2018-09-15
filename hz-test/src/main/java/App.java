import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {


    public static void main(String[] args) throws Exception{

        //开启文件变动检测服务
//        new Thread(() -> {
        DevFileWatcher.start("/Users/bin/work/hzcp/hz-test/target");
//        }).start();
        DevClassLoader.scanClasses("com.test");
        int c = 5 + 3 + 2 + 4 + 5 + 4 + 2;
        String str = "riledagou";
        str.equals("fff");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {

                            // HTTP请求消息解码器
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());

                            /*
                             * HttpObjectAggregator解码器
                             * 将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
                             */
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));

                            //HTTP响应编码器,对HTTP响应进行编码
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());

                            //ChunkedWriteHandler的主要作用是支持异步发送大的码流,但不占用过多的内存,防止JAVA内存溢出
                            ch.pipeline().addLast("http-chunked",
                                    new ChunkedWriteHandler());

                            ch.pipeline().addLast("httpServerHandler", new HttpServerHandler());
                        }
                    });
            ChannelFuture future = b.bind("localhost", 8085).sync();
            System.out.println("HTTP Server startup.");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }



    }

}
