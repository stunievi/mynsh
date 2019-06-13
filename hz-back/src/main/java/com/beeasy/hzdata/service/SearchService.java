package com.beeasy.hzdata.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.entity.GP;
import com.beeasy.hzback.view.GPC;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.hzback.entity.Org;
import com.beeasy.hzback.entity.User;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchService {

    @Autowired
    SQLManager sqlManager;


    //字段施加限制
//    public static Map<String, Object> fieldsMap = C.newMap(
//        "201", C.newMap(
//            "no", 201,
//            "type", SearchTargetType.CUS_COM,
//            "fields", S.fastSplit("CUS_ID,CUS_NAME,CUS_TYPE,CERT_TYPE,CERT_CODE,INVEST_TYPE,COM_SUB_TYP,COM_SCALE,COM_HOLD_TYPE,COM_INS_CODE,COM_CLL_TYPE,COM_CLL_NAME,COM_EMPLOYEE,COM_CRD_TYP,COM_CRD_GRADE,COM_OPT_ST,CUST_MGR,MAIN_BR_ID", ",")
//        ),
//        "202", C.newMap(
//            "no", 202,
//            "type", SearchTargetType.CUS_INDIV,
//            "fields", S.fastSplit("CUS_ID,MNG_BR_ID,CUS_TYPE,CUS_NAME,INDIV_SEX,CERT_TYPE,CERT_CODE,INDIV_NTN,INDIV_BRT_PLACE,INDIV_POL_ST,INDIV_EDT,INDIV_MAR_ST,CUST_MGR,MAIN_BR_ID,CUS_STATUS", ",")
//        ),
//        "203", C.newMap(
//            "no", 203,
//            "type", SearchTargetType.ACC_LOAN,
//            "fields", S.fastSplit("BILL_NO,CONT_NO,PRD_NAME,PRD_TYPE,CUS_ID,CUS_NAME,LOAN_ACCOUNT,LOAN_FORM,LOAN_NATURE,ASSURE_MEANS_MAIN,CUR_TYPE,LOAN_START_DATE,LOAN_END_DATE,TERM_TYPE,ORIG_EXPI_DATE,REPAYMENT_MODE,CLA,CLA_DATE,CUS_MANAGER,INPUT_BR_ID,FINA_BR_ID,MAIN_BR_ID,ACCOUNT_STATUS,RETURN_DATE", ",")
//        )
//    );

    public static volatile Map<String, Integer> InnateMap = C.newMap(
            "贷后跟踪-零售银行部个人按揭", 105,
            "贷后跟踪-小微部个人类", 104,
            "贷后跟踪-零售银行部个人经营", 105,
            "贷后跟踪-零售银行部个人消费", 105,
            "贷后跟踪-小微部公司类", 103,
            "贷后跟踪-企查查贷后检查", 329

    );

    public PageQuery<JSONObject> search(
            String no,
            Map<String, Object> params,
            long uid,
            boolean su
//        ,BeetlPager beetlPager
    ) {
        su = true;
        boolean own = false;
//        User user = null;
        Set<String> overLimit = C.newSet("229", "219", "214", "217");
        Set<String> noLimit = C.newSet("201", "215", "216", "202", "218", "203", "03", "220", "04", "230");
        if (S.eq((String) params.get("own"), "1")) {
            own = true;
        }
        //台账特殊处理
        if (S.eq(no, "219") && S.notEmpty((String) params.get("modelName"))) {
            own = true;
        }
        if (own) {
            params.put("own", 1);
        }
        params.put("uid", uid);

        PageQuery<JSONObject> pageQuery = U.beetlPageQuery("accloan." + no, JSONObject.class, params);
        //查找结果限制
        if (no.equalsIgnoreCase("201") || no.equalsIgnoreCase("202") || no.equalsIgnoreCase("203")) {
            //查找该用户的
            Set<String> allowFields = sqlManager.lambdaQuery(GPC.class)
                    .andEq(GPC::getUid, uid)
                    .andEq(GPC::getType, GP.Type.DATA_SEARCH_RESULT)
                    .andEq(GPC::getObjectId, no)
                    .select(GPC::getDescription)
                    .stream()
                    .map(GPC::getDescription)
                    .filter(S::notEmpty)
                    .flatMap(d -> Arrays.stream(d.split(",")))
                    .collect(Collectors.toSet());

            for (JSONObject map : pageQuery.getList()) {
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    //
                    if (entry.getKey() instanceof String && ((String) entry.getKey()).startsWith("$")) {
                        continue;
                    }
                    if (!allowFields.contains(entry.getKey())) {
                        it.remove();
                    }
                }
            }
        }

        switch (no) {
            case "229":
                Log.log("查询所有客户",0);
                break;
            case "214":
                Log.log("查询对公客户",0);
                break;
            case "217":
                Log.log("查询对私客户",0);
                break;
            case "219":
                Log.log("查询贷款资料",0);
                break;
            case "232":
                Log.log("查询抵押物信息",0);
                break;
            case "231":
                Log.log("查询实际控制人",0);
                break;
            case "203":
                Log.log("查询台账信息",0);
                break;
            case "03":
                Log.log("查询担保合同",0);
                break;
            case "220":
                Log.log("查询贷款合同",0);
                break;
            case "04":
                Log.log("查询抵押物明细",0);
                break;
            case "230":
                Log.log("查询还款记录",0);
                break;
            case "320":
                Log.log("查询还款明细",0);
                break;
            case "201":
                Log.log("查询对公客户基本信息",0);
                break;
            case "215":
                Log.log("查询对公客户高管信息",0);
                break;
            case "216":
                Log.log("查询对公客户联系信息",0);
                break;
            case "202":
                Log.log("查询对私客户详情基本信息",0);
                break;
            case "218":
                Log.log("查询对私客户收入情况",0);
                break;
        }

        if (true) {
            return pageQuery;
        }

        try {
            final String modelName = (String) params.getOrDefault("modelName", "");

            if (no.equals("219")) {
                pageQuery.setList(
                        pageQuery.getList().stream()
                                .map(map -> {
//                    Map<String, String> map = (Map<String, String>) o;
                                    //补充关联用户
//                    User u = accCodes.get(map.getOrDefault("CUST_MGR", ""));
//                    if (null != u) {
//                        map.put("pubUserId", u.getId() + "");
//                        map.put("pubUserName", u.getTrueName());
//                    }
                                    String loanAccount = (String) map.getOrDefault("LOAN_ACCOUNT", "");
//                    if (modelName.contains("贷后跟踪")) {
//                        String name = loanModelNames.getOrDefault(loanAccount, "");
//                        for (WfModel m : finalModels) {
//                            if (S.neq(m.getModelName(), name)) {
//                                continue;
//                            }
//                            if (!pubMap.get(m.getId())) {
//                                continue;
//                            }
//                            map.put("pubModelId", m.getId() + "");
//                            map.put("pubModelName", m.getModelName());
//                            break;
//                        }
//                    } else if (C.notEmpty(finalModels) && pubMap.get(finalModels.get(0).getId())) {
//                        map.put("pubModelId", finalModels.get(0).getId() + "");
//                        map.put("pubModelName", finalModels.get(0).getModelName());
//                    }

                                    //当有任务正在进行时, 禁止发起相同的登记和不良资产管理流程
                                    if (modelName.equals("不良资产登记")) {
                                        long count = sqlManager.lambdaQuery(WfIns.class)
                                                .andEq(WfIns::getModelName, modelName)
                                                .andEq(WfIns::getLoanAccount, loanAccount)
                                                .andNotIn(WfIns::getState, C.newList(WfIns.State.CANCELED, WfIns.State.FINISHED))
                                                .count();
//                        Map r = sqlManager.selectSingle("accloan.countSameBL", C.newMap("MODEL_NAME", modelName, "LOAN_ACCOUNT", loanAccount), Map.class);
//                        int count = (int) r.getOrDefault("1", 0);
                                        if (count > 0) {
                                            map.put("CAN_PUB", 0);
                                        }
                                    }
                                    return map;

                                })
                                .collect(Collectors.toList())
                );
            }
            //对公客户
            //对私客户
            //全部客户
//            else if (no.equals("214") || no.equals("217") || no.equals("229")) {
////                models = sqlManager.lambdaQuery(WfModel.class)
////                        .andLike(WfModel::getModelName, "%资料收集%")
////                        .andEq(WfModel::getOpen, true)
////                        .select();
////                List<WfModel> finalModels1 = models;
////                initPubMap(pubMap, uid, models);
//                ret = ((PageImpl) ret).map(o -> {
//                    Map<String, String> map = (Map<String, String>) o;
//                    map.put("pubModelId", "0");
//                    map.put("pubModelName", "");
////                    if (C.notEmpty(finalModels1) && pubMap.get(finalModels1.get(0).getId())) {
////                        map.put("pubModelId", finalModels1.get(0).getId() + "");
////                        map.put("pubModelName", finalModels1.get(0).getModelName());
////                    }
//                    return map;
//                });
//            }
            return pageQuery;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RestException("参数错误");
        }
    }


//    private void initPubMap(final Map pubMap, final long uid, final List<WfModel> models){
//        Long[] ls = models.stream()
//                .map(WfModel::getId)
//                .peek(l -> pubMap.put(l,false))
//                .collect(Collectors.toList())
//                .toArray(new Long[models.size()]);
//        pubMap.putAll(backRpc.canPub(uid,ls));
//    }
//    private void initLoanMap(final Map<String,String> loanMap){
//        if(loanMap.size() == 0){
//            return;
//        }
//        Map<String,String> map = sqlManager.select("task.selectModelName",Map.class, C.newMap(
//                "v0", loanMap.keySet()
//        )).stream().collect(Collectors.toMap(
//                item -> (String)item.getOrDefault("LOAN_ACCOUNT",""),
//                item -> {
//                    String modelName = (String)item.getOrDefault("MODEL_NAME","");
//                    if(S.blank(modelName)){
//                        return "";
//                    }
//                    return modelName;
//                }
//        ));
//        loanMap.putAll(map);
//    }

//    private void initUserMap(final Map<String,User> userMap){
//        if(C.empty(userMap)){
//            userMap.put("-1",null);
//        }
//        sqlManager.lambdaQuery(User.class)
//            .andEq(User::getAccCode, )
//        Map<String,User> map = sqlManager.select("workflow.selectUsersByAccCode", User.class, C.newMap("codes", userMap.keySet())).stream().collect(Collectors.toMap(
//                item -> item.getAccCode(),
//                item -> item,
//                (v1,v2) -> v1));
//        userMap.putAll(map);
//
//    }


    /**
     * 你问住我了, 这太久远了我也不知道是啥意思了
     *
     * @param uid
     * @return
     */
//    private Map getPermissionLimit(final User user) {
//        if (null == user) {
//            return null;
//        }
////        if (user.getSu()) {
////            return C.newMap();
////        }
////        List<Quarters> qs = (List<Quarters>) user.get("qs");
//        List<Map> ps = sqlManager.select("user.selectPsByUser", Map.class, new HashMap() {{
//            put("uid", user.getId());
//            put("type", "DATA_SEARCH_CONDITION");
//            put("userType", "ROLE");
//        }});
//        List<String> managerCode = new ArrayList<>();
//        List<String> userCode = new ArrayList<>();
//        List<String> finalManagerCode = managerCode;
//        List<String> finalUserCode = userCode;
//
//        //default permission by manager
//        sqlManager.select("user.selectManagedDepartment", Org.class, C.newMap("uid", user.getId()))
//            .stream()
//            .map(Org::getAccCode)
//            .forEach(managerCode::add);
////        qs.stream().filter(q -> q.manager)
////                .map(q -> (Department) q.get("dep"))
////                .filter($::isNotNull)
////                .map(dep -> (dep.accCode))
////                .forEach(managerCode::add);
//        //default permission by user
//        userCode.add(user.getAccCode());
//
//        //external permission
//        ps.stream().filter(p -> null != p.get("description"))
//            .map(p -> (JSONArray) (JSON.parse(p.get("description").toString())))
//            .map(arr -> arr.toJavaList(SearchConditionRule.class))
//            .flatMap(List::stream)
//            //暂时不要这个, 虽然将来还可能加回来
////                .filter(rule -> rule.targetType.equals(searchTargetType))
//            .forEach(rule -> {
//                switch (rule.searchType) {
//                    case FOR_DEPARTMENT:
//                        finalManagerCode.add(rule.value);
//                        break;
//
//                    case FOR_USER:
//                        finalUserCode.add(rule.value);
//                        break;
//                }
//            });
//        String uuid = "!@#$";
//        //去除无用数据
//        managerCode.add(uuid);
//        userCode.add(uuid);
//        System.out.println("rilegou");
//        return C.newMap(
//            "dep", managerCode.stream()
//                .filter(S::isNotBlank)
//                .distinct()
//                .collect(Collectors.toList())
//            , "user", userCode.stream()
//                .filter(S::isNotBlank)
//                .distinct()
//                .collect(Collectors.toList()));
//    }

    /**
     * 别问我我也不知道为什么这么写啊
     *
     * @param user
     * @param searchTargetType
     * @param ret
     * @param defaultFields
     * @return
     */
//    private List getPermissionResultLimit(User user, String no, List<JSONObject> ret, List<String> defaultFields) {
//        List<GPC> gps = sqlManager.lambdaQuery(GPC.class)
//            .andEq(GPC::getUid, user.getId())
//            .andEq(GPC::getObjectId, no)
//            .andEq(GPC::getType, GP.Type.DATA_SEARCH_RESULT)
//            .select();
//        Set<String> fields = gps.stream()
//            .map(GPC::getDescription)
//            .filter($::isNotNull)
//            .flatMap(d -> Arrays.stream(d.split(",")))
//            .filter(S::notEmpty)
//            .collect(Collectors.toSet());
//        fields.addAll(defaultFields);
//
//        for (Map<String, Object> map : ret) {
//            Iterator it = map.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry entry = (Map.Entry) it.next();
//                //
//                if(entry.getKey() instanceof String && ((String) entry.getKey()).startsWith("$")){
//                    continue;
//                }
//                if (!fields.contains(entry.getKey())) {
//                    it.remove();
//                }
//            }
//        }
//        return ret;
//    }


//    public static class SearchConditionRule {
//        //查询规则
//        public SearchType       searchType;
//        //授权对象
//        public SearchTargetType targetType;
//        //约束类型
//        public SearchValueType  valueType;
//        //约束值
//        public String           value;
//    }

//    public static class SearchResultRule {
//        //授权对象
//        public SearchTargetType searchTargetType;
//        //限制字段
//        public String           value;
//    }
//
//    public enum SearchType {
//        FOR_DEPARTMENT,
//        FOR_USER,
//        //不做限制
////        UNLIMITED
//    }
//
//    public enum SearchTargetType {
//        //对公客户
//        CUS_COM,
//        //对私客户
//        CUS_INDIV,
//        //贷款合同
//        CTR_LOAN_CONT,
//        //担保合同
//        GRT_GUAR_CONT,
//        //贷款台账
//        ACC_LOAN
//        //高管
////        CUS_COM_MANAGER,
//        //联系信息
////        CUS_COM_CONT
////        PRIVATE_CLIENT,
////        PUBLIC_CLIENT,
////        ACC_LOAN_DATA
//    }
//
//    public enum SearchValueType {
//        BIND_VALUE,
//        CUSTOM
//    }
}
