package com.beeasy.hzlink.ctrl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzlink.model.TGroupCusList;
import com.beeasy.hzlink.service.Link;
import com.beeasy.hzlink.util.U;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.R;
import org.beetl.sql.core.engine.PageQuery;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.llyb120.nami.core.DBService.sqlManager;
import static com.github.llyb120.nami.core.Json.o;

public class LinkController {

    // 集团客户
    public R list(Obj query){
        return R.ok(
                U.beetlPageQuery("link.search_group_cus_list", JSONObject.class, query)
        );
    }

    // 股东关联
    public R list2(Obj query){
        return R.ok(
                U.beetlPageQuery("link.search_holder_link", JSONObject.class, query)
        );
    }

    public R touchRule (String rule){
        if(null == rule || "".equals(rule) || rule.isEmpty()){
            return null;
        }
        List<String> ruleArr = Arrays.asList(rule.split(","));
        while (sqlManager == null) {
            ThreadUtil.sleep(100);
        }
        var exec = Executors.newFixedThreadPool(10);
        List<Obj> cusList = sqlManager.select("accloan.cun_cus_com", Obj.class, o());

        for (Obj obj : cusList) {
            try{
                exec.submit(() -> {
                    var name = obj.getStr("cus_name");
                    if(StrUtil.isEmpty(name)){
                        return ;
                    }
                    if(ruleArr.contains("11.1")){
                        System.out.println("11.1");
                        Link.do11_1(name);
                    }
                    if(ruleArr.contains("11.2")){
                        System.out.println("11.2");
                        Link.do11_2(name);
                    }
                    if(ruleArr.contains("11.3")){
                        System.out.println("11.3");
                        Link.do11_3(name);
                    }
                    if(ruleArr.contains("11.4")){
                        System.out.println("11.4");
                        Link.do11_4(name);
                    }
                    if(ruleArr.contains("11.5")){
                        System.out.println("11.5");
                        Link.do11_5(name);
                    }
                    if(ruleArr.contains("11.5.1")){
                        System.out.println("11.5.1");
                        Link.do11_5_1(name, true);
                    }
                    if(ruleArr.contains("11.6")){
                        System.out.println("11.6");
                        Link.do11_6();
                    }

                    if(ruleArr.contains("12.2")){
                        System.out.println("12.2");
                        Link.do12_2(name);
                    }
                    if(ruleArr.contains("12.3")){
                        System.out.println("12.3");
                        Link.do12_3(name);
                    }
                    if(ruleArr.contains("12.4")){
                        System.out.println("12.4");
                        Link.do12_4(name);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        List<Obj> holderList = sqlManager.select("accloan.自然人股东", Obj.class, o());
        for (Obj obj : holderList) {
            try{
                exec.submit(() -> {
                    var name = obj.getStr("cus_name");
                    var certCode = obj.getStr("cert_code");
                    if(StrUtil.isEmpty(name) || StrUtil.isBlank(certCode)){
                        return;
                    }
                    if(ruleArr.contains("12.5")){
                        System.out.println("12.5");
                        Link.do12_5(name, certCode);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            exec.shutdown();
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
