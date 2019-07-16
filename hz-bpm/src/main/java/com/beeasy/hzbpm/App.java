package com.beeasy.hzbpm;

import cn.hutool.core.thread.ThreadUtil;
import com.github.llyb120.nami.core.Nami;

public class App {

    public static void main(String[] args) {

//        Param.AddRule((context, parameter) -> parameter.getName().startsWith("my_"), (context, parameter, defaultAction) -> {
//            if(parameter.getType().equals(TUser.class)){
////                context.request.query.put("id", 1);
//                context.query.put("id", 1);
//            }
//            return defaultAction.around(context, parameter, null);
//        });


        var conf = "./config.json";
        var generate = false;
        for(var i = 0; i < args.length; i++){
            if(args[i].equals("-c")){
                conf = args[++i];
            } else if(args[i].equals("-g")){
                generate = true;
            }
        }
        if(generate){

            String finalConf = conf;
            ThreadUtil.execAsync(() -> Nami.start());

        } else {
            Nami.start();
            System.out.println("启动。。。。");
        }
    }

}

