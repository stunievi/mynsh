package com.beeasy.hzlink.ctrl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzlink.service.Link;
import com.github.llyb120.nami.core.Nami;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.R;

import javax.xml.transform.Result;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.llyb120.nami.core.DBService.sqlManager;
import static com.github.llyb120.nami.core.Json.o;

public class LinkController {

    public R touchRule (String rule){
        if(null == rule || "".equals(rule) || rule.isEmpty()){
            return null;
        }
        List<String> ruleStrs = Arrays.asList(rule.split(","));
        while (sqlManager == null) {
            ThreadUtil.sleep(100);
        }
        var exec = Executors.newFixedThreadPool(10);
        List<Obj> list = sqlManager.select("accloan.cun_cus_com", Obj.class, o());

        for (String str :ruleStrs){
//        for(int i=0;i<jsonArray.size();i++){
//            String str = jsonArray.getString(i);
            for (Obj obj : list) {
                try{

                    exec.submit(() -> {
                        var name = obj.getStr("cus_name");
                        if(StrUtil.isEmpty(name)){
                            return ;
                        }
                        switch (str){
                            case "11":
                                System.out.println("11");
    //                            Link.do11_1(name);
    //                            Link.do11_2(name);
    //                            Link.do11_3(name);
    //                            Link.do11_4(name);
    //                            Link.do11_5_1(name, true);
    //                            Link.do11_5(name);
    //                            Link.do11_6(name);
                                break;
                            case "11.1":
                                System.out.println("11.1");
    //                            Link.do11_1(name);
                                break;
                            case "11.2":
                                System.out.println("11.2");
    //                            Link.do11_2(name);
                                break;
                            case "11.3":
                                System.out.println("11.3");
    //                            Link.do11_3(name);
                                break;
                            case "11.4":
                                System.out.println("11.4");
    //                            Link.do11_4(name);
                                break;
                            case "11.5":
                                System.out.println("11.5");
    //                            Link.do11_5(name);
                                break;
                            case "11.5.1":
                                System.out.println("11.5.1");
    //                            Link.do11_5_1(name, true);
                                break;
                            case "11.6":
                                System.out.println("11.6");
    //                            Link.do11_6(name);
                                break;
                            case "12":
                                System.out.println("12");
    //                            Link.do12_2(name);
    //                            Link.do12_3(name);
    //                            Link.do12_4(name);
                                break;
                            case "12.2":
                                System.out.println("12.2");
    //                            Link.do12_2(name);
                                break;
                            case "12.3":
                                System.out.println("12.3");
    //                            Link.do12_3(name);
                                break;
                            case "12.4":
                                System.out.println("12.4");
    //                            Link.do12_4(name);
                                break;
                        }

                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if("11".equals(str) || "12".equals(str)){
                break;
            }
        }
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
