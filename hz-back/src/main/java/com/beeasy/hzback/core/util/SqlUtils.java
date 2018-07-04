package com.beeasy.hzback.core.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class SqlUtils {

    @Autowired
    DataSource dataSource;
    @Autowired
    EntityManager entityManager;

    public List<Map<String,String>> query(String sql){
        return query(sql, new ArrayList<>());
    }

    public List<Map<String,String>> query(String sql, Collection<String> args){
        List s = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            int count = 0;
            //绑定参数
            if(null != args){
                for (String arg : args) {
                    statement.setString(++count, arg);
                }
            }
            rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            count = rsmd.getColumnCount();
            while(rs.next()){
                Map<String, String> hm = new HashMap<String, String>();
                for (int i = 1; i <= count; i++) {
                    String key = rsmd.getColumnLabel(i);
                    String value = rs.getString(i);
                    hm.put(key, value);
                }
                s.add(hm);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(null != connection){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != statement){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(null != rs){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return s;
    }

    public Page pageQuery(String sql, Pageable pageable){
        String countSql = sql
                .replaceFirst("select([\\w\\W]+?)from", "select count(*) as num from")
                .replaceFirst("SELECT([\\w\\W]+?)FROM","SELECT count(*) as num FROM");
         List<Map<String, String>> countList = query(countSql,null);
        int count = Integer.valueOf(countList.get(0).getOrDefault("num","0"));
        //添加分页
        sql += String.format(" limit %d,%d", pageable.getOffset(), pageable.getPageSize());
        List<Map<String, String>> list = query(sql,null);
        return new PageImpl(list, pageable, count);
    }


    /**
     * hql 分页查询
     * @param sql
     * @param params
     * @param countStr
     * @param pageable
     * @return
     */
    public Page<Object> hqlQuery(String sql, Map<String,Object> params, String countStr, Pageable pageable){
        Query query = entityManager.createQuery(sql);
        Query countQuery = entityManager.createQuery(sql.replace(countStr, "count(" + countStr + ")"));
        if(null != params){
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(),entry.getValue());
                countQuery.setParameter(entry.getKey(),entry.getValue());
            }
        }
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        PageImpl page = new PageImpl(query.getResultList(),pageable, (Long) countQuery.getResultList().get(0));
        return page;
    }


}
