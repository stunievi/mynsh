package com.beeasy.MQSender;

import com.beeasy.mscommon.DataSourceConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class DB2Op {

    public static String getUUID() {
		/*UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.substring(0, 8) + str.substring(9, 13)
				+ str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
		return temp;*/

        return UUID.randomUUID().toString().replace("-", "");
    }

    public static List<String> main() {
        Statement stmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
//            String url = "jdbc:db2://47.94.97.138:50000/test";
//            String user = "db2inst1";
//            String password = "db2inst1";


            String url = "jdbc:db2://150.0.104.8:50000/fzsys";
            String user = "fzsys";
            String password = "11111111";


            Connection conn = DriverManager.getConnection(DataSourceConfiguration.DB_URL, DataSourceConfiguration.DB_USER, DataSourceConfiguration.DB_PASSWORD);
            String sql = "select * from (select a.*,row_number() over(partition by CUS_NAME order by CUS_NAME) rn from \n" +
                    "(select p1.* from db2inst1.RPT_M_RPT_SLS_ACCT p1 where p1.ACCOUNT_STATUS in ('1','6') \n" +
                    "and p1.GL_CLASS not like '0%' and CUST_TYPE like '2%') a) b where rn =1";

            stmt = (Statement) conn.createStatement();
            stmt.executeQuery(sql);
            rs = (ResultSet) stmt.getResultSet();
            while (rs.next()) {
                String name = rs.getString("CUS_NAME");
//                System.out.println(name);
                list.add(name);
            }
            rs.close();
            stmt.close();
            conn.close();
            return list;

        } catch (Exception sqle) {
            System.out.println(sqle);
            return null;
        }

    }

    public static boolean writeLog(String StrContent, String StrOrderID) {
        Statement stmt = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        boolean Result = false;
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            String url = "jdbc:db2://47.94.97.138:50000/test";
            String user = "db2inst1";
            String password = "db2inst1";
//            String url = "jdbc:db2://150.0.104.8:50000/fzsys";
//            String user = "fzsys";
//            String password = "11111111";
            Connection conn = DriverManager.getConnection(DataSourceConfiguration.DB_URL,DataSourceConfiguration.DB_USER,DataSourceConfiguration.DB_PASSWORD);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
            Date t1 = new Date();
            String StrNow = df.format(t1);
            String StrID = df1.format(t1);
            String sql = "INSERT INTO DB2INST1.T_QCC_LOG(ID,ADD_TIME, OPERATOR, TYPE, CONTENT, ORDER_ID, DATA_ID) " +
                    "VALUES(" + StrID + ",\'" + StrNow + "\',\'1\',\'01\',\'" + StrContent + "\',\'" + StrOrderID + "\',\'\')";
            System.out.println(sql);
            stmt = (Statement) conn.createStatement();
            Result = stmt.execute(sql);
            rs.close();
            stmt.close();
            conn.close();
            return Result;

        } catch (Exception sqle) {
            return Result;
        }

    }
}
