package com.beeasy.hzback.modules.system.task;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TaskRunner {

    @Autowired
    @Qualifier(value = "sqlManagers")
    Map<String, SQLManager> sqlManagers;

    @Autowired
    private SQLManager sqlManager;

    /**
     * 定时清理登录令牌(每天失效)
     */
    @Scheduled(cron = "0 10 0 ? * *")
    public void clearTokens(){
        sqlManagers.forEach((s, sqlManager) -> {
            sqlManager.lambdaQuery(UserToken.class).delete();
        });
    }

    /**
     * @Author gotomars
     * @Description 功能实现：定时任务脚本，遍历所有符合条件的贷款台账的贷款合同金额、客户名称、证件号码与贷前关联方查询录入的受理金额、客户名称、证件号码一致时，更新贷款台账的“贷前关联查询”字段值为01
     * @Date 16:01 2019/7/30
     **/
//    @Scheduled(cron = "0 10 1 * * ?")
//    public void updateLoanLinkSearchState(){
//        JSONObject cond = new JSONObject();
//        List<JSONObject> list = sqlManager.select("accloan.xxx", JSONObject.class, cond);
//        list.forEach(item->{
//            JSONObject loan = (JSONObject) item;
//            sqlManager.update("accloan.xxx", C.newMap(
//                    "", "",
//                    "CUS_NAME", loan.getString("CUS_NAME"),
//                    "CERT_CODE", loan.getString("CERT_CODE")
//
//            ));
//        });
//    }
}
