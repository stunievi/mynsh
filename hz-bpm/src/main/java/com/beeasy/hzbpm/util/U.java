package com.beeasy.hzbpm.util;

import com.alibaba.fastjson.JSON;
import com.github.llyb120.nami.json.Json;
import com.github.llyb120.nami.json.Obj;
import org.beetl.sql.core.engine.PageQuery;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.github.llyb120.nami.ext.beetlsql.BeetlSql.sqlManager;


public class U {

    public static <T> PageQuery<T> beetlPageQuery(String sqlId, Class<T> clz, Obj params){
        PageQuery pageQuery = new PageQuery<>();
        int page = 1;
        int size = 10;
        try {
            page = params.i("pageIndex");
            if(page < 1){
                page = params.i("page");
            }
            size = params.i("pageSize");
            if(size < 1){
                size = params.i("size");
            }
        } finally {
            if(page < 1){
                page = 1;
            }
            if(size < 1){
                size = 10;
            }
            pageQuery.setPageSize(size);
            pageQuery.setPageNumber(page);
        }
        pageQuery.setParas(params);
        PageQuery<T> retObj =  sqlManager.pageQuery(sqlId, clz, pageQuery);
        List<T> dataList = new ArrayList<>();
        retObj.getList().forEach(o->{
            Map<String, Object> listItem = (Map<String, Object>) JSON.toJSON(o);
            HashMap item = new HashMap();
            for (Map.Entry<String, Object> entry : listItem.entrySet()) {
                item.put(entry.getKey().toUpperCase(), entry.getValue());
            }
            dataList.add((T) item);
        });
        retObj.setList(dataList);
        return retObj;
    }

    public static Document toDoc(Object object){
        return Document.parse(JSON.toJSONString(object));
    }

    public static List<? extends Bson> toList(Collection object){
        ArrayList<Bson> list = new ArrayList<Bson>();
        BsonArray arr = BsonArray.parse(JSON.toJSONString(object));
        for (BsonValue bsonValue : arr) {
            list.add((Bson) bsonValue);
        }
        return (List<? extends Bson>) list;
    }

}
