package com.beeasy.hzcloud.entity;

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
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Table(name = "C_FILE")
@Getter
@Setter
public class CFile extends TailBean implements ValidGroup {
    @AssignID("simple")
    Long id;

    //父文件夹ID
    long pid;
    long uid;
    long creator;

    String name;

    //文件还是文件夹
    Type type;

    //文件绝对路径
    String path;

    //文件拓展
    String ext;

    long size;

    Date lastModify;

    //预览文件目录
//    String preview;


    public enum Type{
        //文件夹
        DIR,
        //文件
        FILE;
    }

    private LinkedList<String> analyzePath(String path){
        Assert(S.notBlank(path));

        return S.split(path, "\\/")
                .stream()
                .map(S::trim)
                .filter(S::notEmpty)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private CFile getFile(SQLManager sqlManager, long uid, long pid , String path){
        return sqlManager.lambdaQuery(CFile.class)
                    .andEq(CFile::getUid, uid)
                    .andEq(CFile::getName, path)
                    .andEq(CFile::getPid, pid)
                    .single();
    }


    /**
     * mkdir
     * @param sqlManager
     * @param object
     * @return
     */
    public Object mkdir(SQLManager sqlManager, JSONObject object){
        long uid = AuthFilter.getUid();
        String err = "创建文件夹失败";
        Assert(S.notEmpty(object.getString("path")));
        LinkedList<String> ps = analyzePath(object.getString("path"));
        long pid = 0;
        CFile file = null;
        for (String p : ps) {
            //没有就创建
            file = getFile(sqlManager, uid, pid, p);
            if (file == null) {
                file = new CFile();
                file.setUid(uid);
                file.setCreator(uid);
                file.setType(Type.DIR);
                file.setName(p);
                file.setPid(pid);
                file.setLastModify(new Date());
                sqlManager.insert(file, true);
            }
            else{
                Assert(Objects.equals(file.getType(), Type.DIR), err);
            }
            pid = file.getId();
        }

        Assert($.isNotNull(file.getId()), err);
        return file;
    }


    public Object mv(SQLManager sqlManager, JSONObject object){

        return null;
    }

    public Object cp(SQLManager sqlManager, JSONObject object){
        return null;
    }

    /**
     *
     * @param sqlManager
     * @param object
     * @return
     */
    public Object rm(SQLManager sqlManager, JSONObject object){
        String err = "删除失败";
        List<JSONObject> objects = null;
        if(object.containsKey("path")){
            LinkedList<String> ps = analyzePath(object.getString("path"));
            long pid = 0;
            CFile file = null;
            for (String p : ps) {
                file = getFile(sqlManager, AuthFilter.getUid(), pid, p);
                Assert($.isNotNull(file), err);
                pid = file.getId();
            }
            objects = sqlManager.select("cloud.查询子文件ID", JSONObject.class, C.newMap("ids", C.newList(pid), "uid", AuthFilter.getUid()));
        }
        else if(object.containsKey("id")){
            List<Long> ids = U.toIdList(object.getString("id"));
            objects = sqlManager.select("cloud.查询子文件ID", JSONObject.class, C.newMap("ids", ids, "uid", AuthFilter.getUid()));
        }
        Assert($.isNotNull(object), err);
        List<String> _ids = objects.stream()
                .map(i -> i.getString("id"))
                .collect(Collectors.toList());
        sqlManager.lambdaQuery(CFile.class)
                .andIn(CFile::getId, _ids)
                .delete();
        return true;
    }





}
