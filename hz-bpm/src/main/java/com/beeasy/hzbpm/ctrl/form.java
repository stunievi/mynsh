package com.beeasy.hzbpm.ctrl;

import cn.hutool.core.util.StrUtil;
import com.beeasy.hzbpm.util.Result;
import com.beeasy.hzbpm.util.U;
import com.github.llyb120.nami.json.Json;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.beetl.sql.core.engine.PageQuery;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

import static com.beeasy.hzbpm.service.MongoService.db;
import static com.github.llyb120.nami.json.Json.a;
import static com.github.llyb120.nami.server.Vars.$get;
import static com.github.llyb120.nami.server.Vars.$request;

public class form {

    public Result list(){
        Integer page = $get.i("page");
        Integer size = $get.i("size");
        if(page == null) page = 1;
        if(size == null) size = 20;
        MongoCollection<Document> collection = db.getCollection("form");
        Json list = a();
        PageQuery pq = new PageQuery((long) page, (long) size);
        pq.setTotalRow(collection.countDocuments());
        Integer finalPage = page;
        Integer finalSize = size;
        collection.find()
                .sort(new Document("lastModify", -1))
                .skip((page - 1) * size)
                .limit(size).iterator()
                .forEachRemaining(d -> {
                    String id = d.getObjectId("_id").toHexString();
                    d.put("_id", id);
                    d.put("idex", list.size() + 1 + (finalPage - 1) * finalSize);
                    list.add(d);
                });
        pq.setList(list.list());
        return Result.ok(pq);
    }


    public Result add(){
        MongoCollection<Document> collection = db.getCollection("form");
        $request.put("createTime", new Date());
        $request.put("lastModify", new Date());
        String id = $request.s("_id");
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
            doc = U.toDoc($request);
            collection.insertOne(doc);
        } else {
            doc = U.toDoc($request);
            collection.replaceOne(new BasicDBObject("_id", new ObjectId(id)), doc);
        }
        return Result.ok(doc);
    }

    public Result delete(String _id){
        MongoCollection<Document> collection = db.getCollection("form");
        Document ret = collection.findOneAndDelete(new Document() {{
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
