package com.beeasy.hzback.entity;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Data;
import org.apache.activemq.command.ActiveMQQueue;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Table(name = "T_QUAL_CUS")
@Data
public class QualCus extends ValidGroup {

    @AssignID("simple")
    Long id;
    Date addTime;
    String cusName;
    String cusType;
    String certType;
    String certCode;
    String companyName;
    long operator;

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        Date nowDate = new Date();
        long uid = AuthFilter.getUid();
        object.put("uid", uid);
        String cusType = object.getString("CUS_TYPE");
        String cusName = Optional.ofNullable(object.getString("CUS_NAME")).get().trim();
        String companyName = Optional.ofNullable(object.getString("COMPANY_NAME")).get().trim();
        switch (action) {
            case "insert":
                String requestSign = "06,08";
                Assert(S.noBlank(cusName) , "客户名称不能为空!");
                Assert(S.noBlank(cusType) && C.list("01", "02").contains(cusType), "客户类型不存在!");
                if(null!=cusType && "02".equals(cusType)){
                    requestSign = "07";
                    Assert(S.noBlank(companyName), "对私客户辅助公司名不能为空!");
                }

                QualCus insertData = new QualCus();
                long id = IdUtil.createSnowflake(0,0).nextId();
                insertData.setId(id);
                insertData.setCusName(cusName);
                insertData.setAddTime(nowDate);
//                insertData.setCusType("");
//                insertData.setCertCode("");
                insertData.setOperator(uid);
                insertData.setCusType(cusType);
                insertData.setCompanyName(companyName);
                sqlManager.insert(insertData);

                // 触发MQ获取企查查信息
                JmsMessagingTemplate jmsMessagingTemplate = U.getBean(JmsMessagingTemplate.class);
                ActiveMQQueue mqQueue = new ActiveMQQueue("qcc-company-infos-request");
                JSONObject requestData = new JSONObject();
                SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
                String StrID = "fzsys_" + df1.format(nowDate);
                requestData.put("uid", uid);
                JSONObject user = new JSONObject();
                user.put("OrderId", StrID);
                if("07".equals(requestSign)){
                    user.put("Content1", cusName);
                    user.put("Content2", companyName);
                }else{
                    user.put("Content1", cusName);
                    user.put("Content2", "");
                }
                user.put("Sign", requestSign);
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(user);
                requestData.put("OrderData", jsonArray);
                jmsMessagingTemplate.convertAndSend(mqQueue, requestData.toString());

                Log.log("新增"+cusType=="01"?"对公":"对私"+"客户资质: %s, %s", cusName, companyName);
                return true;
            case "getDList":
                PageQuery<JSONObject> dataList = U.beetlPageQuery("link.查询资质客户", JSONObject.class, object);
                return dataList;
        }
        return null;
    }

}
