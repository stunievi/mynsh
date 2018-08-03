package com.beeasy.hzback.core.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.beeasy.hzback.core.helper.JSONConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;

@Slf4j
@Component
public class SqlUtils {
    @Value("${spring.datasource.driver-class-name}")
    String dbDriver;

    @Autowired
    DataSource dataSource;
    @Autowired
    EntityManager entityManager;

    public boolean execute(String sql){
        PreparedStatement statement = null;
        ResultSet rs = null;
        Connection connection = null;
        boolean ret = false;
        try{
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            ret = statement.execute();
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
        return ret;
    }

    public List<Map<String,String>> query(String sql){
        return query(sql, new ArrayList<>());
    }

    public List<Map<String,String>> query(String sql, Collection<Object> args){
        return query(sql,args.toArray());
    }

    public List<Map<String,String>> query(String sql, Object ...args){
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
                for (Object arg : args) {
                    if(arg instanceof Collection){
                        for(Object a :(Collection<Object>) arg){
                            statement.setObject(++count, a);
                        }
                    }
                    else{
                        statement.setObject(++count, arg);
                    }
//                    statement.setArray();
//                    statement.setString(++count, arg);
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
        return pageQuery(sql,new ArrayList<>(),pageable);
    }

    public Page pageQuery(String sql, Collection<String> params, Pageable pageable){
        String countSql = sql
                .replaceFirst("select([\\w\\W]+?)from", "select count(*) as NUM from")
                .replaceFirst("SELECT([\\w\\W]+?)FROM","SELECT count(*) as NUM FROM");
        List<Map<String, String>> countList = query(countSql,params);
        int count = Integer.valueOf(countList.get(0).getOrDefault("NUM","0"));
        //添加分页
        switch (dbDriver){
            case "com.mysql.jdbc.Driver":
                sql += String.format(" LIMIT %d, %d", pageable.getOffset(), pageable.getPageSize());
                break;

            case "com.ibm.db2.jcc.DB2Driver":
                //https://www.ibm.com/developerworks/community/wikis/home?lang=en#!/wiki/IBM%20i%20Technology%20Updates/page/OFFSET%20and%20LIMIT
                sql += String.format(" LIMIT %d, %d", pageable.getOffset(),pageable.getPageSize());
                break;
        }
        List<Map<String, String>> list = query(sql,params);
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
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        PageImpl page = new PageImpl(query.getResultList(),pageable, (Long) countQuery.getResultList().get(0));
        return page;
    }

    public List<Object> hqlQuery(String sql, Map<String,Object> params){
        Query query = entityManager.createQuery(sql);
        if(null != params){
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(),entry.getValue());
            }
        }
        return query.getResultList();
    }


}
