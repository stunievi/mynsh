package com.beeasy.hzback.entity;


import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.modules.system.service.NoticeService2;
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
        double tetCapital = Long.parseLong(sysNetCapital.getVarValue());
        BigDecimal b = new BigDecimal(acceptAmt/tetCapital);
//        String ratioStrVal = b.setScale(6,   BigDecimal.ROUND_HALF_UP).toString();
        String ratioPercentVal = b.multiply(new BigDecimal(100)).setScale(6,   BigDecimal.ROUND_HALF_UP).toString() + "%";
        double ratioVal = b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
        switch (action){
            case "insert":
                NoticeService2 noticeService2 = U.getBean(NoticeService2.class);
//                JSONObject permission = sqlManager.selectSingle("user.是否拥有客户经理或办事员权限",  object, JSONObject.class);
//                Assert(null != permission  && permission .getInteger("1") > 0 , "您没有权限添加!");
                Assert(S.noBlank(cusName) , "客户名称不能为空!");
                Assert(S.noBlank(certCode) , "证件号不能为空!");
                Assert(acceptAmt > 0 , "受理金额不能小于0!");

                boolean find = sqlManager.lambdaQuery(RelatedPartyList.class).andEq(RelatedPartyList::getCertCode, certCode).count() > 0;
                if(find){
                    List<JSONObject> list = sqlManager.select("user.查询总行关联方风险角色", JSONObject.class, object);
                    for(JSONObject item : list){

                        noticeService2.addNotice(SysNotice.Type.SYSTEM, item.getLong("uid"), String.format("<a href=\"javascript:;\" class=\"loanRelatedSearch\" data-cert-code=\"%s\">客户：%s，证件号：%s</a>，属于关联方贷款，不得发放信用贷款，受理金额（%f万元）占资本净额的比例为%s", cusName, certCode, acceptAmt,  ratioPercentVal), null);
                    }
                    noticeService2.addNotice(SysNotice.Type.SYSTEM, AuthFilter.getUid(), String.format("<a href=\"javascript:;\" class=\"loanRelatedSearch\" data-cert-code=\"%s\">客户：%s，证件号：%s</a>，属于关联方贷款，不得发放信用贷款，受理金额（%f万元）占资本净额的比例为%s", certCode, cusName, certCode, acceptAmt, ratioPercentVal), null);
                }else{
                    noticeService2.addNotice(SysNotice.Type.SYSTEM, AuthFilter.getUid(), String.format("客户：%s，证件号：%s，不是关联方贷款，受理金额（%f万元）占资本净额的比例为：%s", cusName, certCode, acceptAmt, ratioPercentVal), null);
                }

                LoanRelatedSearch insertSearch = new LoanRelatedSearch();
                insertSearch.setAddTime(nowDate);
                insertSearch.setOperator(object.getString("uid"));
                insertSearch.setCusName(cusName);
                insertSearch.setCertCode(certCode);
                insertSearch.setRatio(ratioVal);
                insertSearch.setResult(find ? "01": "02");
                insertSearch.setAcceptAmt(acceptAmt);
                sqlManager.insert(insertSearch, true);
                Log.log("新增贷前关联人查询: %s, %s", cusName, certCode);
                return find;
            case "getDList":
//                User.AssertMethod("系统管理.日志管理.关联方查询日志");
                return U.beetlPageQuery("link.loan_related_search", JSONObject.class, object);
        }
        return null;
    }


}
