package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LinkSeachService {

    /**
     * 股东清单查询
     */
    public PageQuery<JSONObject> gdSeach(String no,
         Map<String, Object> params){
        PageQuery<com.alibaba.fastjson.JSONObject> pageQuery = U.beetlPageQuery("accloan." + no, JSONObject.class, params);
        return pageQuery;
    }
}
