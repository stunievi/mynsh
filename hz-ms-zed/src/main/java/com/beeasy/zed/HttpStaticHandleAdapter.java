package com.beeasy.zed;

import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioStream;

import java.io.InputStream;
import java.nio.channels.Channels;

public class HttpStaticHandleAdapter extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
// 获取URI
        String uri = request.getUri();
        if(!uri.startsWith("/doc")){
            ctx.fireChannelRead(request);
            return;
        }

        // 设置不支持favicon.ico文件
        if ("favicon.ico".equals(uri)) {
            return;
        }
        // 根据路径地址构建文件
//        String path = location + uri;
//        File html = new File(path);

        // 状态为1xx的话，继续请求
        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);

        String path = URLUtil.getPath(request.uri());

        // 设置文件格式内容
        if (path.endsWith(".html")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        }else if(path.endsWith(".js")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/x-javascript");
        }else if(path.endsWith(".css")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/css; charset=UTF-8");
        } else if(path.endsWith(".map")){
            return;
        }

//        File html = new File(this.getClass().getResource(path).getFile());

        InputStream is = this.getClass().getResourceAsStream(path);

        // 当文件不存在的时候，将资源指向NOT_FOUND
//        if (!html.exists()) {
//            response.setStatus(HttpResponseStatus.NOT_FOUND);
////            html = NOT_FOUND;
//        }

//        RandomAccessFile file = new RandomAccessFile(html, "r");


        // 文件没有发现设置状态为404
//        if (html == NOT_FOUND) {
//            response.setStatus(HttpResponseStatus.NOT_FOUND);
//        }

        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
//            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED);
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);

//        if (ctx.pipeline().get(SslHandler.class) == null) {
//            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
//        } else {
        ctx.write(new ChunkedNioStream(Channels.newChannel(is)));
//        }
        // 写入文件尾部
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        is.close();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
}
