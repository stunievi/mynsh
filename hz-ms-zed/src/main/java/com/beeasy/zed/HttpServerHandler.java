package com.beeasy.zed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.osgl.util.S;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;

class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private static List<Route> RouteList = new ArrayList<>();
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");
    public static Throwable LastException = null;
    private static Map<String, Pattern> urlRegexs = new HashMap<>();

    public static void AddRoute(Route... routes) {
        RouteList.addAll(Arrays.asList(routes));
    }

    public void send404(ChannelHandlerContext ctx, Object msg) {
        ctx.writeAndFlush(get404());
    }

    public FullHttpResponse get404() {
        JSONObject object = new JSONObject();
        object.put("Status", "500");
        object.put("Message", "错误请求");
        String json = object.toJSONString();
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer(bytes));
        response.headers().set("Content-Type", "application/json; charset=utf-8");
        response.headers().set("Content-Length", bytes.length);
        return response;
    }

    public void write(ChannelHandlerContext ctx, FullHttpResponse response, boolean keepAlive) {
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        if (msg instanceof FullHttpRequest) {
            // 请求，解码器将请求转换成HttpRequest对象
            FullHttpRequest request = (FullHttpRequest) msg;

            boolean keepAlive = HttpUtil.isKeepAlive(request);

            for (Route route : RouteList) {
                if (!matches(request.uri(), route.regexp)) {
                    continue;
                }

                Object object = route.handler.run(ctx, request);
                FullHttpResponse response = null;
                if (object == null) {
                    response = get404();
                } else {
                    byte[] responseBytes;
                    if (object instanceof String) {
                        responseBytes = ((String) object).getBytes(StandardCharsets.UTF_8);
                    } else {
                        responseBytes = JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat).getBytes(StandardCharsets.UTF_8);
                    }
                    int contentLength = responseBytes.length;
                    // 构造FullHttpResponse对象，FullHttpResponse包含message body
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
                    response.headers().set("Content-Type", "application/json; charset=utf-8");
                    response.headers().set("Content-Length", Integer.toString(contentLength));
                }

                write(ctx, response, keepAlive);
                return;
            }
            throw new RuntimeException();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LastException = cause;
        cause.printStackTrace();
        ctx.writeAndFlush(get404());
        ctx.close();
    }


    public static JSONObject decodeProxyQuery(FullHttpRequest fullHttpRequest) {
        JSONObject params = new JSONObject();
        String proxy = fullHttpRequest.headers().getAsString("Proxy-Url");
        if(S.empty(proxy)){
            return decodeQuery(fullHttpRequest);
        }
        QueryStringDecoder decoder = new QueryStringDecoder(proxy);
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        return params;
    }

    public static JSONObject decodeQuery(FullHttpRequest request) {
        JSONObject params = new JSONObject();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        return params;
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
}
