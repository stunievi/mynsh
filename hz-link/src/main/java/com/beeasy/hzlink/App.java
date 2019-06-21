package com.beeasy.hzlink;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.beeasy.hzlink.model.Link111;
import com.beeasy.hzlink.model.TUser;
import com.beeasy.hzlink.service.Link;
import com.github.llyb120.nami.core.Context;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.Param;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.llyb120.nami.core.DBService.sqlManager;
import static com.github.llyb120.nami.core.Json.o;

public class App {

    public static void main(String[] args) {

        Param.AddRule((context, parameter) -> parameter.getName().startsWith("my_"), (context, parameter, defaultAction) -> {
            if(parameter.getType().equals(TUser.class)){
                context.query.put("id", 1);
            }
            return defaultAction.around(context, parameter, null);
        });


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
            ThreadUtil.execAsync(() -> Nami.start(finalConf));
            while (sqlManager == null) {
                ThreadUtil.sleep(100);
            }

//            Link.do11_5("");
//            Link.do11_6();
//            if(true){
//                System.exit(0);
//            }
            var exec = Executors.newFixedThreadPool(16);
            List<Obj> list = sqlManager.select("accloan.cun_cus_com", Obj.class, o());
            for (Obj obj : list) {
                exec.submit(() -> {
                    var name = obj.getStr("cus_name");
                    if(StrUtil.isEmpty(name)){
                        return ;
                    }
//                    Link.do11_1(name);
                    Link.do11_2(name);
//                    Link.do11_3(name);
//                    Link.do11_4(name);
//                    Link.do11_5_1(name, true);

//                    Link.do11_6(name);
//
//                    Link.do12_2(name);
//                    Link.do12_3(name);
//                    Link.do12_4(name);
                });
            }
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);

        } else {
            Nami.start(conf);
        }
    }

}

