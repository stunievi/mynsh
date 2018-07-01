package com.beeasy.hzback.modules.system.service;


import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.util.SqlUtils;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.form.GlobalPermissionEditRequest;
import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.objects.Global;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSearchService {

    @Autowired
    SqlUtils sqlUtils;
    @Autowired
    UserService userService;

    /**
     * 对公客户信息查询
     * @param request
     * @param pageable
     * @return
     */
    public Page searchPublicClient(PublicClientRequest request, Pageable pageable){
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE<>'110' and a.CUS_TYPE<>'120' and a.CUS_TYPE<>'130') ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format("a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add((String.format("a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if(!StringUtils.isEmpty(request.getPHONE())){
            strings.add(String.format("a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if(!StringUtils.isEmpty(request.getCUST_MGR())){
            strings.add(String.format("b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        //授权
        if(strings.size() > 0){
            sql += " and " + StringUtils.join(strings.toArray()," and ");
        }
        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 对私客户信息查询
     * @param request
     * @param pageable
     * @return
     */
    public Page searchPrivateClient(PrivateClientRequest request, Pageable pageable){
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a LEFT JOIN CUS_INDIV as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE='110' or a.CUS_TYPE='120' or a.CUS_TYPE='130') ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format(" and a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add((String.format(" and a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if(!StringUtils.isEmpty(request.getPHONE())){
            strings.add(String.format(" and a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if(!StringUtils.isEmpty(request.getCUST_MGR())){
            strings.add(String.format(" and b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        if(!StringUtils.isEmpty(request.getCERT_CODE())){
            strings.add(String.format(" and a.CERT_CODE like %%%s%%", request.getCERT_CODE()));
        }
        //授权
        sql += StringUtils.join(strings.toArray()," ");
        log.info(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    /**
     * 贷款台账查询
     * @param request
     * @param pageable
     * @return
     */
    public Page searchAccLoan(AccloanRequest request, Pageable pageable){
        String sql = "select BILL_NO,CONT_NO,LOAN_ACCOUNT,CUS_ID,CUS_NAME,ASSURE_MEANS_MAIN,LOAN_AMOUNT,LOAN_BALANCE,REPAYMENT_MODE,CLA,CUS_MANAGER,MAIN_BR_ID from ACC_LOAN where 1 = 1 and ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getBILL_NO())){
            strings.add(String.format(" and BILL_NO like '%%%s%%'", request.getBILL_NO()));
        }
        if(!StringUtils.isEmpty(request.getCONT_NO())){
            strings.add(String.format(" and CONT_NO like '%%%s%%'", request.getCONT_NO()));
        }
        if(!StringUtils.isEmpty(request.getLOAN_ACCOUNT())){
            strings.add(String.format(" and LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT()));
        }
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format(" and CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add(String.format(" and CUS_NAME like '%%%s%%'", request.getCUS_NAME()));
        }
        sql += StringUtils.join(strings.toArray(), " ");
        return sqlUtils.pageQuery(sql,pageable);
    }

    public Page searchAccLoanData(AccloanRequest request, Pageable pageable){
        String sql = "select BILL_NO,CONT_NO,LOAN_ACCOUNT,CUS_ID,CUS_NAME,ASSURE_MEANS_MAIN,LOAN_AMOUNT,LOAN_BALANCE,REPAYMENT_MODE,CLA,CUS_MANAGER,MAIN_BR_ID from ACC_LOAN where 1 = 1 and ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getBILL_NO())){
            strings.add(String.format(" and BILL_NO like '%%%s%%'", request.getBILL_NO()));
        }
        if(!StringUtils.isEmpty(request.getCONT_NO())){
            strings.add(String.format(" and CONT_NO like '%%%s%%'", request.getCONT_NO()));
        }
        if(!StringUtils.isEmpty(request.getLOAN_ACCOUNT())){
            strings.add(String.format(" and LOAN_ACCOUNT like '%%%s%%'", request.getLOAN_ACCOUNT()));
        }
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format(" and CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add(String.format(" and CUS_NAME like '%%%s%%'", request.getCUS_NAME()));
        }
        sql += StringUtils.join(strings.toArray(), " ");
        return sqlUtils.pageQuery(sql,pageable);
    }


    /**
     * 设置查找条件授权
     * @param requests
     * @return
     */
    public boolean setPermissions(
            GlobalPermissionEditRequest[] requests
    ){
        if(Arrays.stream(requests).map(item -> item.getType()).distinct().count() != 1){
            return false;
        }
        //清空授权
        userService.deleteGlobalPermissionByTypeAndObjectId(requests[0].getType(),0);
        List rules;
        for (GlobalPermissionEditRequest request : requests) {
            switch (request.getType()){
                case DATA_SEARCH_CONDITION:
                     rules = request.getArray().toJavaList(SearchConditionRule.class);
                    userService.addGlobalPermission(request.getType(),0, request.getUserType(), request.getLinkIds(), rules);
                    break;

                case DATA_SEARCH_RESULT:
                    rules = request.getArray().toJavaList(SearchResultRule.class);
                    userService.addGlobalPermission(request.getType(),0, request.getUserType(), request.getLinkIds(), rules);
                    break;
            }
        }
        return true;
    }

    /**
     * 授权查询
     * @param types
     * @return
     */
    public List<GlobalPermission> getPermissions(List<GlobalPermission.Type> types){
        List limitList = ImmutableList.of(
            GlobalPermission.Type.DATA_SEARCH_CONDITION,
            GlobalPermission.Type.DATA_SEARCH_RESULT
        );
        types = types.stream().filter(item -> limitList.contains(item)).collect(Collectors.toList());
        return userService.getGlobalPermissions(types,0);
    }

    @Data
    public static class PublicClientRequest{
        String CUS_ID;
        String CUS_NAME;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class PrivateClientRequest{
        String CUS_ID;
        String CUS_NAME;
        String CERT_CODE;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class AccloanRequest{
        String BILL_NO;
        String CONT_NO;
        String LOAN_ACCOUNT;
        String CUS_ID;
        String CUS_NAME;
    }

    @Data
    public static class SearchConditionRule{
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
    public static class SearchResultRule{
        //授权对象
        SearchTargetType searchTargetType;
        //限制字段
        String value;
    }


    public enum SearchType{
        FOR_DEPARTMENT,
        FOR_USER
    }
    public enum SearchTargetType{
        PRIVATE_CLIENT,
        PUBLIC_CLIENT,
        ACC_LOAN,
        ACC_LOAN_DATA
    }
    public enum SearchValueType{
        BIND_VALUE,
        CUSTOM
    }

}
