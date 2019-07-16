package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.beeasy.hzbpm.util.Result;
import com.github.llyb120.nami.core.Obj;
import com.github.llyb120.nami.core.R;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.QueryBuilder;
import org.beetl.sql.core.engine.PageQuery;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.core.Json.a;
import static com.github.llyb120.nami.server.Vars.$get;
import static com.github.llyb120.nami.server.Vars.$request;

public class form {

    public Result list(){
        var page = $get.i("page");
        var size = $get.i("size");
        if(page == null) page = 1;
        if(size == null) size = 20;
        var collection = db.getCollection("form");
        var list = a();
        var pq = new PageQuery((long)page,(long)size);
        pq.setTotalRow(collection.countDocuments());
        Integer finalPage = page;
        Integer finalSize = size;
        collection.find()
                .sort(new Document("lastModify", -1))
                .skip((page - 1) * size)
                .limit(size).iterator()
                .forEachRemaining(d -> {
                    var id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    d.put("idex", list.size() + 1 + (finalPage - 1) * finalSize);
                    list.add(d);
                });
        pq.setList(list);
        return Result.ok(pq);
    }


    public Result add(){
        var collection = db.getCollection("form");
        $request.put("createTime", new Date());
        $request.put("lastModify", new Date());
        var id = $request.s("_id");
        $request.remove("_id");
        //名字
        if(StrUtil.isBlank($request.s("name"))){
            return Result.error("表单名不能为空");
        }
        long same = 0;
        if(StrUtil.isBlank(id)){
            //不能有同名的
            same = collection.countDocuments(new BasicDBObject("name", $request.s("name")));
        } else {
            //除我之外不能有同名的
            same = collection.countDocuments(
                    new BasicDBObject()
                    .append("_id", new BasicDBObject("$ne", new ObjectId(id)))
                    .append("name", $request.s("name"))
            );
        }
        if(same > 0){
            return Result.error("已经有同名的表单");
        }
        Document doc;
        if(StrUtil.isBlank(id)){
            //add
            doc = new Document($request);
            collection.insertOne(doc);
        } else {
            doc = new Document($request);
            collection.replaceOne(new BasicDBObject("_id", new ObjectId(id)), doc);
        }
        return Result.ok(doc);
    }

    public Result delete(String _id){
        var collection = db.getCollection("form");
        var ret = collection.findOneAndDelete(new Document(){{
            put("_id", new ObjectId(_id));
        }});
        return Result.ok(ret != null);
    }


//    private void analyzeForm(String xml){
//       var doc =  XmlUtil.parseXml(xml);
//        doc.getChildNodes().getLength();
//    }
//
//    private void walk(org.w3c.dom.Document node){
//
//    }
}
