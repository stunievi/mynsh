package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.AsciiString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beeasy.zed.Config.config;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.NOT_FOUND;

//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;

class HttpServerHandler extends ChannelInboundHandlerAdapter {

    public static List<Route> RouteList = new ArrayList<>();
//    private static ThreadLocal<JSONObject> querys = new ThreadLocal<JSONObject>(){
//        @Override
//        protected JSONObject initialValue() {
//            return new JSONObject();
//        }
//    };

    public static Vector<Channel> clients = new Vector<>();
    private WebSocketServerHandshaker handshaker = null;

    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
    public static Throwable LastException = null;
    private static Map<String, Pattern> urlRegexs = new ConcurrentHashMap<>();

    public static void AddRoute(Route... routes) {
        RouteList.addAll(Arrays.asList(routes));
    }

    public void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, Object msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

//    public FullHttpResponse get404() {
//        JSONObject object = new JSONObject();
//        object.put("Status", "500");
//        object.put("Message", "错误请求");
//        String json = object.toJSONString();
//        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer(bytes));
//        response.headers().set("Content-Type", "application/json; charset=utf-8");
//        response.headers().set("Content-Length", bytes.length);
//        return response;
//    }

    public void write(ChannelHandlerContext ctx, FullHttpResponse response, boolean keepAlive) {
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            // 请求，解码器将请求转换成HttpRequest对象
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        else if(msg instanceof WebSocketFrame){
            handleWebSocketRequest(ctx, (WebSocketFrame) msg);
        }
    }

    public void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame){
        if(frame instanceof CloseWebSocketFrame){
            clients.remove(ctx.channel());
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
        } else if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if(frame instanceof TextWebSocketFrame){
            ctx.channel().writeAndFlush(new TextWebSocketFrame(((TextWebSocketFrame) frame).text()));
        }
    }


    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception{
        String uri = request.uri();
        if(uri.equalsIgnoreCase("/ws")){
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                String.format("http://0.0.0.0:%d/ws/", config.port), null, false);
            handshaker = wsFactory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), request);
                clients.add(ctx.channel());
            }
            return;
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        if (keepAlive) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
        }

        for (Route route : RouteList) {
            if (!matches(uri, route.regexp)) {
                continue;
            }

            Object object = route.handler.run(ctx, request);
            if (object == null) {
                sendError(ctx, HttpResponseStatus.valueOf(500), "");
                return;

            } else {
                byte[] responseBytes;
                if(object instanceof File){
                    proxyFile(ctx, request, (File) object);
                    return;
//                    try(
//                        FileInputStream fis = new FileInputStream((File) object);
//                        ){
//                        responseBytes = IoUtil.readBytes(fis);
//                        ByteBuf buf = Unpooled.copiedBuffer(responseBytes);
////                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
//                        response.headers().set("Content-Type", "text/plain; charset=utf-8");
//                        response.headers().set("Content-Length", buf.readableBytes());
//                        response.content().writeBytes(buf);
//                        buf.release();
//                    }

                } else {
                    if (object instanceof String) {
                        responseBytes = ((String) object).getBytes(StandardCharsets.UTF_8);
                    } else {
                        responseBytes = JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue).getBytes(StandardCharsets.UTF_8);
                    }
                    // 构造FullHttpResponse对象，FullHttpResponse包含message body
                    ByteBuf buf = Unpooled.wrappedBuffer(responseBytes);
                    response.headers().set("Content-Type", "application/json; charset=utf-8");
                    response.headers().set("Content-Length", buf.readableBytes());
                    response.content().writeBytes(buf);
                    buf.release();
                }

                ChannelFuture future = ctx.writeAndFlush(response);
                if(!keepAlive){
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            }

            return;
        }

        sendError(ctx,HttpResponseStatus.NOT_FOUND, "not found");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LastException = cause;
        cause.printStackTrace();
        sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "");
    }


//    public static JSONObject decodeProxyQuery(FullHttpRequest fullHttpRequest) {
//        JSONObject params = new JSONObject();
//        String proxy = fullHttpRequest.headers().getAsString("Proxy-Url");
//        if(S.empty(proxy)){
//            return decodeQuery(fullHttpRequest);
//        }
//        QueryStringDecoder decoder = new QueryStringDecoder(proxy);
//        Map<String, List<String>> paramList = decoder.parameters();
//        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
//            params.put(entry.getKey(), entry.getValue().get(0));
//        }
//        return params;
//    }

    public static JSONObject decodeQuery(FullHttpRequest request) {
        JSONObject query = new JSONObject();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            query.put(entry.getKey(), entry.getValue().get(0));
        }
        query.put("$decoded", true);
        return query;
    }

//    public static Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {
//
//        Map<String, Object> params = new HashMap<String, Object>();
//
//        if (fullHttpRequest.method() == HttpMethod.GET) {
//            // 处理get请求
//            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
//            Map<String, List<String>> paramList = decoder.parameters();
//            for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
//                params.put(entry.getKey(), entry.getValue().get(0));
//            }
//            return params;
//        } else {
//            return null;
//        }
//
//    }

    public static boolean matches(String url, String pattern) {
//        pattern = pattern ;
        Pattern p = urlRegexs.get(pattern);
        if (p == null) {
            p = Pattern.compile(pattern);
            urlRegexs.put(pattern, p);
        }
        Matcher m = p.matcher(url);
        if (m.find()) {
            if (url.length() == m.end()) {
                return true;
            }
            char c = url.charAt(m.end());
            if (c == '?' || c == '#' || c == '/' || c == '.') {
                return true;
            }
        }
        return false;

    }


    public void proxyFile(ChannelHandlerContext ctx, FullHttpRequest request, File file){
        try(
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            ){
            long fileLength = raf.length();
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            HttpUtil.setContentLength(response, fileLength);
            response.headers().set("Content-Type", "application/json; charset=utf-8");
//        setContentTypeHeader(response, file);
//        setDateAndCacheHeaders(response, file);
            if (HttpUtil.isKeepAlive(request)) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // Write the initial line and the header.
            ctx.write(response);

            // Write the content.
            ChannelFuture sendFileFuture;
            ChannelFuture lastContentFuture;
            if (ctx.pipeline().get(SslHandler.class) == null) {
                sendFileFuture =
                    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                // Write the end marker.
                lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                sendFileFuture =
                    ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                        ctx.newProgressivePromise());
                // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                lastContentFuture = sendFileFuture;
            }

            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                    if (total < 0) { // total unknown
                        System.err.println(future.channel() + " Transfer progress: " + progress);
                    } else {
                        System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) {
                    System.err.println(future.channel() + " Transfer complete.");
                }
            });

            // Decide whether to close the connection or not.
            if (!HttpUtil.isKeepAlive(request)) {
                // Close the connection when the whole content is written out.
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }

//            raf.close();
        }
        catch (IOException e){
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "");
        }
    }
}
