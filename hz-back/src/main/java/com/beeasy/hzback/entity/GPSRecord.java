package com.beeasy.hzback.entity;

import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import io.jsonwebtoken.lang.Assert;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.$;
import org.osgl.util.S;

import java.util.Date;
import java.util.Map;

@Table(name = "T_GPS_RECORD")
@Getter
@Setter
public class GPSRecord extends ValidGroup {
    @AssignID("simple")
    Long id;

    String lat;
    String lng;
    String address;
    Long uid;
    Date addTime;

    @Override
    public String onGetListSql(Map<String, Object> params) {
        params.put("uid", AuthFilter.getUid());
        return "gps.查看列表";
    }

    @Override
    public Object onAdd(SQLManager sqlManager) {
        Assert(S.notEmpty(lat), "");
        Assert(S.notEmpty(lng), "");
        Assert(S.notEmpty(address), "");

        addTime = new Date();
        uid = AuthFilter.getUid();

        sqlManager.insert(getClass(), this);
        return this;
    }
}

