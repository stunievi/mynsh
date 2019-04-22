package com.beeasy.easyshop.core;

import static com.beeasy.easyshop.core.Config.config;
import static cn.hutool.core.util.StrUtil.*;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
//    private static List<Route> RouteList = new ArrayList<>();

    public static Map<String, String> ctrls = new HashMap<>();
    public static Vector<Channel> clients = new Vector<>();
    private WebSocketServerHandshaker handshaker = null;

    private static final String CONNECTION = ("Connection");
    private static final String KEEP_ALIVE = ("keep-alive");
    public static Throwable LastException = null;
    private static Map<String, Pattern> urlRegexs = new HashMap<>();

//    public static void AddRoute(Route... routes) {
//        RouteList.addAll(Arrays.asList(routes));
//    }

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            // 请求，解码器将请求转换成HttpRequest对象
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketRequest(ctx, (WebSocketFrame) msg);
        }
    }

    public void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            clients.remove(ctx.channel());
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(((TextWebSocketFrame) frame).text()));
        }
    }


    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.equalsIgnoreCase("/ws")) {
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
        if(uri.equals("/favicon.ico")){
            ctx.close();
            return;
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        String[] arr = URLUtil.getPath(uri).substring(1).split("\\/");
        route:
        {
            if (arr.length < 2) {
                break route;
            }
            String packageName = ctrls.get(arr[0]);
            if (packageName == null) {
                break route;
            }
            Class clz = (Class) (new MyClassLoadader()).loadClass(packageName + "." + arr[1]);
            Object result = null;
            for (Method method : clz.getDeclaredMethods()) {
                if (method.getName().equalsIgnoreCase(arr[2])) {
                    Object[] args = autoWiredParams(request, clz, method);
                    result = method.invoke(clz.newInstance(), args);
                    break;
                }
            }
            FullHttpResponse response = null;
            if (result == null) {
                response = get404();
            } else {
                byte[] responseBytes;
                if (result instanceof String) {
                    responseBytes = ((String) result).getBytes(StandardCharsets.UTF_8);
                } else {
                    responseBytes = JSON.toJSONString(result, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat).getBytes(StandardCharsets.UTF_8);
                }
                int contentLength = responseBytes.length;
                // 构造FullHttpResponse对象，FullHttpResponse包含message body
                ByteBuf buf = Unpooled.wrappedBuffer(responseBytes);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                response.headers().set("Content-Type", "application/json; charset=utf-8");
                response.headers().set("Content-Length", buf.readableBytes());
            }

            write(ctx, response, keepAlive);
            return;
        }

        send404(ctx, null);
}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LastException = cause;
        cause.printStackTrace();
        ctx.writeAndFlush(get404());
        ctx.close();
    }



    public static JSONObject decodeQuery(FullHttpRequest request) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri(), StandardCharsets.UTF_8);
        JSONObject object = new JSONObject();
        decoder.parameters().forEach((k,v) -> object.put(k,v.get(0)));
        return object;
    }

    public static JSONObject decodePost(FullHttpRequest request) {
        JSONObject ret = new JSONObject();
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.offer(request);
        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData parm : parmList) {
            Attribute data = null;
            if(parm instanceof Attribute){
                data = (Attribute) parm;
            } else if(parm instanceof FileUpload){
                ret.put(parm.getName(), new MultipartFile((FileUpload) parm));
                continue;
            }
            try {
                ret.put(data.getName(), data.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static JSON decodeJson(FullHttpRequest request){
        ByteBuf buf = request.content();
        String json = buf.toString(StandardCharsets.UTF_8);
        return (JSON) JSON.parse(json);
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

    private Object[] autoWiredParams(FullHttpRequest request, Class clz, Method method){
        Parameter[] parameters = method.getParameters();
        Object[] ret = new Object[parameters.length];
        int idex = -1;
        int i = 0;
        JSONObject query = decodeQuery(request);
        JSON body = null;
        JSONObject params = new JSONObject();
        if(HttpMethod.POST == request.method()){
            String contentType = request.headers().getAsString("Content-Type");
            if(contentType.contains("application/json")){
                body = decodeJson(request);
            } else {
                body = decodePost(request);
            }
        }
        //全放到这里
        params.putAll(query);
        if(body instanceof JSONObject){
            params.putAll((JSONObject) body);
        }

        for (Parameter parameter : parameters) {
            //特殊字段
            String name = parameter.getName();
            Class<?> type = parameter.getType();
//            name = names.get(i);
            ret[i] = null;
            switch (name){
                case "query":
                    ret[i] = query.toJavaObject(type);

                case "body":
                    if (body != null) {
                        ret[i] = body.toJavaObject(type);
                    }
                    break;

                case "params":
                    ret[i] = params.toJavaObject(type);
                    break;

                default:
                    //必然为JSONOBJECT
                    idex = type.getTypeName().indexOf("[]");
                    //是数组的情况
                    if(idex > -1){
                        String source = query.getString(name);
                        if(isNotEmpty(source)){
                            if(source.startsWith("[") && source.endsWith("]")){
                                JSONArray array = query.getJSONArray(name);
                                ret[i] = array.toJavaObject(type);
                            } else if(source.contains(",")){
                                String[] split = source.split(",");
                                JSONArray array = new JSONArray(Arrays.asList(split));
                                try{
                                    ret[i] = array.toJavaObject(type);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if(type == MultipartFile.class) {
                        ret[i] = params.get(name);
                    } else {
                        ret[i] = getParamValue(query, name, type);
                    }
                    break;
            }
            i++;
        }
        return ret;
    }

    private Object getParamValue(JSONObject query, String name, Class type){
        try{
            return query.getObject(name, type);
        }catch (Exception e){
            return null;
        }
    }
}
