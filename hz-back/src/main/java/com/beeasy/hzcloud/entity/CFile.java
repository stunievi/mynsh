package com.beeasy.hzcloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.$;
import org.osgl.util.S;

@Table(name = "C_FILE")
@Getter
@Setter
public class CFile extends TailBean implements ValidGroup {
    @AssignID("simple")
    Long id;

    //父文件夹ID
    Long pid;

    String name;

    //文件还是文件夹
    Type type;

    //预览文件目录
    String preview;


    public enum Type{
        //文件夹
        DIR,
        //文件
        FILE;
    }


    public Object createDir(SQLManager sqlManager, JSONObject object){
        CFile file = object.toJavaObject(getClass());
        Assert(S.notEmpty(file.getName()), "文件夹名不能为空");

        //如果传递了路径
        String path = object.getString("path");
        if(S.notEmpty(object.getString("path"))){

        }
        return null;
    }
}
