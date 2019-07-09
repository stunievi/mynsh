package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.entity.WfInsAttr;
import com.beeasy.hzback.entity.WfNodeDealer;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Async
@Service
@Transactional
public class TaskSyncService {

    @Autowired
    SQLManager sqlManager;


    public void sync(WfIns wfIns){
        //查询余下的贷款
        List<JSONObject> list = sqlManager.execute(new SQLReady(String.format("SELECT loan_account, bill_no from RPT_M_RPT_SLS_ACCT where cont_no = (select cont_no from RPT_M_RPT_SLS_ACCT where loan_account = '%s')", wfIns.getLoanAccount())), JSONObject.class);
        long id = wfIns.getId();
        for (JSONObject jsonObject : list) {
            String la = jsonObject.getString("loanAccount");
            String bo = jsonObject.getString("billNo");
            if(la.equals(wfIns.getLoanAccount())){
                continue;
            }
            wfIns.setId(null);
            wfIns.setLoanAccount(la);
            wfIns.setBillNo(bo);
            //主体
            sqlManager.insert(wfIns, true);
            long nid = wfIns.getId();
            //属性
            List<WfInsAttr> attrs = sqlManager.lambdaQuery(WfInsAttr.class)
                    .andEq(WfInsAttr::getInsId, id)
                    .select()
                    .stream()
                    .peek(e -> {
                        e.setId(null);
                        e.setInsId(nid);
                        if(e.getAttrKey().equalsIgnoreCase("LOAN_ACCOUNT")){
                            e.setAttrValue(la);
                        }
                        if(e.getAttrKey().equalsIgnoreCase("BILL_NO")){
                            e.setAttrValue(bo);
                        }
                    })
                    .collect(Collectors.toList());
            sqlManager.insertBatch(WfInsAttr.class, attrs);
            //处理人
            List<WfNodeDealer> dealers = sqlManager.lambdaQuery(WfNodeDealer.class)
                    .andEq(WfNodeDealer::getInsId, id)
                    .select()
                    .stream()
                    .peek(e -> {
                        e.setId(null);
                        e.setInsId(nid);
                    })
                    .collect(Collectors.toList());
            sqlManager.insertBatch(WfNodeDealer.class, dealers);
        }

    }

}
