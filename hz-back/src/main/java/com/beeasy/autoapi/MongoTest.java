//package com.beeasy.autoapi;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import org.bson.Document;
//import org.osgl.$;
//import org.osgl.Lang;
//import org.osgl.util.C;
//import org.osgl.util.converter.TypeConverterRegistry;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MongoTest {
////    private List<Lang.TypeConverter> converters = new ArrayList<>();
////
////    public class Converter extends Lang.TypeConverter<JSONArray, BasicDBList>{
////
////        @Override
////        public BasicDBList convert(JSONArray objects) {
////            return $.map(objects).withConverter(converters).to(BasicDBList.class);
////        }
////    }
////
////    public class
//
//    public static Object convert(Object o){
//        if(o instanceof JSONObject){
//            BasicDBObject ret = new BasicDBObject();
//            ((JSONObject) o).forEach((k,v) -> {
//                ret.put(k, convert(v));
//            });
//            return ret;
//        }
//        else if(o instanceof JSONArray){
//            BasicDBList ret = new BasicDBList();
//            ((JSONArray) o).forEach(v -> {
//                ret.add(convert(v));
//            });
//            return ret;
//        }
//        return o;
//    }
//
//    public static void main(String[] args){
//
//        //test
//        JSONObject object2 = new JSONObject();
//        JSONArray arr = new JSONArray();
//        for(short i = 0; i < 10; i++){
//            JSONObject object1 = new JSONObject();
//            object1.put(i + "", i);
//            arr.add(object1);
//        }
//        object2.put("test", arr);
//
//        Document document = $.map(convert(object2)).to(Document.class);
//        int c = 1;
//    }
//}
