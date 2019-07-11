package com.beeasy.hzlink.util;

import com.alibaba.fastjson.JSON;
import com.github.llyb120.nami.core.Obj;
import org.beetl.sql.core.engine.PageQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.llyb120.nami.core.DBService.sqlManager;

public class U {

    public static <T> PageQuery<T> beetlPageQuery(String sqlId, Class<T> clz, Obj params){
        PageQuery pageQuery = new PageQuery<>();
        int page = 1;
        int size = 10;
        try {
            page = params.getIntValue("pageIndex");
            if(page < 1){
                page = params.getIntValue("page");
            }
            size = params.getIntValue("pageSize");
            if(size < 1){
                size = params.getIntValue("size");
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

}
