package com.beeasy.hzcloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.$;
import org.osgl.util.S;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "C_FILE")
@Getter
@Setter
public class CFile extends TailBean implements ValidGroup {
    @AssignID("simple")
    Long id;

    //父文件夹ID
    Long pid;
    Long uid;

    String name;

    //文件还是文件夹
    Type type;

    //文件绝对路径
    String path;

    //预览文件目录
    String preview;


    public enum Type{
        //文件夹
        DIR,
        //文件
        FILE;
    }


    public Object createDir(SQLManager sqlManager, JSONObject object){
        long uid = AuthFilter.getUid();
        CFile file = object.toJavaObject(getClass());
        Assert(S.notEmpty(file.getName()), "文件夹名不能为空");

        CFile ret;
        //如果传递了父目录, 从父目录开始找
        if(null != pid){
            CFile dir = sqlManager.lambdaQuery(CFile.class)
                .andEq(CFile::getUid, uid)
                .andEq(CFile::getType, Type.DIR)
                .andEq(CFile::getId, pid)
                .single();
            path = dir.getPath() + path;
        }

        //如果传递了路径

//        CFile dir = sqlManager.lambdaQuery(CFile.class)
//            .andEq(CFile::getPath, path)
//            .
//        name = name.trim();
//        List<String> ps = Arrays.stream(name.split("/")).collect(Collectors.toList());
//        if(ps.size() > 1){
//            List<CFile> files = null;
//            for(int i = ps.size() - 2; i >= 0; i--){
//                if(null == files){
//                    files = sqlManager.lambdaQuery(CFile.class)
//                        .andEq(CFile::getUid, uid)
//                        .andEq(CFile::getType, Type.DIR)
//                        .andEq(CFile::getName, ps.get(i).trim())
//                        .select();
//                }
//                else{
//                    files = files.stream()
//                        .map(item -> sqlManager.lambdaQuery(CFile.class).andEq(CFile::getId, ))
//                        .
//                }
//            }
//        }
//        else{
//            ret = sqlManager.lambdaQuery(CFile.class)
//                .andEq(CFile::getName, name)
//                .andEq(CFile::getUid, uid)
//                .andEq(CFile::getPid, pid)
//                .single();
//            if(null != ret){
//                return ret;
//            }
//            else{
//
//            }
//            //检查是否有同名目录
//        }
        return null;
    }



    @Override
    public boolean equals(Object obj) {
        return (null != obj && hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return (id + "").hashCode();
    }
}
