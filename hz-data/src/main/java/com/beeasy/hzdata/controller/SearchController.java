package com.beeasy.hzdata.controller;

import act.app.ActionContext;
import act.controller.Controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avaje.ebean.config.UnderscoreNamingConvention;
import com.beeasy.hzdata.entity.Department;
import com.beeasy.hzdata.entity.Quarters;
import com.beeasy.hzdata.entity.User;
import com.beeasy.hzdata.filter.AuthFilter;
import com.beeasy.hzdata.utils.RetJson;
import com.beeasy.hzdata.utils.Utils;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.intellij.lang.annotations.JdkConstants;
import org.osgl.$;
import org.osgl.http.H;
import org.osgl.mvc.annotation.*;
import org.osgl.mvc.result.*;
import org.osgl.util.C;
import org.osgl.util.S;
import org.rythmengine.internal.parser.build_in.DebugParser;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import javax.persistence.Column;
import java.util.*;
import java.util.stream.Collectors;


@With(AuthFilter.class)
public class SearchController {


    //字段施加限制
    public static Map<String, Object> fieldsMap = C.newMap(
            "201", C.newMap(
                    "no", 201,
                    "type", SearchTargetType.CUS_COM,
                    "fields", S.fastSplit("CUS_ID,CUS_NAME,CUS_TYPE,CERT_TYPE,CERT_CODE,INVEST_TYPE,COM_SUB_TYP,COM_SCALE,COM_HOLD_TYPE,COM_INS_CODE,COM_CLL_TYPE,COM_CLL_NAME,COM_EMPLOYEE,COM_CRD_TYP,COM_CRD_GRADE,COM_OPT_ST,CUST_MGR,MAIN_BR_ID", ",")
            ),
            "202", C.newMap(
                    "no", 202,
                    "type", SearchTargetType.CUS_INDIV,
                    "fields", S.fastSplit("CUS_ID,MNG_BR_ID,CUS_TYPE,CUS_NAME,INDIV_SEX,CERT_TYPE,CERT_CODE,INDIV_NTN,INDIV_BRT_PLACE,INDIV_POL_ST,INDIV_EDT,INDIV_MAR_ST,CUST_MGR,MAIN_BR_ID,CUS_STATUS", ",")
            ),
            "203", C.newMap(
                    "no", 203,
                    "type", SearchTargetType.ACC_LOAN,
                    "fields", S.fastSplit("BILL_NO,CONT_NO,PRD_NAME,PRD_TYPE,CUS_ID,CUS_NAME,LOAN_ACCOUNT,LOAN_FORM,LOAN_NATURE,ASSURE_MEANS_MAIN,CUR_TYPE,LOAN_START_DATE,LOAN_END_DATE,TERM_TYPE,ORIG_EXPI_DATE,REPAYMENT_MODE,CLA,CLA_DATE,CUS_MANAGER,INPUT_BR_ID,FINA_BR_ID,MAIN_BR_ID,ACCOUNT_STATUS,RETURN_DATE", ",")
            )
    );

    @Inject
    SQLManager sqlManager;

    @Inject
    User.Mapper userMapper;

    @Action("/search/accloan/{no}")
    public Result search(
            String no,
            Map<String, Object> params,
            int page,
            int size,
            H.Flash flash
    ) {
        final long uid = Long.parseLong(flash.get("uid"));
        User user = sqlManager.unique(User.class,uid);
        //permission
        Map<String, List> limitMap = getPermissionLimit(user);
        if (null == limitMap) {
            return RetJson.error("找不到数据").toResult();
        } else if (C.notEmpty(limitMap)) {
            params.put("deplimit", limitMap.get("dep"));
            params.put("userlimit", limitMap.get("user"));
        }
        PageQuery pageQuery = getPage(page, size);
        try {
            pageQuery.setParas(params);
            PageQuery pg = sqlManager.pageQuery("accloan." + no, Map.class, pageQuery);
            //命名方式错误, 转换
            List retList = Utils.underLineList(pg.getList());
            //字段过滤
            if(fieldsMap.containsKey(no)){
                Map item = (Map) fieldsMap.get(no);
                retList = getPermissionResultLimit(user, (SearchTargetType)item.get("type"),retList, (List)item.get("fields"));
            }
            Page ret = new PageImpl(retList, PageRequest.of((int) pageQuery.getPageNumber() - 1, (int) pageQuery.getPageSize()), pageQuery.getTotalRow());
            return RetJson.ok(ret).toResult();
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("参数错误").toResult();
        }
    }

    private PageQuery getPage(int page, int size) {
        if (page <= 1) {
            page = 1;
        }
        if (size <= 0) {
            size = 15;
        }
        return new PageQuery(page, size);
    }


//    private User findUserById(final long uid) {
//        User template = new User();
//        template.id = uid;
//        User user = sqlManager.templateOne(template);
//        return user;
//    }

    /**
     * 你问住我了, 这太久远了我也不知道是啥意思了
     *
     * @param uid
     * @return
     */
    private Map getPermissionLimit(final User user) {
        if (null == user) {
            return null;
        }
        if (user.su) {
            return C.newMap();
        }
//        List<Quarters> qs = (List<Quarters>) user.get("qs");
        List<Map> ps = sqlManager.select("user.selectPsByUser", Map.class, new HashMap() {{
            put("uid", user.id);
            put("type", "DATA_SEARCH_CONDITION");
            put("userType", "ROLE");
        }});
        List<String> managerCode = new ArrayList<>();
        List<String> userCode = new ArrayList<>();
        List<String> finalManagerCode = managerCode;
        List<String> finalUserCode = userCode;

        //default permission by manager
        sqlManager.select("user.selectManagedDepartment", Department.class, C.newMap("uid", user.id))
                .stream()
                .map(dep -> dep.accCode)
                .forEach(managerCode::add);
//        qs.stream().filter(q -> q.manager)
//                .map(q -> (Department) q.get("dep"))
//                .filter($::isNotNull)
//                .map(dep -> (dep.accCode))
//                .forEach(managerCode::add);
        //default permission by user
        userCode.add(user.accCode);

        //external permission
        ps.stream().filter(p -> null != p.get("description"))
                .map(p -> (JSONArray) JSON.parse(p.get("description").toString()))
                .map(arr -> arr.toJavaList(SearchConditionRule.class))
                .flatMap(List::stream)
                //暂时不要这个, 虽然将来还可能加回来
//                .filter(rule -> rule.targetType.equals(searchTargetType))
                .forEach(rule -> {
                    switch (rule.searchType) {
                        case FOR_DEPARTMENT:
                            finalManagerCode.add(rule.value);
                            break;

                        case FOR_USER:
                            finalUserCode.add(rule.value);
                            break;
                    }
                });
        String uuid = "!@#$";
        //去除无用数据
        managerCode.add(uuid);
        userCode.add(uuid);
        return C.newMap(
                "dep", managerCode.stream()
                        .filter(S::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList())
                , "user", userCode.stream()
                        .filter(S::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList()));
    }

    /**
     * 别问我我也不知道为什么这么写啊
     *
     *
     * @param user
     * @param searchTargetType
     * @param ret
     * @param defaultFields
     * @return
     */
    private List getPermissionResultLimit(User user, SearchTargetType searchTargetType, List<Map<String,Object>> ret, List<String> defaultFields) {
        if (user.su) {
            return ret;
        }
        List<Map> ps = sqlManager.select("user.selectPsByUser", Map.class, C.newMap(
                "uid", user.id,
                "type", "DATA_SEARCH_RESULT",
                "userType", "ROLE"
        ));
        Set<String> fields = ps.stream()
                .filter(p -> $.isNotNull(p.get("description")))
                .map(p -> (JSONArray) JSON.parse(p.get("description").toString()))
                .map(arr -> arr.toJavaList(SearchResultRule.class))
                .flatMap(List::stream)
                .filter(item -> item.searchTargetType.equals(searchTargetType))
                .flatMap(item -> S.fastSplit(item.value, "\n").stream())
                .filter(S::isNotBlank)
                .map(item -> item.trim())
                .collect(Collectors.toSet());
        fields.addAll(defaultFields);

        for (Map<String, Object> map : ret) {
            Iterator it = map.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next();
                if(!fields.contains(entry.getKey())){
                    it.remove();
                }
            }
        }
        return ret;
    }



    public static class SearchConditionRule {
        //查询规则
        public SearchType searchType;
        //授权对象
        public SearchTargetType targetType;
        //约束类型
        public SearchValueType valueType;
        //约束值
        public String value;
    }

    public static class SearchResultRule {
        //授权对象
        public SearchTargetType searchTargetType;
        //限制字段
        public String value;
    }

    public enum SearchType {
        FOR_DEPARTMENT,
        FOR_USER,
        //不做限制
//        UNLIMITED
    }

    public enum SearchTargetType {
        //对公客户
        CUS_COM,
        //对私客户
        CUS_INDIV,
        //贷款合同
        CTR_LOAN_CONT,
        //担保合同
        GRT_GUAR_CONT,
        //贷款台账
        ACC_LOAN
        //高管
//        CUS_COM_MANAGER,
        //联系信息
//        CUS_COM_CONT
//        PRIVATE_CLIENT,
//        PUBLIC_CLIENT,
//        ACC_LOAN_DATA
    }

    public enum SearchValueType {
        BIND_VALUE,
        CUSTOM
    }


}
