package com.beeasy.hzback.entity;

import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;
import java.util.Map;

@Table(name = "T_QCC_RISK")
@Data
public class QccRisk extends ValidGroup {

    @AssignID("simple")
    Long id;

    Date addTime;
    String cusId;
    String cusName;
    String certCode;
    String riskInfo;
    String mainBrId;
    String custMgr;

    @Override
    public String onGetListSql(Map<String, Object> params) {
        params.put("uid", AuthFilter.getUid());
        return "accloan.企查查风险信息查询";
    }
}
