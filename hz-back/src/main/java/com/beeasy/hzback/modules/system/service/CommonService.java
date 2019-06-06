package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.filter.AuthFilter;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 测试公共controller
 */
@Service
public class CommonService {
    @Autowired
    SQLManager sqlManager;

    private static Connection getConn() {
        String driver = "com.ibm.db2.jcc.DB2Driver";
        String url = "jdbc:db2://47.106.216.52:50000/dxmy";
        String username = "db2inst1";
        String password = "db2inst1";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public int insert() {
        List<JSONObject> jsonObjects = sqlManager.select("accloan.对公客户", JSONObject.class, C.newMap("uid", AuthFilter.getUid()));
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into CUS_COM_LIST (CUS_ID,CUS_NAME) values(?,?)";
        PreparedStatement pstmt = null;
        try {
            for (JSONObject jsonObject : jsonObjects) {
                String cusId = jsonObject.getString("CUS_ID");
                String cusName = jsonObject.getString("CUS_NAME");
                pstmt = (PreparedStatement) conn.prepareStatement(sql);
                pstmt.setString(1, cusId);
                pstmt.setString(2, cusName);
                i = pstmt.executeUpdate();
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public int delete() {
        Connection conn = getConn();
        int i = 0;
        String sql = "delete from CUS_COM_LIST";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            i = pstmt.executeUpdate();
            System.out.println("resutl: " + i);
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

}
