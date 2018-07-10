package com.beeasy.hzback.modules.system.service;


import com.alibaba.fastjson.JSONArray;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.system.dao.IGlobalPermissionDao;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.objects.Global;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSearchService {

    @Autowired
    SqlUtils sqlUtils;
    @Autowired
    UserService userService;
    @Autowired
    IGlobalPermissionDao globalPermissionDao;

    /**
     * 对公客户信息查询
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page searchPublicClient(long uid, PublicClientRequest request, Pageable pageable) {
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE<>'110' and a.CUS_TYPE<>'120' and a.CUS_TYPE<>'130') ";
        List<String> strings = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            strings.add(String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            strings.add((String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if (!StringUtils.isEmpty(request.getPHONE())) {
            strings.add(String.format(" and a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if (!StringUtils.isEmpty(request.getCUST_MGR())) {
            strings.add(String.format(" and b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        //授权
        User user = userService.findUser(uid).orElse(null);
        if (null == user) {
            throw new RuntimeException();
        }
        List<String> managerCode = new ArrayList<>();
        List<String> userCode = new ArrayList<>();
        if (!user.isSu()) {
            //查询用户所持有的所有授权
            List<GlobalPermission> ps = globalPermissionDao.getPermissionsByUser(Collections.singleton(GlobalPermission.Type.DATA_SEARCH_CONDITION), uid);
            List<Quarters> qs = user.getQuarters();
            ps.stream().filter(p -> null != p.getDescription())
                    .map(p -> ((JSONArray) p.getDescription()).toJavaList(SearchConditionRule.class))
                    .flatMap(List::stream)
                    .filter(rule -> rule.getTargetType().equals(SearchTargetType.PUBLIC_CLIENT))
                    .forEach(rule -> {
                        switch (rule.getSearchType()) {
                            case FOR_DEPARTMENT:
                                switch (rule.getValueType()) {
                                    case BIND_VALUE:
                                        managerCode.addAll(qs.stream().map(q -> initString(q.getDepartment().getAccCode())).collect(Collectors.toList()));
                                        break;

                                    case CUSTOM:
                                        managerCode.add(initString(rule.getValue()));
                                        break;
                                }
                                break;

                            case FOR_USER:
                                switch (rule.getValueType()) {
                                    case BIND_VALUE:
                                        userCode.add(initString(user.getAccCode()));
                                        break;

                                    case CUSTOM:
                                        userCode.add(initString(rule.getValue()));
                                        break;
                                }
                                break;
                        }
                    });

        }
        List<String> limitCondition = new ArrayList<>();
        if(managerCode.size() > 0){
            limitCondition.add(String.format(" b.MAIN_BR_ID in (%s)", joinIn(managerCode)));
        }
        if(userCode.size() > 0){
            limitCondition.add(String.format(" b.CUST_MGR in (%s)", joinIn(userCode)));
        }
        if (strings.size() > 0) {
            sql += StringUtils.join(strings.toArray(), " ");
        }
        if(limitCondition.size() > 0){
            sql += " and (" + StringUtils.join(limitCondition.toArray(), " or ") + ")";
        }
        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 对私客户信息查询
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page searchPrivateClient(PrivateClientRequest request, Pageable pageable) {
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a LEFT JOIN CUS_INDIV as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE='110' or a.CUS_TYPE='120' or a.CUS_TYPE='130') ";
        List<String> strings = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            strings.add(String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            strings.add((String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if (!StringUtils.isEmpty(request.getPHONE())) {
            strings.add(String.format(" and a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if (!StringUtils.isEmpty(request.getCUST_MGR())) {
            strings.add(String.format(" and b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        if (!StringUtils.isEmpty(request.getCERT_CODE())) {
            strings.add(String.format(" and a.CERT_CODE like %%%s%%", request.getCERT_CODE()));
        }
        //授权
        sql += StringUtils.join(strings.toArray(), " ");
        log.info(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Map searchClientBase(String CUS_ID){
        String sql = "select CUS_ID,CUS_NAME,PHONE,CERT_TYPE,CERT_CODE from CUS_BASE where CUS_ID = ? ";
        List<Map<String, String>> rs = sqlUtils.query(sql, Collections.singleton(CUS_ID));
        if(rs.size() > 0){
            return rs.get(0);
        }
        return new HashMap();
    }

    /**
     * 贷款台账查询
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page searchAccLoan(AccloanRequest request, Pageable pageable) {
        String sql = "select a.BILL_NO,a.CONT_NO,a.LOAN_ACCOUNT,a.CUS_ID,a.CUS_NAME,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT,a.LOAN_BALANCE,a.REPAYMENT_MODE,a.CLA,a.CUS_MANAGER,a.MAIN_BR_ID from ACC_LOAN a ";
        if(null != request.register){
            sql += " ,t_workflow_instance ins";
        }
        sql += " where 1 = 1";
        if(null != request.register){
            sql += " and ins.model_name = '不良资产登记' ";
            sql += " and (select count(*) from t_workflow_instance_attribute attr where attr.instance_id = ins.id and attr.attr_key = 'BILL_NO' and attr.attr_value <> '')";
            if(request.register){
                sql += " > 0";
            }
            else{
                sql += " = 0";
            }
        }
        List<String> strings = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getBILL_NO())) {
            strings.add(String.format(" and BILL_NO like '%%%s%%'", request.getBILL_NO()));
        }
        if (!StringUtils.isEmpty(request.getCONT_NO())) {
            strings.add(String.format(" and CONT_NO like '%%%s%%'", request.getCONT_NO()));
        }
        if (!StringUtils.isEmpty(request.getLOAN_ACCOUNT())) {
            strings.add(String.format(" and LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT()));
        }
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            strings.add(String.format(" and CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            strings.add(String.format(" and CUS_NAME like '%%%s%%'", request.getCUS_NAME()));
        }
        sql += StringUtils.join(strings.toArray(), " ");
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchAccLoanData(AccloanRequest request, Pageable pageable) {
        String sql = "select a.BILL_NO,a.CONT_NO,a.LOAN_ACCOUNT,a.CUS_ID,a.CUS_NAME,a.ASSURE_MEANS_MAIN,a.LOAN_AMOUNT,a.LOAN_BALANCE,a.REPAYMENT_MODE,a.CLA,a.CUS_MANAGER,a.MAIN_BR_ID,a.CUS_TYPE from ACC_LOAN a where 1 = 1";
        List<String> strings = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getBILL_NO())) {
            strings.add(String.format(" and a.BILL_NO like '%%%s%%'", request.getBILL_NO()));
        }
        if (!StringUtils.isEmpty(request.getCONT_NO())) {
            strings.add(String.format(" and a.CONT_NO like '%%%s%%'", request.getCONT_NO()));
        }
        if (!StringUtils.isEmpty(request.getLOAN_ACCOUNT())) {
            strings.add(String.format(" and a.LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT()));
        }
        if (!StringUtils.isEmpty(request.getCUS_ID())) {
            strings.add(String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if (!StringUtils.isEmpty(request.getCUS_NAME())) {
            strings.add(String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME()));
        }
        sql += StringUtils.join(strings.toArray(), " ");
        return sqlUtils.pageQuery(sql, pageable);
    }


    public Page searchCusComManager(String cusId, Pageable pageable){
        String sql = String.format("select a.CUS_ID,a.COM_MRG_NAME,a.COM_MRG_CERT_TYP,a.COM_MRG_CERT_CODE,a.COM_MRG_DUTY,a.COM_MRG_EDT,a.COM_MRG_PHN,a.COM_MRG_ADRR from CUS_COM_MANAGER as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where a.CUS_ID='%s'", cusId);
        return sqlUtils.pageQuery(sql,pageable);
    }

    public Page searchComAddr(String cusId, Pageable pageable){
        String sql = String.format("select a.SEQ,a.COM_ADDR_TYP,a.COM_ADDR,a.COM_PHN_CODE,a.COM_FAX_CODE from CUS_COM_CONT as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where a.CUS_ID='%s'", cusId);
        return sqlUtils.pageQuery(sql,pageable);
    }

    public Page searchCusInDiv(String cusId, Pageable pageable){
        String sql = String.format("select a.INDIV_DEPOSITS,a.INDIV_SUR_YEAR,a.INDIV_ANN_INCM from CUS_INDIV_INCOME as a left join CUS_INDIV as b on a.CUS_ID=b.CUS_ID where a.CUS_ID='%s'", cusId);
        return sqlUtils.pageQuery(sql,pageable);
    }

    public Page searchCrtLoan(String contNo, Pageable pageable){
        String sql = String.format("select CONT_NO,SERNO,CONT_TYPE,CONT_AMT,AVAIL_AMT,LOAN_START_DATE,LOAN_END_DATE,CONT_STATE from CTR_LOAN_CONT where CONT_NO='%s'", contNo);
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchGrtGuar(String contNo, Pageable pageable){
        String sql = "select a.CONT_NO,b.GUAR_CONT_NO,b.GUAR_CONT_TYPE,b.GUAR_WAY,b.BORROWER_RELATION,b.GUAR_NO,b.GUAR_NAME,b.GUAR_AMT,b.GUAR_START_DATE,b.GUAR_END_DATE,b.GUAR_CONT_STATE from GRT_LOANGUAR_INFO as a left join GRT_GUAR_CONT as b on a.GUAR_CONT_NO=b.GUAR_CONT_NO where a.CONT_NO='%s'";
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchGRTGBasicInfo(String contNo, Pageable pageable){
        String sql = String.format("select a.GUARANTY_ID,a.CUS_ID,a.CUS_NAME,a.GAGE_TYPE,a.GAGE_NAME,a.RIGHT_CERT_TYPE_CODE,a.EVAL_AMT,a.MORTAGAGE_RATE,a.MAX_MORTAGAGE_AMT,a.AREA_LOCATION,a.USED_AMT,a.STATUS_CODE,a.DEPOT_STATUS from GRT_LOANGUAR_INFO as b left join GRT_GUARANTY_RE as c on b.SERNO=c.SER_NO left join GRT_G_BASIC_INFO as a on c.GUARANTY_ID=a.GUARANTY_ID left join ACC_LOAN as d on b.CONT_NO=d.CONT_NO where b.CONT_NO='%s'", contNo);
        return sqlUtils.pageQuery(sql, pageable);
    }


    public List searchCUS_COM(String cusId){
        String sql = "select * from CUS_COM where CUS_ID = ? ";
        return sqlUtils.query(sql, Collections.singleton(cusId));
    }

    public List searchCUS_INDIV(String cusId){
        String sql = "select * from CUS_INDIV where CUS_ID = ?";
        return sqlUtils.query(sql, Collections.singleton(cusId));
    }

    public List searchACC_LOAN(String billNo){
        String sql = "select * from ACC_LOAN where BILL_NO = ?";
        return sqlUtils.query(sql, Collections.singleton(billNo));
    }

    /********* 报表 **********/
    public Page searchYQYSBJDQ(YQYSBJDQRequest request, Pageable pageable){
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, LOAN_BALANCE\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1 ";
        if(!StringUtils.isEmpty(request.getSTART_DATE())){
            sql += (String.format(" and LOAN_END_DATE >= '%s'", request.getSTART_DATE()));
        }
        if(!StringUtils.isEmpty(request.getEND_DATE())){
            sql += (String.format(" and LOAN_END_DATE <= '%s'", request.getEND_DATE()));
        }
        if(!StringUtils.isEmpty(request.getPRD_TYPE())){
            sql += (String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE()));
        }
        if(!StringUtils.isEmpty(request.getLOAN_NATURE())){
            sql += (String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN()));
        }
        if(!StringUtils.isEmpty(request.getACCOUNT_STATUS())){
            sql += (String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS()));
        }
        return sqlUtils.pageQuery(sql,pageable);
    }

    public Page searchYQYSLX(YQYSLXRequest request,Pageable pageable){
        String sql = "SELECT a1.BILL_NO,a1.CONT_NO,a1.CUS_ID,a1.CUS_NAME,a1.LOAN_BALANCE * a1.REALITY_IR_Y / 12 * (\n" +
                " TO_DAYS(Least(a1.LOAN_END_DATE, '%s')) - TO_DAYS(GREATEST(a1.LOAN_START_DATE, '%s'))\n" +
                " ) / 30 as INT_CUMU\n" +
                "FROM ACC_LOAN a1\n" +
                "WHERE 1 = 1 ";
        sql = String.format(sql, request.getEND_DATE(), request.getSTART_DATE());
        if(!StringUtils.isEmpty(request.getPRD_TYPE())){
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if(!StringUtils.isEmpty(request.getLOAN_NATURE())){
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if(!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())){
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if(!StringUtils.isEmpty(request.getACCOUNT_STATUS())){
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchYuQYSBJ(YuQYSBJRequest request, Pageable pageable){
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, UNPD_PRIN_BAL\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1";
        if(!StringUtils.isEmpty(request.getPRD_TYPE())){
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if(!StringUtils.isEmpty(request.getLOAN_NATURE())){
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if(!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())){
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if(!StringUtils.isEmpty(request.getACCOUNT_STATUS())){
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchYuQYSLX(YuQYSLXRequest request, Pageable pageable){
        String sql = "SELECT BILL_NO, CONT_NO, CUS_ID, CUS_NAME, OVERDUE_RECE_INT,DELAY_INT_CUMU,UNPD_ARR_PRN_BAL,ACT_ARR_PRN_BAL\n" +
                "FROM ACC_LOAN\n" +
                "WHERE 1 = 1 ";
        if(!StringUtils.isEmpty(request.getPRD_TYPE())){
            sql += String.format(" and PRD_TYPE = '%s'", request.getPRD_TYPE());
        }
        if(!StringUtils.isEmpty(request.getLOAN_NATURE())){
            sql += String.format(" and LOAN_NATURE = '%s'", request.getLOAN_NATURE());
        }
        if(!StringUtils.isEmpty(request.getASSURE_MEANS_MAIN())){
            sql += String.format(" and ASSURE_MEANS_MAIN = '%s'", request.getASSURE_MEANS_MAIN());
        }
        if(!StringUtils.isEmpty(request.getACCOUNT_STATUS())){
            sql += String.format(" and ACCOUNT_STATUS = '%s'", request.getACCOUNT_STATUS());
        }
        return sqlUtils.pageQuery(sql, pageable);
    }



    /**
     * 设置查找条件授权
     *
     * @param request
     * @return
     */
    public boolean setPermissions(
            GlobalPermissionEditRequest request
    ) {
        //清空授权
        userService.deleteGlobalPermissionByTypeAndObjectId(request.getType(), 0);
        List rules;
        switch (request.getType()) {
            case DATA_SEARCH_CONDITION:
                rules = request.getArray().toJavaList(SearchConditionRule.class);
                userService.addGlobalPermission(request.getType(), 0, request.getUserType(), request.getLinkIds(), rules);
                break;

            case DATA_SEARCH_RESULT:
                rules = request.getArray().toJavaList(SearchResultRule.class);
                userService.addGlobalPermission(request.getType(), 0, request.getUserType(), request.getLinkIds(), rules);
                break;
        }
        return true;
    }

    /**
     * 授权查询
     *
     * @return
     */
    public Optional<GlobalPermission> getPermission(GlobalPermission.Type type, GlobalPermission.UserType userType, Long linkId) {
        List limitList = ImmutableList.of(
                GlobalPermission.Type.DATA_SEARCH_CONDITION,
                GlobalPermission.Type.DATA_SEARCH_RESULT
        );
        if(!limitList.contains(type)){
            return Optional.empty();
        }
        return userService.getGlobalPermission(type,0,userType,linkId);
    }

    private String initString(String str) {
        return null == str ? "" : str;
    }
    private String joinIn(List<String> list){
        list =  list.stream().map(str -> "'" + str + "'").collect(Collectors.toList());
        return StringUtils.join(list.toArray(), ",");
    }


    @Data
    public static class PublicClientRequest {
        String CUS_ID;
        String CUS_NAME;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class PrivateClientRequest {
        String CUS_ID;
        String CUS_NAME;
        String CERT_CODE;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class AccloanRequest {
        String BILL_NO;
        String CONT_NO;
        String LOAN_ACCOUNT;
        String CUS_ID;
        String CUS_NAME;

        Boolean register;
    }

    @Data
    public static class YQYSBJDQRequest{
        String START_DATE;
        String END_DATE;

        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }
    @Data
    public static class YQYSLXRequest{
        @NotEmpty
        String START_DATE;
        @NotEmpty
        String END_DATE;
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }
    @Data
    public static class YuQYSBJRequest{
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }
    @Data
    public static class YuQYSLXRequest{
        String PRD_TYPE;
        String LOAN_NATURE;
        String ASSURE_MEANS_MAIN;
        String ACCOUNT_STATUS;
    }

    @Data
    public static class SearchConditionRule {
        //查询规则
        SearchType searchType;
        //授权对象
        SearchTargetType targetType;
        //约束类型
        SearchValueType valueType;
        //约束值
        String value;
    }

    @Data
    public static class SearchResultRule {
        //授权对象
        SearchTargetType searchTargetType;
        //限制字段
        String value;
    }


    public enum SearchType {
        FOR_DEPARTMENT,
        FOR_USER
    }

    public enum SearchTargetType {
        PRIVATE_CLIENT,
        PUBLIC_CLIENT,
        ACC_LOAN,
        ACC_LOAN_DATA
    }

    public enum SearchValueType {
        BIND_VALUE,
        CUSTOM
    }

}
