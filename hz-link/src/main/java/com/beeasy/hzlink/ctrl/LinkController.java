package com.beeasy.hzlink.ctrl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzlink.model.Link111;
import com.beeasy.hzlink.model.RelatedPartyList;
import com.beeasy.hzlink.model.TGroupCusList;
import com.beeasy.hzlink.service.Link;
import com.beeasy.hzlink.util.U;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.R;
import org.beetl.sql.core.engine.PageQuery;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        for (String r : ruleArr){
            if(r.contains("12.")){
                sqlManager.lambdaQuery(RelatedPartyList.class)
                    .andEq(RelatedPartyList::getLink_rule, r)
                    .andEq(RelatedPartyList::getData_flag,"01")
                    .delete();
            }else if(r.contains("11.")){
                sqlManager.lambdaQuery(TGroupCusList.class)
                    .andEq(TGroupCusList::getLink_rule, r)
                    .andEq(TGroupCusList::getData_flag,"01")
                    .delete();
            }
        }
        var exec = Executors.newFixedThreadPool(10);
        List<Obj> cusList = sqlManager.select("accloan.cun_cus_com", Obj.class, o());

        if(ruleArr.contains("11.5")){
            System.out.println("11.5");
            Link.do11_5("");
        }
        if(ruleArr.contains("11.6")){
            System.out.println("11.6");
            Link.do11_6();
        }

        for (Obj obj : cusList) {
            exec.submit(() -> {
                try{
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
                }catch (Exception e){
                    System.out.println(e);
                    e.printStackTrace();
                }
            });
        }

        List<Obj> holderList = sqlManager.select("accloan.1201", Obj.class, o());
        for (Obj obj : holderList) {
            try{
                exec.submit(() -> {
                    var name = obj.getStr("cus_name");
                    var certCode = obj.getStr("cert_code");
                    String gdType = obj.getStr("gd_type");
                    if(StrUtil.isEmpty(name) || StrUtil.isBlank(certCode)){
                        return;
                    }
                    if(Arrays.asList("自然人","员工").contains(gdType)){
                        // 自然人股东
                        if(ruleArr.contains("12.5")){
                            System.out.println("12.5");
                            Link.do12_5(name, certCode);
                        }
                    }else if(Arrays.asList("法人").contains(gdType)){
                        // 企业股东
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
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            exec.shutdown();
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            for (Obj obj : cusList) {
                String compName = obj.getStr("cus_name");
                Date nowDate = new Date();
                BigDecimal p = new BigDecimal(0);
                List<Link111> comByName = sqlManager.lambdaQuery(Link111.class)
                        .andEq(Link111::getOrigin_name, compName)
                        .andEq(Link111::getLink_rule, "11.1")
                        .select();
                HashSet<String> arr = new HashSet<>();
                for(Link111 link : comByName){
                    if(StrUtil.isNotBlank(link.getLink_right())){
                        arr.add(link.getLink_right());
                    }
                }
                for(String personName : arr){
                    List<Link111> items = sqlManager.lambdaQuery(Link111.class)
                            .andEq(Link111::getOrigin_name, compName)
                            .andEq(Link111::getLink_rule, "11.1")
                            .andEq(Link111::getLink_right, personName)
                            .select();
                    HashMap<String, JSONObject> personCom = new HashMap<>();
                    for(Link111 item : items){
                        String linkComName = item.getLink_left();
                        if(null == personCom.get(linkComName)){
                            JSONObject data = new JSONObject();
                            HashSet<String> types = new HashSet<>();
                            types.add(item.getLink_type());
                            data.put("types", types);
                            if("自然人股东".equals(item.getLink_type())){
                                p = item.getStock_percent();
                                data.put("percent", p.toString());
                            }
                            personCom.put(linkComName, data);
                        }else{
                            HashSet<String> types = (HashSet<String>) personCom.get(linkComName).get("types");
                            if("自然人股东".equals(item.getLink_type())){
                                p = item.getStock_percent();
                                personCom.get(linkComName).put("percent", p.toString());
                            }
                            types.add(item.getLink_type());
                            personCom.get(linkComName).put("types", types);
                        }
                    }
                    System.out.println("1111" + JSONObject.toJSONString(personCom));
                    for(Map.Entry<String, JSONObject> entry : personCom.entrySet()){
                        String companyName = entry.getKey();
                        JSONObject linkData = entry.getValue();
                        boolean find = sqlManager.lambdaQuery(TGroupCusList.class)
                                .andEq(TGroupCusList::getCus_name, companyName)
                                .andEq(TGroupCusList::getLink_name, personName)
                                .andEq(TGroupCusList::getLink_rule, "11.1")
                                .count() == 0;
                        if(find){
                            TGroupCusList data = new TGroupCusList();
                            data.setAdd_time(nowDate);
                            data.setCus_name(companyName);
                            data.setCert_code("");
                            data.setLink_name(personName);
                            data.setLink_cert_code("");
                            data.setLink_rule("11.1");
                            data.setRemark_1(StrUtil.join("/", linkData.get("types")));
                            data.setRemark_2("");
                            data.setRemark_3(linkData.getString("percent"));
                            data.setData_flag("01");
                            sqlManager.insert(TGroupCusList.class, data, true);
                        }
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
