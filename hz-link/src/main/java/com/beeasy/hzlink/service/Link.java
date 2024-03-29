package com.beeasy.hzlink.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzlink.model.*;
import com.github.llyb120.nami.core.Arr;
import com.github.llyb120.nami.core.Json;
import com.github.llyb120.nami.core.Obj;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.SnowflakeIDAutoGen;
import org.beetl.sql.ext.SnowflakeIDWorker;
import org.jxls.common.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.llyb120.nami.core.DBService.sqlManager;
import static com.github.llyb120.nami.core.Json.*;
import static com.github.llyb120.nami.core.Config.config;

public class Link {

    public static void do11_1(String compName) {
        var ret = a();
        //企业基本信息
//        var detail = sqlManager.lambdaQuery(QccDetails.class)
//            .andEq(QccDetails::getInner_company_name, compName)
//            .single();
//        if (detail == null) {
//            return;
//        }
        var arr = new LinkedHashSet<String>();
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return;
        }
        arr.add(detail.getStr("OperName"));

        //主要人员
        var ps = detail.getArr("Employees");
        for (Obj obj : ps.toObjList()) {
            if(obj.getStr("Job","").contains("董事")){
                arr.add(obj.getStr("Name"));
            }
        }

//        var ps = sqlManager.lambdaQuery(QccDetailsEmployees.class)
//            .andEq(QccDetailsEmployees::getInner_company_name, compName)
//            .andLike(QccDetailsEmployees::getJob, "%董事%")
//            .select(QccDetailsEmployees::getName);

        //股东
        var dps = detail.getArr("Partners");
        for (Obj obj : dps.toObjList()) {
            if(!obj.getStr("StockType", "").equals("自然人股东")){
                continue;
            }
            //算不出来的一概忽略
            try {
                var stockPercent = obj.getStr("StockPercent").replaceAll("%", "");
                if(StrUtil.isNotBlank(stockPercent)){
                    var p = Float.parseFloat(stockPercent);
                    if (p >= 25) {
                        arr.add(obj.getStr("StockName"));
                    }
                }else{
                    System.out.println(compName + obj.getStr("StockName") + "|缺失股权比例");
                }
            } catch (Exception e) {
                System.out.println(JSONObject.toJSON(obj));
                e.printStackTrace();
                System.exit(1);
            }
        }

//        var dps = sqlManager.lambdaQuery(QccDetailsPartners.class)
//            .andEq(QccDetailsPartners::getInner_company_name, compName)
//            .andEq(QccDetailsPartners::getStock_type, "自然人股东")
//            .select();
//
//        //加入法人
////        arr.add(detail.getOper_name());
//        //加入董事
////        for (QccDetailsEmployees p : ps) {
////            arr.add(p.getName());
////        }
//        //加入持股25%以上的自然人股东
//        for (QccDetailsPartners dp : dps) {
//            //算不出来的一概忽略
//            try {
//                var p = Float.parseFloat(dp.getStock_percent().replaceAll("%", ""));
//                if (p >= 25) {
//                    arr.add(dp.getStock_name());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //移除空的项目
        arr.removeIf(p -> StrUtil.isEmptyIfStr(p));

        if (arr.size() == 0) {
            return;
        }

        //检查董监高
        for (String s : arr) {
            //如果这个人已经在股东里出现，切持股比例满足，则直接加入
            for (Obj obj : dps.toObjList()) {
                if(!obj.getStr("StockType", "").equals("自然人股东")){
                    continue;
                }
                if(!obj.getStr("StockName","").equals(s)){
                    continue;
                }
                //算不出来的一概忽略
                try {
                    var p = Float.parseFloat(obj.getStr("StockPercent").replaceAll("%", ""));
                    if (p >= 25) {
                        ret.add(a(compName, s, "自然人股东", new BigDecimal(p)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            var cia = getCia(compName, s);
            if (cia != null) {
                for (Obj ciaCompanyLegals : cia.getArr("CIACompanyLegals").toObjList()) {
                    ret.add(a(ciaCompanyLegals.getStr("Name"), s, "法人"));
                }
                for (Obj ciaForeignOffices : cia.getArr("CIAForeignOffices").toObjList()) {
                    if(ciaForeignOffices.getStr("Position", "").contains("董事")){
                        ret.add(a(ciaForeignOffices.getStr("Name"), s, "董事"));
                    }
                }
                for (Obj ciaForeignInvestments : cia.getArr("CIAForeignInvestments").toObjList()) {
                    //算不出来的一概忽略
                    try {
                        var val = ciaForeignInvestments.getStr("SubConAmt");
                        if (val!=null && val.contains(",")) {
                            var items = StrUtil.splitTrim(val, ",");
                            var i = new BigDecimal(0);
                            for (String item : items) {
                                i.add(new BigDecimal(item));
                            }
                            val = i.toString();
                        }
                        var regCap = ciaForeignInvestments.getStr("RegCap");
                        if(StrUtil.isNotBlank(val) && StrUtil.isNotBlank(regCap)){
                            var percent = convertToMoney(val + "万").divide(convertToMoney(regCap), 2, RoundingMode.HALF_UP);
                            if (percent.floatValue() >= 0.25) {
                                ret.add(a(ciaForeignInvestments.getStr("Name"), s, "自然人股东", percent.multiply(new BigDecimal(100))));
                            }
                        }else{
                            System.out.println(compName + ciaForeignInvestments.getStr("Name") + "|缺失股权比例计算数据");
                        }
                    } catch (Exception e) {
                        System.out.println(JSONObject.toJSON(ciaForeignInvestments));
                        System.out.println(convertToMoney(ciaForeignInvestments.getStr("RegCap")));
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
        //担任法人
//        var dfaren = sqlManager.lambdaQuery(QccCiaCompanyLegals.class)
//            .andEq(QccCiaCompanyLegals::getInner_company_name, compName)
//            .andIn(QccCiaCompanyLegals::getPer_name, arr)
//            .select(QccCiaCompanyLegals::getName, QccCiaCompanyLegals::getPer_name);
//        for (QccCiaCompanyLegals qccCiaCompanyLegals : dfaren) {
//            ret.add(a(qccCiaCompanyLegals.getName(), qccCiaCompanyLegals.getPer_name(), "法人"));
//        }
//        //担任董事
//        var dongshi = sqlManager.lambdaQuery(QccCiaForeignOffices.class)
//            .andEq(QccCiaForeignOffices::getInner_company_name, compName)
//            .andIn(QccCiaForeignOffices::getPer_name, arr)
//            .andLike(QccCiaForeignOffices::getPosition, "%董事%")
//            .select(QccCiaForeignOffices::getName, QccCiaForeignOffices::getPer_name);
//        for (QccCiaForeignOffices qccCiaForeignOffices : dongshi) {
//            ret.add(a(qccCiaForeignOffices.getName(), qccCiaForeignOffices.getPer_name(), "董事"));
//        }
//        //25%以上的自然人股东
//        var gudong = sqlManager.lambdaQuery(QccCiaForeignInvestments.class)
//            .andEq(QccCiaForeignInvestments::getInner_company_name, compName)
//            .andIn(QccCiaForeignInvestments::getPer_name, arr)
//            .select();
//        for (QccCiaForeignInvestments qccCiaForeignInvestments : gudong) {
//            //有的没数据，忽略
//            if (StrUtil.isNotBlank(qccCiaForeignInvestments.getReg_cap()) && StrUtil.isNotBlank(qccCiaForeignInvestments.getSub_con_amt())) {
//                //算不出来的一概忽略
//                try {
//                    var val = qccCiaForeignInvestments.getSub_con_amt();
//                    if (val.contains(",")) {
//                        var items = StrUtil.splitTrim(val, ",");
//                        var i = new BigDecimal(0);
//                        for (String item : items) {
//                            i.add(new BigDecimal(item));
//                        }
//                        val = i.toString();
//                    }
//                    var percent = convertToMoney(val + "万").divide(convertToMoney(qccCiaForeignInvestments.getReg_cap()), 2);
//                    if (percent.floatValue() >= 0.25) {
//                        ret.add(a(qccCiaForeignInvestments.getName(), qccCiaForeignInvestments.getPer_name(), "自然人股东", percent.multiply(new BigDecimal(100))));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        //检查是否在我行有贷款
        if (ret.size() == 0) {
            return;
        }
        var allCompName = ret.toArrList().stream()
            .map(o -> o.getString(0))
            .distinct()
            .collect(Collectors.toList());
        var allows = hasLoan(allCompName);
        ret.removeIf(a -> {
            return allows.stream()
                .allMatch(
                    e -> !((Arr) a).getString(0).equals(e)
                );
        });

        //整理数据
        sqlManager.lambdaQuery(Link111.class)
            .andEq(Link111::getOrigin_name, compName)
            .andEq(Link111::getLink_rule, "11.1")
            .delete();
        var cache = o();
        var batch = a();
        for (Arr objects : ret.toArrList()) {
            if(compName.equals(objects.getString(0))){
                continue;
            }
            var key = objects.getString(0) + objects.getString(1) + objects.getString(2);
            if (cache.containsKey(key)) {
                continue;
            }
            Link111 link111 = new Link111();
            link111.setOrigin_name(compName);
            link111.setId(IdUtil.objectId());
            link111.setLink_left(objects.getString(0));
            link111.setLink_right(objects.getString(1));
            link111.setLink_type(objects.getString(2));
            link111.setLink_rule("11.1");
            link111.setIs_company(1);
            if(objects.size() > 3){
                link111.setStock_percent(objects.getBigDecimal(3));
            }
            cache.put(key, true);
            batch.add(link111);
        }
//        sqlManager.insert(batch.get(0), true);
        sqlManager.insertBatch(Link111.class, batch);
    }

    public static void do11_2(String compNames) {
        var obj = getCompanyDetail(compNames);
        var operName = "";
        if(null != obj){
            operName = obj.getStr("OperName");
        }
        if(!"".equals(operName)){
            CusCom cusCom = sqlManager.lambdaQuery(CusCom.class).andEq(CusCom::getCus_name,compNames).single();
            if(cusCom != null ){
                String legalName = cusCom.getLegal_name();
                // 法人不一致，发送消息提醒
                if(!operName.equals(legalName)){
                    var user = sqlManager.lambdaQuery(TUser.class).andEq(TUser::getAcc_code,cusCom.getCust_mgr()).single();

                    TSystemNotice notice = new TSystemNotice();
                    notice.setId(IdUtil.createSnowflake(0,0).nextId());
                    notice.setState("UNREAD");
                    notice.setType("SYSTEM");
                    notice.setUser_id(user.getId());
                    notice.setContent(compNames + "企业法人有变动！");
                    notice.setBind_data(JSON.toJSONString(null));
                    notice.setAdd_time(new Date());
//                    sqlManager.insert(notice, true);
                }else{
                    String legalCertCode = cusCom.getLegal_cert_code();

                    // 检查是否有贷款
                    Arr names = new Arr();
                    names.add(operName);
                    List<Obj> prt = sqlManager.select("accloan.cun_cus_indiv", Obj.class, o("names", names, "certCode",legalCertCode));

//                    var p = sqlManager.lambdaQuery(RptMRptSlsAcct.class).andEq(RptMRptSlsAcct::getPsn_cert_code, legalCertCode).andEq(RptMRptSlsAcct::getCus_name, operName).select();
                    if(null != prt && !prt.isEmpty()){
                        Link111 link111 = new Link111();
                        link111.setOrigin_name(compNames);
                        link111.setId(IdUtil.objectId());
                        link111.setLink_left(compNames);
                        link111.setLink_right(legalCertCode + "|" +operName);
                        link111.setLink_type("自然人");
                        link111.setLink_rule("11.2");
                        link111.setIs_company(1);

                        TGroupCusList tGroupCusList = new TGroupCusList();
                        tGroupCusList.setAdd_time(new Date());
                        tGroupCusList.setCus_name(compNames);
                        tGroupCusList.setCert_code("");
                        tGroupCusList.setLink_name(operName);
                        tGroupCusList.setLink_cert_code(legalCertCode);
                        tGroupCusList.setLink_rule("11.2");
                        tGroupCusList.setRemark_1("关联自然人有贷款");
                        tGroupCusList.setRemark_2("");
                        tGroupCusList.setRemark_3("");
                        tGroupCusList.setData_flag("01");

                        sqlManager.insert(tGroupCusList, true);

                        sqlManager.lambdaQuery(Link111.class)
                                .andEq(Link111::getLink_rule, "11.2")
                                .andEq(Link111::getOrigin_name, compNames)
                                .delete();
                        sqlManager.insert(link111, true);
                    }
                }
            }
        }
//        //返回所有公司的法人
//        var ret = o();
//        for (QccDetails qccDetails : sqlManager.lambdaQuery(QccDetails.class)
//            .andIn(QccDetails::getInner_company_name, compNames)
//            .select(QccDetails::getOper_name, QccDetails::getInner_company_name)) {
//            ret.put(qccDetails.getInner_company_name(), qccDetails.getOper_name());
//        }
//        return ret;

    }

    public static void do11_3(String compName) {
        System.out.println("do11_3:" + compName);
        var batch = new ArrayList<Link111>();
        var batch2 = new ArrayList<TGroupCusList>();
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return;
        }
        for (Obj obj : detail.getArr("Partners").toObjList()) {
            if(!obj.getStr("StockType","").equals("企业法人")){
                continue;
            }
            //算不出来的一概忽略
            try {
                var p = new BigDecimal(obj.getStr("StockPercent").replaceAll("%", ""));
                if (p.floatValue() >= 25) {
                    var name = obj.getStr("StockName");
//                    var arr = getCompanyHolders(compName);
                    //控股25%以上的公司列表
//                    for (String s : arr.toStrArr()) {
                        var page = 1;
                        while (true) {
                            var list = getHoldingCompany(name, page++);
                            if (list == null) {
                                break;
                            }
                            for (Obj names : list.getArr("Names").toObjList()) {
                                if(!names.getStr("Level","").contains("1")){
                                    continue;
                                }
                                try {
                                    var percent = new BigDecimal(names.getStr("PercentTotal", "").replace("%", ""));
                                    if (percent.floatValue() >= 25) {
                                        Link111 link111 = new Link111();
                                        link111.setId(IdUtil.objectId());
                                        link111.setLink_rule("11.3");
                                        link111.setOrigin_name(compName);
                                        link111.setLink_left(name);
                                        link111.setLink_right(names.getStr("Name"));
                                        link111.setLink_type("企业:" + name);
                                        link111.setIs_company(1);
                                        link111.setStock_percent(percent);
                                        batch.add(link111);

                                        TGroupCusList tGroupCusList = new TGroupCusList();
                                        tGroupCusList.setAdd_time(new Date());
                                        tGroupCusList.setCus_name(compName);
                                        tGroupCusList.setCert_code("");
                                        tGroupCusList.setLink_name(names.getStr("Name"));
                                        tGroupCusList.setLink_cert_code("");
                                        tGroupCusList.setLink_rule("11.3");
                                        tGroupCusList.setRemark_1("有共同关联的企业");
                                        tGroupCusList.setRemark_2("");
                                        tGroupCusList.setRemark_3("");
                                        tGroupCusList.setData_flag("01");

                                        batch2.add(tGroupCusList);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!batch.isEmpty()){
            Set<String> names = batch.stream()
                    .map(e -> e.getLink_right())
                    .collect(Collectors.toSet());
            var leftNames = hasLoan(names);
            batch = (ArrayList<Link111>) batch
                    .stream()
                    .filter(e -> leftNames.contains(e.getLink_right()))
                    .collect(Collectors.toList());
        }


        if (!batch.isEmpty()) {
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getLink_rule, "11.3")
                .andEq(Link111::getOrigin_name, compName)
                .delete();

            System.out.println("do11_3:" + JSONObject.toJSONString(batch2));
            sqlManager.insertBatch(Link111.class, batch);
//            sqlManager.insertBatch(Link111.class, batch2, true);
            for(TGroupCusList item : batch2){
                sqlManager.insert(item, true);
            }

        }

//            var list = sqlManager.lambdaQuery(QccHoldingCompanyNames.class)
//                .andEq(QccHoldingCompanyNames::getInner_company_name, s)
//                .select(QccHoldingCompanyNames::getName, QccHoldingCompanyNames::getPercent_total)
//                .stream()
//                .filter(q -> {
//                    //只保留25%以上的
//                    try {
//                        var percent = new BigDecimal(q.getPercent_total().replace("%", ""));
//                        return percent.floatValue() >= 25;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return false;
//                })
//                .map(p -> p.getName())
//                .distinct()
//                .collect(Collectors.toList());
//
//            var batch = hasLoan(list)
//                .toStrList()
//                .stream()
//                .map(e -> {
//                    Link111 link111 = new Link111();
//                    link111.setId(IdUtil.objectId());
//                    link111.setLink_rule("11.3");
//                    link111.setOrigin_name(compName);
//                    link111.setLink_left(compName);
//                    link111.setLink_right(e);
//                    link111.setLink_type("企业:" + s);
//                    link111.setIs_company(1);
//                    return link111;
//                })
//                .collect(Collectors.toList());
//            if (batch.size() > 0) {
//                sqlManager.insertBatch(Link111.class, batch);
//            }

    }


    public static void do11_4(String compName) {
        var arr = getCompanyHolders(compName);
        if(arr.isEmpty()){
            return;
        }
        //检查在我行是否有贷款
        var names = hasLoan(arr.toStrList());
        //插入关联集团
        var batch = names.toStrList().stream()
            .map(e -> {
                Link111 link111 = new Link111();
                link111.setId(IdUtil.objectId());
                link111.setLink_rule("11.4");
                link111.setOrigin_name(compName);
                link111.setLink_left(compName);
                link111.setLink_right(e);
                link111.setLink_type("关联企业有贷款");
                link111.setIs_company(1);
                return link111;
            })
            .collect(Collectors.toList());

        var batch2 = names.toStrList().stream()
            .map(e -> {
                TGroupCusList tGroupCusList = new TGroupCusList();
                tGroupCusList.setAdd_time(new Date());
                tGroupCusList.setCus_name(compName);
                tGroupCusList.setLink_name(e);
                tGroupCusList.setLink_cert_code("");
                tGroupCusList.setLink_rule("11.4");
                tGroupCusList.setRemark_1("关联企业有贷款");
                tGroupCusList.setRemark_2("");
                tGroupCusList.setRemark_3("");
                tGroupCusList.setData_flag("01");
                return tGroupCusList;
            })
            .collect(Collectors.toList());

        if (batch.size() > 0) {
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getLink_rule, "11.4")
                .andEq(Link111::getOrigin_name, compName)
                .delete();
            sqlManager.insertBatch(Link111.class, batch);

            for(TGroupCusList item : batch2){
                sqlManager.insert(item, true);
            }

//            sqlManager.insertBatch(TGroupCusList.class, batch2, true);
        }

    }

    /**
     * 前置
     * @param name
     * @param isCompany
     */
    public static void do11_5_1(String name, boolean isCompany) {
        var cusids = a();
        if (isCompany) {
            var com = sqlManager.lambdaQuery(CusCom.class)
                .andEq(CusCom::getCus_name, name)
                .select(CusCom::getCus_id);
            for (CusCom cusCom : com) {
                cusids.add(cusCom.getCus_id());
            }

        } else {
            var indiv = sqlManager.lambdaQuery(CusIndiv.class)
                .andEq(CusIndiv::getCus_name, name)
                .select(CusIndiv::getCus_id);
            for (CusIndiv cusIndiv : indiv) {
                cusids.add(cusIndiv.getCus_id());
            }
        }
        var rptlist = sqlManager.lambdaQuery(RptMRptSlsAcct.class)
            .andIn(RptMRptSlsAcct::getCus_id, cusids)
            .select(RptMRptSlsAcct::getLoan_account)
            .stream()
            .map(e -> e.getLoan_account())
            .collect(Collectors.toList());
        if (rptlist.size() == 0) {
            return;
        }
        List<Obj> diyas = sqlManager.select("accloan.04", Obj.class, o("loans", rptlist));
        var total = new BigDecimal(0);
        //由于一个人可能有多个担保品，所以用担保人ID来计算每个人所持有的类型
        var map = o();
        for (Obj diya : diyas) {
            try {
                total = total.add(diya.getBigDecimal("core_value"));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            var key = diya.getStr("guar_no") + "|" + diya.getStr("guar_name");

            //按照人头计算
            BigDecimal value = (BigDecimal) map.get(key);
            if (value == null) {
                value = new BigDecimal(0);
            }
            try{
                value = value.add(diya.getBigDecimal("core_value"));
            }
            catch (Exception e){}
            map.put(key, value);
        }


        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        var limit = new BigDecimal("0.33333");
        BigDecimal finalTotal = total;

        var nos = a();
        map.forEach((k,v) -> {
            try {
                BigDecimal vv = (BigDecimal) v;
                if(vv.divide(finalTotal).compareTo(limit) >= 0){
                    nos.add(k);
                }
            } catch (Exception ee) {
            }
        });

        var batch = diyas
            .stream()
            .filter(e -> {
                return nos.contains(e.getStr("guar_no") + "|" + e.getStr("guar_name"));
//                try {
//                    return (e.getBigDecimal("core_value").divide(finalTotal).compareTo(limit) >= 0);
//                } catch (Exception ee) {
//                    return false;
//                }
            })
            .map(e -> e.getStr("cus_id") + "|" + e.getStr("guar_name"))
            .distinct()
            .map(e -> {
                var link = new Link111();
                link.setId(IdUtil.objectId());
                link.setLink_rule("11.5.1");
                link.setOrigin_name(name);
                link.setLink_left(name);
                link.setLink_right(e);
                link.setLink_type("担保人");
                link.setIs_company(isCompany ? 1 : 0);
                return link;
            })
            .collect(Collectors.toList());
        if (batch.size() > 0) {
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getOrigin_name, name)
                .andEq(Link111::getIs_company, isCompany ? 1 : 0)
                .andEq(Link111::getLink_rule, "11.5.1")
                .delete();
            sqlManager.insertBatch(Link111.class, batch);
        }

    }


    public static void do11_5(String compName) {

        Long start = System.currentTimeMillis();
        List<Obj> objs = sqlManager.select("accloan.11_5", Obj.class, o());
//        sqlManager.lambdaQuery(TGroupCusList.class)
//            .andEq(TGroupCusList::getCus_name, compName)
//            .andEq(TGroupCusList::getLink_rule, "11.5")
//            .andEq(TGroupCusList::getData_flag, "01")
//            .delete();
        var batch = a();
        Date nowDdate = new Date();
        for (Obj list : objs) {
            String guarName = list.getStr("guar_name");
            String linkCertCode = list.getStr("cer_no");
            String cusName = list.getStr("cus_name");
            String certCode = list.getStr("cert_code");
            TGroupCusList tGroupCusList = new TGroupCusList();
            tGroupCusList.setAdd_time(nowDdate);
            tGroupCusList.setCus_name(cusName);
            tGroupCusList.setCert_code(certCode);
            tGroupCusList.setLink_name(guarName);
            tGroupCusList.setLink_cert_code(linkCertCode);
            tGroupCusList.setLink_rule("11.5");
            tGroupCusList.setRemark_1("有共同的担保人");
            tGroupCusList.setRemark_2("");
            tGroupCusList.setRemark_3("");
            tGroupCusList.setData_flag("01");
            try {
                sqlManager.insert(tGroupCusList, true);
            }catch (Exception e){
                System.out.println(e.toString());
                System.out.println(JSONObject.toJSON(tGroupCusList));
            }

//            batch.add(new TGroupCusList(){{
//                setAdd_time(nowDdate);
//                setCus_name(cusName);
//                setCert_code(certCode);
//                setLink_name(guarName);
//                setLink_cert_code(linkCertCode);
//                setLink_rule("11.5");
//                setRemark_1("有共同的担保人");
//                setRemark_2("");
//                setRemark_3("");
//                setData_flag("01");
//            }});
        }
//        sqlManager.insertBatch(TGroupCusList.class, batch, true);

        /*var i = 0;
        var step = 1000;
        for (; i < batch.size() / step + 1; i++) {
            var end = (i+1) * step;
            sqlManager.insertBatch(TGroupCusList.class, batch.subList(i * step, Math.min(end, batch.size())));
        }*/

//        if (batch.isNotEmpty()) {
//            sqlManager.insertBatch(TGroupCusList.class, batch);
//        }

        System.out.println("11_5运行时间：" + (System.currentTimeMillis() - start));


//        var diyas = sqlManager.lambdaQuery(Link111.class)
//            .andEq(Link111::getLink_rule, "11.5.1")
//            .andEq(Link111::getOrigin_name, compName)
//            .select();
//        var batch = a();
//        for (Link111 diya : diyas) {
//            var p = diya.getLink_right().split("\\|");
//            if (p.length != 2) {
//                continue;
//            }
//            var loans = sqlManager.lambdaQuery(RptMRptSlsAcct.class)
//                .andEq(RptMRptSlsAcct::getCus_id, p[0])
//                .select(RptMRptSlsAcct::getCus_name);
//            for (RptMRptSlsAcct loan : loans) {
//                if (loan.getCus_name().equals(compName)) {
//                    continue;
//                }
//                var link = new Link111();
//                link.setId(IdUtil.objectId());
//                link.setLink_rule("11.5");
//                link.setOrigin_name(compName);
//                link.setLink_type("担保关联");
//                link.setLink_left(compName);
//                link.setLink_right(loan.getCus_name());
//                link.setIs_company(1);
//                batch.add(link);
//            }
//        }
//        if (batch.isNotEmpty()) {
//            sqlManager.lambdaQuery(Link111.class)
//                .andEq(Link111::getLink_rule, "11.5")
//                .andEq(Link111::getOrigin_name, compName)
//                .delete();
//            sqlManager.insertBatch(Link111.class, batch);
//        }
    }


    public static void do11_6(){

        List<Obj> objList = sqlManager.select("accloan.11_6",Obj.class,o());
        var batch = a();
        var batch2 = a();
        for (Obj list : objList) {
            var link = new Link111();
            String certCode = list.getStr("cert_code");
            String cerNo = list.getStr("cer_no");
            String cusName = list.getStr("cus_name");
            String guarName = list.getStr("guar_name");

            link.setId(IdUtil.objectId());
            link.setLink_rule("11.6");
            link.setLink_left(cusName);
            link.setOrigin_name(cusName);
            link.setLink_right(guarName);
            link.setLink_type("担保人有贷款");
            link.setIs_company(1);
            batch.add(link);

            batch2.add(new TGroupCusList(){{
                setAdd_time(new Date());
                setCus_name(cusName);
                setCert_code(certCode);
                setLink_name(guarName);
                setLink_cert_code(cerNo);
                setLink_rule("11.6");
                setRemark_1("担保人有贷款");
                setRemark_2("");
                setRemark_3("");
                setData_flag("01");
            }});

            if(batch.isNotEmpty()){
                sqlManager.lambdaQuery(Link111.class)
                    .andEq(Link111::getLink_rule, "11.6")
                    .andEq(Link111::getOrigin_name, list.getStr("cus_name"))
                    .delete();
            }
        }
        if(batch.isNotEmpty()){
            sqlManager.insertBatch(Link111.class, batch);
            sqlManager.insertBatch(TGroupCusList.class, batch2, true);
        }
        /*var diyas = sqlManager.lambdaQuery(Link111.class)
            .andEq(Link111::getLink_rule, "11.5.1")
            .andEq(Link111::getOrigin_name, compName)
            .select();
        var batch = a();
        for (Link111 diya : diyas) {
            var p = diya.getLink_right().split("\\|");
            if(p.length != 2){
                continue;
            }
            //todo: 这里可能需要换成存量对公的客户
            var count = sqlManager.lambdaQuery(RptMRptSlsAcct.class)
                .andEq(RptMRptSlsAcct::getCus_id, p[0])
                .count();
            if(count > 0){
                var link = new Link111() ;
                link.setId(IdUtil.objectId());
                link.setLink_rule("11.6");
                link.setLink_left(compName);
                link.setOrigin_name(compName);
                link.setLink_right(p[1]);
                link.setLink_type("担保关联");
                link.setIs_company(1);
                batch.add(link);
            }
        }
        if(batch.isNotEmpty()){
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getLink_rule, "11.6")
                .andEq(Link111::getOrigin_name, compName)
                .delete();
            sqlManager.insertBatch(Link111.class, batch);
        }*/
    }


    public static void do12_2(String compName){
        //企业基本信息
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return;
        }
        HashSet<String> personNames = new HashSet();
        String operName = detail.getStr("OperName");
        personNames.add(operName);
        var batch = a();
        batch.add(new Link111(){
            {
                setLink_rule("12.2");
                setIs_company(1);
                setId(IdUtil.objectId());
                setLink_type("法人");
                setOrigin_name(compName);
                setLink_left(compName);
                setLink_right(operName);
            }
        });

        //主要人员
        var ps = detail.getArr("Employees");
        for (Obj obj : ps.toObjList()) {
            if(obj.getStr("Job","").contains("董事")){
                String name = obj.getStr("Name");
                personNames.add(name);
                batch.add(new Link111(){
                    {
                        setLink_rule("12.2");
                        setIs_company(1);
                        setId(IdUtil.objectId());
                        setLink_type("董事");
                        setOrigin_name(compName);
                        setLink_left(compName);
                        setLink_right(name);
                    }
                });
            }
        }

        var dps = detail.getArr("Partners");
        for (Obj obj : dps.toObjList()) {
            //算不出来的一概忽略
            try {
                var p = new BigDecimal(obj.getStr("StockPercent").replaceAll("%", ""));
                if (p.floatValue() >= 25) {
                    String stockName = obj.getStr("StockName");
                    personNames.add(stockName);
                    batch.add(new Link111(){{
                        setLink_rule("12.2");
                        setIs_company(1);
                        setId(IdUtil.objectId());
                        setLink_type("自然人股东");
                        setOrigin_name(compName);
                        setLink_left(compName);
                        setLink_right(stockName);
                        setStock_percent(p);
                    }});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(batch.isNotEmpty()){
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getLink_rule, "12.2")
                .andEq(Link111::getOrigin_name, compName)
                .delete();
            sqlManager.insertBatch(Link111.class, batch);

//            sqlManager.lambdaQuery(RelatedPartyList.class)
//                    .andEq(RelatedPartyList::getLink_rule, "12.2")
//                    .andEq(RelatedPartyList::getData_flag,"01")
//                    .andEq(RelatedPartyList::getRelated_name, compName)
//                    .delete();
            for(String name : personNames){
                List<Link111> arr = sqlManager.lambdaQuery(Link111.class)
                    .andEq(Link111::getLink_rule, "12.2")
                    .andEq(Link111::getOrigin_name, compName)
                    .andEq(Link111::getLink_right, name)
                    .select();

                String linkType = "";
                BigDecimal stockPercent = new BigDecimal(0);
                for(Link111 item : arr){
                    linkType += item.getLink_type() + "、";
                    if("自然人股东".equals(item.getLink_type())){
                        stockPercent = item.getStock_percent();
                    }
                }

                RelatedPartyList data = new RelatedPartyList();
                data.setAdd_time(new Date());
                data.setRelated_name(name);
                data.setData_flag("01");
                data.setLink_rule("12.2");
                data.setLink_info(compName);
                data.setRemark_1("企业股东的"+linkType);
                data.setRemark_2(detail.getStr("Address"));
                data.setRemark_3(stockPercent.toString());
                sqlManager.insert(data, true);
            }
        }

    }


    public static void do12_3(String compName){
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return;
        }
        var batch = a();
        var batch2 = a();
        for (Obj obj : detail.getArr("Partners").toObjList()) {
            if(!obj.getStr("StockType","").equals("企业法人")){
                continue;
            }
            //算不出来的一概忽略
            try {
                var p = new BigDecimal(obj.getStr("StockPercent").replaceAll("%", ""));
                if (p.floatValue() >= 25) {
                    String name = obj.getStr("Name");
                    batch.add(new Link111(){{
                        setLink_rule("12.3");
                        setIs_company(1);
                        setId(IdUtil.objectId());
                        setLink_type("企业股东");
                        setOrigin_name(compName);
                        setLink_left(compName);
                        setLink_right(name);
                        setStock_percent(p);
                    }}) ;

                    batch2.add(new RelatedPartyList(){{
                        setRelated_name(name);
                        setLink_info(compName);
                        setLink_rule("12.3");
                        setData_flag("01");
                        setRemark_1("企业股东的企业股东（25%及以上）");
                        setRemark_3(p.toString());
                    }});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(batch.isNotEmpty()){
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getLink_rule, "12.3")
                .andEq(Link111::getOrigin_name, compName)
                .delete();
            sqlManager.insertBatch(Link111.class, batch);

            sqlManager.lambdaQuery(RelatedPartyList.class)
                .andEq(RelatedPartyList::getLink_rule, "12.3")
                .andEq(RelatedPartyList::getRelated_name, compName)
                .andEq(RelatedPartyList::getData_flag, "01")
                .delete();
            sqlManager.insertBatch(RelatedPartyList.class, batch2);
        }

    }

    public static void do12_4(String compName){
        var page = 1;
        var batch = a();
        var batch2 = a();
        while(true){
            var holdings = getHoldingCompany(compName, page++);
            if (holdings == null) {
                break;
            }
            for (Obj names : holdings.getArr("Names").toObjList()) {
                try {
                    var percent = new BigDecimal(names.getStr("PercentTotal", "").replace("%", ""));
                    String name = names.getStr("Name");
                    if (percent.floatValue() >= 25) {
                        Link111 link111 = new Link111();
                        link111.setId(IdUtil.objectId());
                        link111.setLink_rule("12.4");
                        link111.setOrigin_name(compName);
                        link111.setLink_left(compName);
                        link111.setLink_right(names.getStr("Name"));
                        link111.setLink_type("控股公司");
                        link111.setIs_company(1);
                        link111.setStock_percent(percent);
                        batch.add(link111);

                        batch2.add(new RelatedPartyList(){{
                            setRelated_name(name);
                            setLink_info(compName);
                            setLink_rule("12.4");
                            setData_flag("01");
                            setRemark_1("企业股东控股（25%及以上）的企业");
                            setRemark_3(percent.toString());
                        }});

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(batch.isNotEmpty()){
            sqlManager.lambdaQuery(Link111.class)
                .andEq(Link111::getOrigin_name, compName)
                .andEq(Link111::getLink_rule, "12.4")
                .delete();
            sqlManager.insertBatch(Link111.class, batch);

            sqlManager.lambdaQuery(RelatedPartyList.class)
                .andEq(RelatedPartyList::getLink_rule, "12.4")
                .andEq(RelatedPartyList::getRelated_name, compName)
                .andEq(RelatedPartyList::getData_flag, "01")
                .delete();
            sqlManager.insertBatch(RelatedPartyList.class, batch2);
        }
    }

    public static void do12_5(String partners, String IDCard){
        // 担任法人公司列表
        var lists = sqlManager.lambdaQuery(CusCom.class)
                .andEq(CusCom::getLegal_name, partners)
                .andEq(CusCom::getLegal_cert_code, IDCard)
                .select();
        if(null == lists || lists.isEmpty()){
            // TODO::判断是否是关联方数据中的关联人
//            var linkList = sqlManager.lambdaQuery(Link111.class).andEq(Link111::getLink_right, IDCard + "|"+partners).select();
//            if(null == linkList || linkList.isEmpty()){
//                return;
//            }else{
//                for(Link111 link: linkList){
//                    String company = link.getLink_left();
//                    save12_5(company);
//                }
//            }
        }else{
            for(CusCom list : lists){
                String company = list.getCus_name();
                save12_5(partners, IDCard, company);
            }
        }

    }

    // 12_5规则 查询法人、自然人股东、董事并保存
    private static void save12_5(String partners, String IDCard, String compName){
        String originName = partners + "|" + IDCard;
        String linkType = "担任法人";
        //企业基本信息
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return;
        }
        var batch = a();
        batch.add(new Link111(){
            {
                setLink_rule("12.5");
                setIs_company(1);
                setId(IdUtil.objectId());
                setLink_type("法人");
                setOrigin_name(originName);
                setLink_left(compName);
                setLink_right(partners);
            }
        });

        //主要人员
        var ps = detail.getArr("Employees");
        for (Obj obj : ps.toObjList()) {
            if(obj.getStr("Job","").contains("董事") && partners.equals(obj.getStr("Name"))){
                batch.add(new Link111(){
                    {
                        setLink_rule("12.5");
                        setIs_company(1);
                        setId(IdUtil.objectId());
                        setLink_type("董事");
                        setOrigin_name(originName);
                        setLink_left(compName);
                        setLink_right(partners);
                    }
                });
                linkType += "/担任董事";
                break;
            }
        }

        BigDecimal stockPercent = new BigDecimal(0);
        // 股东
        var dps = detail.getArr("Partners");
        for (Obj obj : dps.toObjList()) {
            //算不出来的一概忽略
            var p = new BigDecimal(obj.getStr("StockPercent").replaceAll("%", ""));
            if (p.floatValue() >= 25 && "自然人股东".equals(obj.getStr("StockType"))
            && partners.equals(obj.getStr("StockName"))) {
                batch.add(new Link111(){{
                    setLink_rule("12.5");
                    setIs_company(1);
                    setId(IdUtil.objectId());
                    setLink_type("自然人股东");
                    setOrigin_name(originName);
                    setLink_left(compName);
                    setLink_right(partners);
                    setStock_percent(p);
                }});
                stockPercent = p;
                linkType += "/担任股东(25%及以上)";
                break;
            }
        }

        if(batch.isNotEmpty()){
            sqlManager.lambdaQuery(Link111.class)
                    .andEq(Link111::getLink_rule, "12.5")
                    .andEq(Link111::getOrigin_name, compName)
                    .delete();
            sqlManager.insertBatch(Link111.class, batch);

            sqlManager.lambdaQuery(RelatedPartyList.class)
                    .andEq(RelatedPartyList::getLink_rule, "12.5")
                    .andEq(RelatedPartyList::getCert_code, IDCard)
                    .andEq(RelatedPartyList::getData_flag, "01")
                    .delete();
            RelatedPartyList data = new RelatedPartyList();
            data.setAdd_time(new Date());
            data.setLink_info(partners);
            data.setRelated_name(compName);
            data.setCert_code(IDCard);
            data.setData_flag("01");
            data.setLink_rule("12.5");
            data.setRemark_1("自然人股东"+linkType+"的公司");
            data.setRemark_2(detail.getStr("Address"));
            data.setRemark_3(stockPercent.toString());
            sqlManager.insert(data, true);
        }
    }

    /**
     * 检查某些公司是否在我行有贷款
     * @param names
     */
    private static Arr hasLoan(Collection<String> names) {
        if(names.size() > 0){
            List<Obj> allows = sqlManager.select("accloan.cun_cus_com", Obj.class, o("names", names));
//        var allows = sqlManager.lambdaQuery(CusComList.class)
//            .andIn(CusComList::getCus_name, names)
//            .select();
            return a(allows.stream()
                    .map(e -> e.getStr("cus_name"))
                    .toArray()
            );
        }else{
            return new Arr();
        }

    }

    /**
     * 得到某个公司控股持股25%以上的股东
     * @return
     */
    private static Arr getCompanyHolders(String compName) {
        var arr = a();
        //持股25%以上的企业股东
        var detail = getCompanyDetail(compName);
        if (detail == null) {
            return arr;
        }
        for (Obj obj : detail.getArr("Partners").toObjList()) {
            if(!obj.getStr("StockType","").equals("企业法人")){
                continue;
            }
            //算不出来的一概忽略
            try {
                var p = Float.parseFloat(obj.getStr("StockPercent").replaceAll("%", ""));
                if (p >= 25) {
                    arr.add(obj.getStr("StockName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        var ps = sqlManager.lambdaQuery(QccDetailsPartners.class)
//            .andEq(QccDetailsPartners::getInner_company_name, compName)
//            .andEq(QccDetailsPartners::getStock_type, "企业法人")
//            .select();
//        for (QccDetailsPartners p : ps) {
//            try {
//                var percent = new BigDecimal(p.getStock_percent().replace("%", ""));
//                if (percent.floatValue() >= 25) {
//                    arr.add(p.getStock_name());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return arr;
    }


    private static BigDecimal convertToMoney(String str) {
        var sstr = str.replaceAll("人民币", "");
        BigDecimal bg = null;
        try {
            if (sstr.contains("万")) {
                sstr = sstr.replaceAll("万港?美?元?|\\s+", "");
                bg = new BigDecimal(sstr);
                bg = bg.multiply(new BigDecimal(10000));
            } else {
                bg = new BigDecimal(sstr);
            }
            return bg;
        }catch (Exception e){
            return new BigDecimal(0);
        }

    }

    private static Obj getCompanyDetail(String compName){
        try{
            var str = HttpUtil.get(getUrl("/ECIV4/GetDetailsByName"), o("fullName", compName));
            return (Obj) Json.parseObject(str).getByPath("Result");
        } catch (Exception e){
            return null;
        }
    }

    private static Obj getCia(String compName, String perName){
        try{
            var str = HttpUtil.get(getUrl("/CIAEmployeeV4/GetStockRelationInfo"), o("fullName", compName, "personName", perName));
            return (Obj) Json.parseObject(str).getByPath("Result");
        } catch (Exception e){
            return null;
        }
    }


    private static Obj getHoldingCompany(String compName, int page){
        try{
            var str = HttpUtil.get(getUrl("/HoldingCompany/GetHoldingCompany"), o("fullName", compName, "paegIndex", page, "pageSize", 100));
            var obj = (Obj) Json.parseObject(str);
            var paging = obj.getObj("Paging");
            //没有下一页了
            if(paging.getIntValue("TotalRecords") / 100 + 1 < page){
                return null;
            }
            return (Obj) obj.getByPath("Result");
        }
        catch (Exception e){
            return null;
        }
    }

    private static String getUrl(String path){
        var str = config.ext.getStr("qcc-search-url");
        return String.format("%s%s", str, path);
    }

    public static void saveGroupCusList(JSONObject jsonObject){
        TGroupCusList entity = new TGroupCusList();
        entity.setId(IdUtil.createSnowflake(0, 0).nextId());
        entity.setAdd_time(jsonObject.getDate("nowDdate"));
        entity.setCus_name(jsonObject.getString("cusName"));
        entity.setCert_code(jsonObject.getString("certCode"));
        entity.setLink_name(jsonObject.getString("guarName"));
        entity.setLink_cert_code(jsonObject.getString("linkCertCode"));
        entity.setLink_rule(jsonObject.getString("rule"));
        entity.setRemark_1(jsonObject.getString("remark1"));
        entity.setRemark_2(jsonObject.getString("remark2"));
        entity.setRemark_3(jsonObject.getString("remark3"));
        entity.setData_flag("01");
        sqlManager.insert(entity);

    }

    public static void queryCusList(String linkRule){
        List<JSONObject> lists = sqlManager.select("accloan.根据取数规则查询", JSONObject.class, o("linkRule",linkRule));
        Map<String, String> cMap = new HashMap<>();
        for(JSONObject a : lists){
            String linkLeft = a.getString("link_left");
            String linkRight = a.getString("link_right");
            String linkType = a.getString("link_type");
            String stockPercent = a.getString("stock_percent");
            if(null == cMap.get(linkLeft + "|" + linkRight) || cMap.get(linkLeft + "|" + linkRight).isEmpty()){
                cMap.put(linkLeft + "|" + linkRight, linkType);
            }else{
                String org = cMap.get(linkLeft + "|" + linkRight);
                if(!org.contains(linkType)){
                    org = org + "," + linkType;
                }
                if(null != stockPercent){
                    if(!org.contains(stockPercent)){
                        org = org +"|" + stockPercent;
                    }
                }
                cMap.put(linkLeft + "|" + linkRight, org);
            }
        }
        Date nowDate = new Date();
        for(Map.Entry<String, String > map : cMap.entrySet()){
            var cp = map.getKey().split("\\|");
            String cusName = "";
            String comp = "";
            if(cp.length>=2){
                comp = cp[0];
                cusName = cp[1];
            }
            var ts = map.getValue().split("\\|");
            String type = "";
            String stockPercent = "";
            if(ts.length>=2){
                type = ts[0];
                stockPercent = ts[1];
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nowDdate", nowDate);
            jsonObject.put("cusName", comp);
            jsonObject.put("guarName", cusName);
            jsonObject.put("rule","11.1");
            jsonObject.put("remark1",type);
            jsonObject.put("remark2",stockPercent);
            jsonObject.put("remark3","");

            saveGroupCusList(jsonObject);
        }
    }
}
