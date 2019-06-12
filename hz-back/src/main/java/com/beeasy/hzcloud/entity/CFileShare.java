package com.beeasy.hzcloud.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.S;

import java.util.Date;
import java.util.Map;

@Table(name = "C_FILE_SHARE")
@Getter
@Setter
public class CFileShare extends ValidGroup {

    @AssignID("simple")
    private Long id;

    long fid;
    long fromUid;
    long toUid;

    Date lastModify;


    @Override
    public Object onGetList(SQLManager sqlManager, Map<String, Object> params) {
        JSONObject object = (JSONObject) JSON.toJSON(params);
        object.put("uid", AuthFilter.getUid());
        String pid = object.getString("pid");
        if(S.notEmpty(pid) && S.neq(pid, "0")){
            return U.beetlPageQuery("cloud.我的分享列表2", JSONObject.class, object);
        }
        return U.beetlPageQuery("cloud.我的分享列表",JSONObject.class, object);
    }
}
