package com.beeasy.hzback.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Component
public class SqlUtils {

    @Autowired
    DataSource dataSource;

    public List<Map<String,String>> query(String sql){
        return query(sql, new ArrayList<>());
    }

    public List<Map<String,String>> query(String sql, Collection<String> args){
        List s = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try{
            statement = dataSource.getConnection().prepareStatement(sql);
            int count = 0;
            for (String arg : args) {
                statement.setString(++count, arg);
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


}
