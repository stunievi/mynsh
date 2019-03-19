package com.beeasy.hzback.entity;

import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;
import java.util.Map;

@Table(name = "T_SHORT_MESSAGE_LOG")
@Getter
@Setter
public class ShortMsg extends TailBean implements ValidGroup {
    private Long id;
    private String phone;
    private String message;
    private Date addTime;
    private Integer state;


    /*****/

    @Override
    public String onGetListSql(Map<String,Object> params) {
        User.AssertMethod("系统管理.日志管理.短信发送历史");
        return "system.查询短信发送历史列表";
    }


}
