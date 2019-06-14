package com.beeasy.MQSender;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendMessage {

    public static void main(String StrGetFlag) {
        ConnectionFactory factory; //连接工厂
        Connection connection;//jms连接
        Session session;//发送、接收线程
        Destination destination;//消息目的地
        MessageProducer producer;//消息发送者

        factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://47.94.97.138:8011");
//        factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
//                "tcp://150.0.100.7:61616");

        List<String> cus_name_list = new ArrayList<String>();   //公司名称列表

        try {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("qcc-company-infos-request");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);  //是否为持久化 ，NON_PERSISTENT非持久化 ，PERSISTENT持久化

            //获取存量对公客户名单
            cus_name_list = DB2Op.main();

            //生成指令ID
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
            Date t1 = new Date();
            String StrID = "fzsys_" + df1.format(t1);

            String StrBefore = "{\"uid\":\"1\",\"OrderData\":[";
            String StrAfter = "],\"OrderId\":\"" + StrID + "\"}";
            String StrContent = "";

            //根据存量对公客户名单拼装指令内容
            for (int i = 0; i < cus_name_list.size(); i++) {
                if (i==0){
                    StrContent = "{\"Content\":\"" + cus_name_list.get(i) + "\",\"Sign\":\"" + StrGetFlag + "\"}";
                }
                else{
                    StrContent = StrContent + ",{\"Content\":\"" + cus_name_list.get(i) + "\",\"Sign\":\"" + StrGetFlag + "\"}";
                }
            }
            //待发送指令内容
            String msg = StrBefore + StrContent + StrAfter;
            System.out.println(msg);
            //写企查查日志（系统跑批日志）
            String StrGetFlag1 = "";
            switch(StrGetFlag){
                case "01":
                    StrGetFlag1 = "基本信息";
                    break;
                case "02":
                    StrGetFlag1 = "法律诉讼";
                    break;
                case "03":
                    StrGetFlag1 = "经营风险";
                    break;
                case "04":
                    StrGetFlag1 = "企业族谱";
                    break;
                case "05":
                    StrGetFlag1 = "历史信息";
                    break;
                default:
                    StrGetFlag1 = "未知";
                    break;
            }
            String StrLogContent = "系统跑批：存量对公客户(数量："+ String.valueOf(cus_name_list.size()) + ")-" + StrGetFlag1;
            DB2Op.writeLog(StrLogContent,StrID);
            //向ActiveMQ发送字符串指令
            TextMessage textMessage = session.createTextMessage(msg);
                producer.send(textMessage);

//
//            //根据存量对公客户名单拼装指令内容
//            for (int i = 0; i < cus_name_list.size(); i++) {
//
//                //生成指令ID
//                SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
//                Date t1 = new Date();
//                String StrID = "fzsys_" + df1.format(t1);
//
//                String StrBefore = "{\"uid\":\"1\",\"OrderData\":[";
//                String StrAfter = "],\"OrderId\":\"" + StrID + "\"}";
//                String StrContent = "";
//
//                StrContent = "{\"Content\":\"" + cus_name_list.get(i) + "\",\"Sign\":\"" + StrGetFlag + "\"}";
//                //待发送指令内容
//                String msg = StrBefore + StrContent + StrAfter;
//
//                //写企查查日志（系统跑批日志）
//                String StrGetFlag1 = "";
//                switch(StrGetFlag){
//                    case "01":
//                        StrGetFlag1 = "基本信息";
//                        break;
//                    case "02":
//                        StrGetFlag1 = "法律诉讼";
//                        break;
//                    case "03":
//                        StrGetFlag1 = "经营风险";
//                        break;
//                    case "04":
//                        StrGetFlag1 = "企业族谱";
//                        break;
//                    case "05":
//                        StrGetFlag1 = "历史信息";
//                        break;
//                    default:
//                        StrGetFlag1 = "未知";
//                        break;
//                }
//                String StrLogContent = "系统跑批:" + cus_name_list.get(i) + "-" + StrGetFlag1;
//                DB2Op.writeLog(StrLogContent,StrID);
//                //向ActiveMQ发送字符串指令
//                TextMessage textMessage = session.createTextMessage(msg);
//                producer.send(textMessage);
//                try
//                {
//                    Thread.sleep(100);
//                } catch (InterruptedException eee) {
//                    eee.printStackTrace();
//                }
//            }

            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
