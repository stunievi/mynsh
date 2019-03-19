package com.beeasy.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.osgl.util.C;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class test {


    public static void main(String[] args) throws Exception{

        //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress("47.94.97.138",27017);
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        addrs.add(serverAddress);
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);
        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(addrs);
        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("databaseName");

        MongoCollection<Document> coll = mongoDatabase.getCollection("fu");
//String str = "{\"title\":\"MongoDB 教程\",\"description\":\"MongoDB 是一个 Nosql 数据库\",\"by\":\"菜鸟教程\",\"url\":\"http://www.runoob.com\",\"tags\":{\"mongodb\":[{\"a\":\"b\"}]},\"likes\":100}";
//JSONObject object = JSON.parseObject(str);
//        mongoDatabase.getCollection("fu").insertOne(new Document(object));
//
//
//
//        FindIterable<Document> findIterable =  mongoDatabase.getCollection("fu").find();
//        MongoCursor<Document> mongoCursor = findIterable.iterator();
//        while(mongoCursor.hasNext()){
//            Object obj = mongoCursor.next();
//            int c = 1;
//        }
//        Pattern queryPattern = Pattern.compile("bccc", Pattern.CASE_INSENSITIVE);
//        BasicDBObject queryObject = new BasicDBObject("tags.mongodb.a",queryPattern);
//        FindIterable<Document> cursor = mongoDatabase.getCollection("fu").find(queryObject);
//        MongoCursor<Document> it = cursor.iterator();
//        while(it.hasNext()){
//            Document obj = it.next();
//            System.out.println(obj.toString());
//
//        }

//        String res = QccUtil.getData("http://api.qichacha.com/ECIV4/GetFullDetailsByName", C.newMap(
//                "keyWord","深圳市"
//        ));
//        JSONObject object = JSON.parseObject(res);
//        object.put("cusId", "222222222222222222222222222");
//        object.put("updateTime", new Date().getTime());
//        coll.insertOne(new Document(object));
//        Bson and = Filters.and(Filters.eq("cusId", "111"), Filters.lt("updateTime", new Date().getTime()));
//        Document com = mongoDatabase.getCollection("fu").find(and).first();
//        if(null == com){
//            String res = QccUtil.getData("http://localhost:8015/test/qcc/ECI/Search", C.newMap(
//                    "aaaaa","b",
//                    "ccccc","d"
//            ));
//            JSONObject object = JSON.parseObject(res);
//            object.put("cusId", "111");
//            object.put("updateTime", new Date().getTime());
//            coll.insertOne(new Document(object));
////            coll.deleteMany(Filters.eq("cusId", "111"));
//        }else{
//            System.out.println("kong");
//        }



        System.out.println("Connect to database successfully");
    }

}
