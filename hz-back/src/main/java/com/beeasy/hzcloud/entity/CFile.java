package com.beeasy.hzcloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
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
import org.beetl.sql.core.query.LambdaQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Table(name = "C_FILE")
@Getter
@Setter
public class CFile extends ValidGroup {
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
    String tags;

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


    @Override
    public String onGetListSql(Map<String, Object> params) {
        params.put("uid", AuthFilter.getUid());
        if(!params.containsKey("pid")) params.put("pid", "0");
        return "cloud.文件列表";
    }

    //    private LinkedList<String> analyzePath(String path){
//        if(S.blank(path)) path = "";
//        return S.split(path, "\\/")
//                .stream()
//                .map(S::trim)
//                .filter(S::notEmpty)
//                .collect(Collectors.toCollection(LinkedList::new));
//    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    private static class Holder{
        public String name;
        public CFile file;
    }

    /**
     * 拿到文件树
     * @param sqlManager
     * @param path
     * @return
     */
    private LinkedList<Holder> analyzePath(SQLManager sqlManager, long uid, String path){
        if(S.blank(path)) path = "";
        AtomicLong pid = new AtomicLong(0);
        AtomicBoolean allnull = new AtomicBoolean(false);
        LinkedList<Holder> files = new LinkedList<>();
        S.split(path, "\\/")
                .stream()
                .map(S::trim)
                .filter(S::notEmpty)
                .forEachOrdered(i -> {
                    if(!allnull.get()){
                        CFile file = getFile(sqlManager, uid, pid.get(), i);
                        if (file == null) {
                            allnull.set(true);
                            files.add(new Holder(i, null));
                        }
                        else{
                            files.add(new Holder(i, file));
                            pid.set(file.getId());
                        }
                    }
                    else {
                        files.add(new Holder(i, null));
                    }
                });
        Assert(files.size() > 0);
        return files;
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
        Long uid = AuthFilter.getUid();
        String err = "创建文件夹失败";
        if(object.containsKey("path")){
            Assert(S.notEmpty(object.getString("path")), err);
            LinkedList<Holder> ps = analyzePath(sqlManager, uid, object.getString("path"));
            long pid = 0;
            CFile file = null;
            for(short i = 0; i < ps.size(); i++){
                Holder holder = ps.get(i);
                if (holder.getFile() == null) {
                    file = new CFile();
                    file.setUid(uid);
                    file.setCreator(uid);
                    file.setType(Type.DIR);
                    file.setName(holder.name);
                    file.setPid(pid);
                    file.setLastModify(new Date());
                    sqlManager.insert(file, true);
                    holder.file = file;
                }
                pid = holder.file.getId();
            }
            Assert($.isNotNull(ps.getLast().file.id), err);
            return ps.getLast().file;
        }
        else if(object.containsKey("pid")){
            Assert(S.notEmpty(object.getString("pid")), err);
            Assert(S.notEmpty(object.getString("name")), err);
            //检查是否存在相同的目录
            long pid = object.getLong("pid");
            if(pid > 0){
                long count = sqlManager.lambdaQuery(CFile.class)
                    .andEq(CFile::getUid, uid)
                    .andEq(CFile::getId, object.getString("pid"))
                    .count();
                Assert(count > 0, err);
            }
            CFile file = sqlManager.lambdaQuery(CFile.class)
                .andEq(CFile::getPid, object.getString("pid"))
                .andEq(CFile::getName, object.getString("name"))
                .andEq(CFile::getUid, uid)
                .single();
            if (file == null) {
                file = new CFile();
                file.setUid(uid);
                file.setCreator(uid);
                file.setType(Type.DIR);
                file.setName(object.getString("name"));
                file.setPid(pid);
                file.setLastModify(new Date());
                sqlManager.insert(file, true);
            }
            else{
                Assert(Objects.equals(file.getType(), Type.DIR), err);
            }
            return file;
        }

        throw new RestException(err);
    }


    /**
     * mv
     * @param sqlManager
     * @param object
     * @return
     */
    public Object mv(SQLManager sqlManager, JSONObject object){
        String err = "移动失败";
        long uid = AuthFilter.getUid();
        if(object.containsKey("from") && object.containsKey("to")){
            LinkedList<Holder> fromPs = analyzePath(sqlManager, uid, object.getString("from"));
            LinkedList<Holder> toPs = analyzePath(sqlManager, uid, object.getString("to"));
            CFile file = fromPs.getLast().file;
            Assert($.isNotNull(file), err);

            //如果存在且是文件夹
            if($.isNotNull(toPs.getLast()) && Objects.equals(toPs.getLast().file.type, Type.DIR)){
                file.pid = toPs.getLast().file.id;
                file.lastModify = new Date();
                sqlManager.updateById(file);
            }
            //如果不存在, 移动并改名
            else if($.isNull(toPs.getLast().file)){
                object.put("path", object.getString("to").replaceFirst("/[^/]+$", ""));
                CFile target = (CFile) mkdir(sqlManager, object);
                Assert($.isNotNull(target), err);
                file.pid = target.id;
                file.lastModify = new Date();
                file.name = toPs.getLast().name;
                sqlManager.updateById(file);
            }
            else{
                Assert(false, err);
            }

            return fromPs.getLast().file;
        }
        else if(object.containsKey("fromId") && object.containsKey("toId")){
            Assert(S.notEmpty(object.getString("fromId")));
            Assert(S.notEmpty(object.getString("toId")));
            CFile fromFile = sqlManager.lambdaQuery(CFile.class)
                    .andEq(CFile::getUid, AuthFilter.getUid())
                    .andEq(CFile::getId, object.getString("fromId"))
                    .single();
            CFile toFile = sqlManager.lambdaQuery(CFile.class)
                    .andEq(CFile::getUid, AuthFilter.getUid())
                    .andEq(CFile::getId, object.getString("toId"))
                    .single();
            Assert($.isNotNull(fromFile) && $.isNotNull(toFile), err);

            fromFile.pid = toFile.id;
            fromFile.lastModify = new Date();
            sqlManager.updateById(fromFile);
            return fromFile;
        }

        throw new RestException(err);
    }

    public Object cp(SQLManager sqlManager, JSONObject object){
        return null;
    }

    /**
     * rm
     * @param sqlManager
     * @param object
     * @return
     */
    public Object rm(SQLManager sqlManager, JSONObject object){
        long uid = AuthFilter.getUid();
        String err = "删除失败";
        List<JSONObject> objects = null;
        if(object.containsKey("path")){
            LinkedList<Holder> ps = analyzePath(sqlManager, uid, object.getString("path"));
            Assert($.isNotNull(ps.getLast().file), err);
            objects = sqlManager.select("cloud.查询子文件ID", JSONObject.class, C.newMap("ids", C.newList(ps.getLast().file.id), "uid", uid));
        }
        else if(object.containsKey("id")){
            List<Long> ids = U.toIdList(object.getString("id"));
            objects = sqlManager.select("cloud.查询子文件ID", JSONObject.class, C.newMap("ids", ids, "uid", uid));
        }
        Assert($.isNotNull(objects), err);
        List<String> _ids = objects.stream()
                .map(i -> i.getString("id"))
                .collect(Collectors.toList());
        sqlManager.lambdaQuery(CFile.class)
                .andIn(CFile::getId, _ids)
                .delete();
        return true;
    }


    /**
     *
     * @param sqlManager
     * @param object
     * @return
     */
    public Object rename(SQLManager sqlManager, JSONObject object){
        String id = object.getString("id");
        String name = object.getString("name");
        AssertNotEmpty(id, "参数错误");
        AssertNotEmpty(name, "参数错误");

        CFile cFile = sqlManager.single(getClass(), id);
        AssertEq(cFile.getUid(), AuthFilter.getUid(), "只能重命名自己的文件");
        //检查是否有同名文件
        Assert(
            sqlManager.lambdaQuery(CFile.class)
                .andEq(CFile::getUid, AuthFilter.getUid())
                .andEq(CFile::getPid, cFile.getPid())
                .andEq(CFile::getName, name)
                .count() == 0,
            "已经有同名文件"
        );

        cFile.setName(name);
        sqlManager.updateById(cFile);

        return cFile;
    }

    /**
     *
     * @param sqlManager
     * @param object
     * @return
     */
    public Object retags(SQLManager sqlManager, JSONObject object){
        String id = object.getString("id");
        String tags = object.getString("tags");
        AssertNotEmpty(id, "参数错误");
        AssertNotEmpty(tags, "参数错误");
        CFile cFile = sqlManager.single(getClass(), id);
        Assert($.isNotNull(cFile) && Objects.equals(cFile.getUid(), AuthFilter.getUid()), "只能给自己的文件编辑标签");

        cFile.setTags(tags);
        sqlManager.updateById(cFile);

        return cFile;
    }

    /**
     * 分享
     * @param sqlManager
     * @param object
     * @return
     */
    public Object share(SQLManager sqlManager, JSONObject object){
        String fid = object.getString("fid");
        String uid = object.getString("uid");
        AssertNotEmpty(fid, "参数错误");
        AssertNotEmpty(uid, "参数错误");
        CFile cFile = sqlManager.single(getClass(), fid);
        Assert($.isNotNull(cFile) && Objects.equals(cFile.getUid(), AuthFilter.getUid()), "只能分享自己的文件");

        List<Long> uids = U.toIdList(uid);
        List<CFileShare> shares = uids.stream()
            .filter(ui -> {
                return sqlManager.lambdaQuery(CFileShare.class)
                    .andEq(CFileShare::getFid, fid)
                    .andEq(CFileShare::getToUid, ui)
                    .count() == 0;
            })
            .map(ui -> {
                CFileShare share = new CFileShare();
                share.setLastModify(new Date());
                share.setFid(Long.parseLong(fid));
                share.setToUid(ui);
                share.setFromUid(AuthFilter.getUid());
                return share;
            })
            .collect(Collectors.toList());
        sqlManager.insertBatch(CFileShare.class, shares);
        return true;
    }



}
