package com.beeasy.hzback.modules.system.service;


import com.beeasy.hzback.core.util.SqlUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataSearchService {

    @Autowired
    SqlUtils sqlUtils;

    public Page searchPublicClient(PublicClientRequest request, Pageable pageable){
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a left join CUS_COM as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE<>'110' and a.CUS_TYPE<>'120' and a.CUS_TYPE<>'130') ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format("a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add((String.format("a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if(!StringUtils.isEmpty(request.getPHONE())){
            strings.add(String.format("a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if(!StringUtils.isEmpty(request.getCUST_MGR())){
            strings.add(String.format("b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        if(strings.size() > 0){
            sql += " and " + StringUtils.join(strings.toArray()," and ");
        }

        log.error(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    public Page searchPrivateClient(PrivateClientRequest request, Pageable pageable){
        String sql = "select a.CUS_ID,a.CUS_NAME,b.CUST_MGR from CUS_BASE as a LEFT JOIN CUS_INDIV as b on a.CUS_ID=b.CUS_ID where (a.CUS_TYPE='110' or a.CUS_TYPE='120' or a.CUS_TYPE='130') ";
        List<String> strings = new ArrayList<>();
        if(!StringUtils.isEmpty(request.getCUS_ID())){
            strings.add(String.format("a.CUS_ID like '%%%s%%'", request.getCUS_ID()));
        }
        if(!StringUtils.isEmpty(request.getCUS_NAME())){
            strings.add((String.format("a.CUS_NAME like '%%%s%%'", request.getCUS_NAME())));
        }
        if(!StringUtils.isEmpty(request.getPHONE())){
            strings.add(String.format("a.PHONE like '%%%s%%'", request.getPHONE()));
        }
        if(!StringUtils.isEmpty(request.getCUST_MGR())){
            strings.add(String.format("b.CUST_MGR like '%%%s%%'", request.getCUST_MGR()));
        }
        if(!StringUtils.isEmpty(request.getCERT_CODE())){
            strings.add(String.format("a.CERT_CODE like %%%s%%", request.getCERT_CODE()));
        }
        if(strings.size() > 0){
            sql += " and " + StringUtils.join(strings.toArray()," and ");
        }
        log.info(sql);
        return sqlUtils.pageQuery(sql, pageable);
    }

    @Data
    public static class PublicClientRequest{
        String CUS_ID;
        String CUS_NAME;
        String PHONE;
        String CUST_MGR;
    }

    @Data
    public static class PrivateClientRequest{
        String CUS_ID;
        String CUS_NAME;
        String CERT_CODE;
        String PHONE;
        String CUST_MGR;
    }
}
