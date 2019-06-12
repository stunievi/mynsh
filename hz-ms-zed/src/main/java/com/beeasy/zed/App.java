package com.beeasy.zed;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static com.beeasy.zed.Config.config;


public class App {
    public static ConcurrentMap<String, String> concurrentMapWordCounts = new ConcurrentHashMap<>();

    static {
        JSONObject jsonObject = JSONObject.parseObject(Utils.readFile().toString());
        JSONArray array = jsonObject.getJSONArray("Result");
        for (Object o : array) {
            JSONObject j = JSONObject.parseObject(o.toString());
            concurrentMapWordCounts.put(j.getString("Code"), j.getString("ProvinceName"));
        }
    }



//    public static ZedService zedService = new ZedService();
//    public static DeconstructService deconstructService = new DeconstructService();

    public static void main(String[] args) throws ParseException, InterruptedException, ExecutionException, FileNotFoundException {
        DBService.getInstance().initSync();
        if(Version.version.versionName >= 1.0){
            Version.update();
        }
        //test
        if (args.length > 1) {
            if (StrUtil.equals(args[0], "test")) {
                DeconstructService deconstructService = new DeconstructService();
                deconstructService.initSync();
                long stime = System.currentTimeMillis();
                for (int i = 1; i < args.length; i++) {
                    File file = new File(args[i]);
                    deconstructService.onDeconstructRequest("1", "2", new FileInputStream(file));
                }

//                args[1] = args[1].replaceAll("\\*", ".+?");
//                File file = new File(args[1]).getAbsoluteFile();
//                File dir = file.getParentFile();
//                String reg =
//                    "^" + args[1]
//                        .replaceAll("\\.", "\\\\.")
//                        .replaceAll("\\*", ".+") + "$";
//                Pattern pattern = Pattern.compile(reg);
//                System.out.println(reg);
//                System.out.println(JSON.toJSONString(dir.list()));
//                for (File listFile : dir.listFiles()) {
//                    if(listFile.isDirectory()){
//                        continue;
//                    }
//                    System.out.println(listFile.getName());
//                    Matcher m = pattern.matcher(listFile.getName());
//                    if(!m.find()){
//                        continue;
//                    }
//                    System.out.printf("working on %s \n", listFile.getName());
//                    deconstructService.onDeconstructRequest("1","2", new FileInputStream(listFile));
//                }

                System.out.printf("测试结束，共耗时%dms \n", System.currentTimeMillis() - stime);
                System.exit(-1);
            }
        }


        //routes
//        HttpServerHandler.AddRoute(new Route(("^/zed"), (ctx, req) -> {
//            return zedService.doNettyRequest(ctx, req);
//        }));

//        TestService.register();

        if (config.workmode.equals(Config.WorkMode.DECONSTRUCT) || config.workmode.equals(Config.WorkMode.ALL)) {
            //消息监听服务
            MQService.getInstance().initAsync();
            //注册解构接口
            new DeconstructService().initAsync();
        }
        if (config.workmode.equals(Config.WorkMode.SEARCH) || config.workmode.equals(Config.WorkMode.ALL)) {
            //注册查询接口
            new QccService().initAsync();
            //起动netty
            new NettyService().initSync();
        } else {
            new CountDownLatch(1).await();
        }

    }

}
