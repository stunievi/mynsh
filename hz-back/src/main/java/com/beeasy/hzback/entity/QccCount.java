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

@Table(name="T_QCC_COUNT")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QccCount extends TailBean implements ValidGroup {
    @AssignID("simple")
    Long id;
    int count;

    String ifNameEN;
    String ifNameCH;
    String orderID;
    String dataID;

    Date addTime;

    public Object queryQccCount(SQLManager sqlManager, JSONObject object){
        return U.beetlPageQuery("qcc.查询企查查接口调用次数", JSONObject.class, object);
    }
}
