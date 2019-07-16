package com.beeasy.hzback.entity;


import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.S;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Table(name = "T_LOAN_RELATED_SEARCH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRelatedSearch extends ValidGroup {

    @AssignID("simple")
    Long id;
    String cusName;
    String certType;
    String certCode;
    String operator;
    String mainById;
    Date addTime;
    Double acceptAmt;
    String result;
    Double ratio;

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        Date nowDate = new Date();
        long uid = AuthFilter.getUid();
        object.put("uid", uid);
        String cusName = object.getString("CUS_NAME");
        String certCode = object.getString("CERT_CODE");
        double acceptAmt = object.getDoubleValue("ACCEPT_AMT");

        SysVar sysNetCapital = sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName, "NET_CAPITAL").single();
        long tetCapital = Long.parseLong(sysNetCapital.getVarValue());
        BigDecimal b = new BigDecimal(acceptAmt/tetCapital);
        double ratioVal = b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        switch (action){
            case "insert":
                long count = sqlManager.lambdaQuery(LoanRelatedSearch.class)
                        .andEq(LoanRelatedSearch::getOperator, uid)
                        .andEq(LoanRelatedSearch::getCertCode, certCode)
                        .count();
                Assert(count == 0 , "已存在相同证件号!");

                JSONObject permission = sqlManager.selectSingle("user.是否拥有客户经理或办事员权限",  object, JSONObject.class);
//                Assert(null != permission  && permission .getInteger("1") > 0 , "您没有权限添加!");
                Assert(S.noBlank(cusName) , "客户名称不能为空!");
                Assert(S.noBlank(certCode) , "证件号不能为空!");
                Assert(acceptAmt > 0 , "受理金额不能小于0!");

                LoanRelatedSearch insertSearch = new LoanRelatedSearch();
                insertSearch.setAddTime(nowDate);
                insertSearch.setOperator(object.getString("uid"));
                insertSearch.setCusName(cusName);
                insertSearch.setCertCode(certCode);
                insertSearch.setRatio(ratioVal);
                insertSearch.setAcceptAmt(acceptAmt);
                sqlManager.insert(insertSearch, true);
                Log.log("新增贷前关联人查询: %s, %s", cusName, certCode);
                return true;
            case "getDList":
//                User.AssertMethod("系统管理.日志管理.关联方查询日志");
                object.put("uid", 1);
                return U.beetlPageQuery("link.loan_related_search_log", JSONObject.class, object);
        }
        return null;
    }


}
