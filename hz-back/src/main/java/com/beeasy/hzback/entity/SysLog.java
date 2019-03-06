package com.beeasy.hzback.entity;

import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

@Table(name = "T_SYSTEM_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysLog extends TailBean implements ValidGroup {
    @AssignID("simple")
    Long id;

    Long   userId;
//    String userName;
    String controller;
    String method;
    Date   addTime;


    /******/

    @Override
    public String onGetListSql(Map<String,Object> params) {
        User.AssertMethod("系统管理.日志管理.系统日志");
        return "system.查询系统日志";
    }


}
