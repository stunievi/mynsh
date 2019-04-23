package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;
import java.util.Map;

@Table(name = "T_QCC_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QCCLog extends TailBean implements ValidGroup {

    @AssignID("simple")
    Long id;

    Long content;
    String operator;
    String type;
    Date addTime;

    @Override
    public String onGetListSql(Map<String, Object> params) {
        return "qcc.企查查日志查询";
    }


    public Object cusCom(SQLManager sqlManager, JSONObject object){
        return U.beetlPageQuery("qcc.对公客户", JSONObject.class, object);
    }


}
