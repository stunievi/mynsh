package com.beeasy.hzback.modules.system.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.QccHistLog;
import com.beeasy.mscommon.util.U;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
//@Async
public class QccHistLogAsyncService {
    @Autowired
    private SQLManager sqlManager;

    /*private final String URL = "http://47.96.98.198:8081/qcc/";
    // 失信信息
    private final String searchShiXinURL = URL+"CourtV4/SearchShiXin";
    // 被执行信息
    private final String searchZhiXingURL = URL + "CourtV4/SearchZhiXing";
    // 裁判文书
    private final String searchJudgmentDocURL = URL + "JudgeDocV4/SearchJudgmentDoc";
    // 法院公告
    private final String searchCourtAnnouncementURL = URL + "CourtNoticeV4/SearchCourtAnnouncement";
    // 开庭公告
    private final String searchCourtNoticeURL = URL + "CourtAnnoV4/SearchCourtNotice";
    // 司法拍卖
    private final String getJudicialSaleListURL = URL + "JudicialSale/GetJudicialSaleList";
    // 环保处罚
    private final String getEnvPunishmentListURL = URL + "EnvPunishment/GetEnvPunishmentList";
    // 司法协助
    private final String getJudicialAssistanceURL = URL + "JudicialAssistance/GetJudicialAssistance";
    // 经营异常
    private final String getOpExceptionURL = URL + "ECIException/GetOpException";*/

    //    private  String URL = "http://47.94.97.138/qcc/";
    // 失信信息
    private  String searchShiXinURL ;
    // 被执行信息
    private  String searchZhiXingURL ;
    // 裁判文书
    private  String searchJudgmentDocURL ;
    // 法院公告
    private  String searchCourtAnnouncementURL ;
    // 开庭公告
    private  String searchCourtNoticeURL ;
    // 司法拍卖
    private  String getJudicialSaleListURL ;
    // 环保处罚
    private  String getEnvPunishmentListURL ;
    // 司法协助
    private  String getJudicialAssistanceURL ;
    // 经营异常
    private  String getOpExceptionURL ;


    public void onInit(String URL){
        // 失信信息
        searchShiXinURL = URL+"CourtV4/SearchShiXin";
        // 被执行信息
        searchZhiXingURL = URL + "CourtV4/SearchZhiXing";
        // 裁判文书
        searchJudgmentDocURL = URL + "JudgeDocV4/SearchJudgmentDoc";
        // 法院公告
        searchCourtAnnouncementURL = URL + "CourtNoticeV4/SearchCourtAnnouncement";
        // 开庭公告
        searchCourtNoticeURL = URL + "CourtAnnoV4/SearchCourtNotice";
        // 司法拍卖
        getJudicialSaleListURL = URL + "JudicialSale/GetJudicialSaleList";
        // 环保处罚
        getEnvPunishmentListURL = URL + "EnvPunishment/GetEnvPunishmentList";
        // 司法协助
        getJudicialAssistanceURL = URL + "JudicialAssistance/GetJudicialAssistance";
        // 经营异常
        getOpExceptionURL = URL + "ECIException/GetOpException";
    }

    /**
     * 失信信息
     */
    public Map<String, String> searchShiXin(){
        Map<String, String> map = new HashMap<>();
        JSONArray ja = sendURL(searchShiXinURL);
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" + qccId, cusName);

        }
        return map;
    }

    /**
     * 被执行信息
     */
    public Map<String, String> searchZhiXing(){

        JSONArray ja = sendURL(searchZhiXingURL);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");

            map.put(cusName + "-" +qccId, cusName);

        }
        return map;
    }

    /**
     * 裁判文书
     */
    public Map<String, String> searchJudgmentDoc(){
        JSONArray ja = sendURL(searchJudgmentDocURL);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);

        }
        return map;
    }

    /**
     * 法院公告
     */
    public Map<String, String> searchCourtAnnouncement(){
        JSONArray ja = sendURL(searchCourtAnnouncementURL);
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);
        }
        return map;
    }

    /**
     * 开庭公告
     */
    public Map<String,String> searchCourtNotice(){
        JSONArray ja = sendURL(searchCourtNoticeURL);
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);

        }
        return map;
    }

    /**
     * 司法拍卖
     */
    public Map<String, String> getJudicialSaleList(){
        JSONArray ja = sendURL(getJudicialSaleListURL);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);

        }
        return map;
    }

    /**
     * 环保处罚
     */
    public Map<String, String> getEnvPunishmentList(){
        JSONArray ja = sendURL(getEnvPunishmentListURL);
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);
        }
        return map;
    }

    /**
     * 司法协助
     */
    public Map<String, String> getJudicialAssistance(){
        JSONArray ja = sendURL(getJudicialAssistanceURL);
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("ExecutionNoticeNum");
            String cusName = jo2.getString("Name");
            map.put(cusName + "-" +qccId, cusName);

        }
        return map;
    }

    /**
     * 经营异常
     */
    public Map<String, String> getOpException(){
        JSONArray ja = sendURL(getOpExceptionURL);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String decisionOffice = jo2.getString("DecisionOffice");

            String qccId = decisionOffice;
            String addDate = jo2.getString("AddDate");
            String cusName = jo2.getString("Name");

            if(null != addDate){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Timestamp ts = Timestamp.valueOf(addDate);
                String time = sdf.format(ts);
                qccId = qccId + time;
            }
            map.put(cusName + "-" +qccId, cusName);


        }
        return map;
    }

//    private JSONArray sendURL(String URL, String cusName){
//        // 发送请求
//        String resultString = sendGET(URL, cusName);
//        JSONObject objectStr = JSON.parseObject(resultString);
//
//        JSONArray ja = objectStr.getJSONArray("Result");
//        return ja;
//    }
    private JSONArray sendURL(String URL){
        // 发送请求
        String resultString = sendGET(URL);
        JSONObject objectStr = JSON.parseObject(resultString);

        JSONArray ja = objectStr.getJSONArray("Result");
        return ja;
    }

    // 保存数据
    public void saveEntity(String cusName, String qccId, String type){
        QccHistLog entity = new QccHistLog();
        entity.setId(U.getSnowflakeIDWorker().nextId());
        entity.setAddTime(new Date());
        entity.setFullName(cusName);
        entity.setQccId(qccId);
        entity.setQccOtherId("");
        entity.setType(type);
        sqlManager.insert(entity, true);
    }

    // 发送get请求
    private String sendGET(String URL){
        String resultString ="";
        try{
            resultString =  HttpUtil.get(URL, C.newMap(
                    "pageIndex","1","pageSize","9999"
            ));
        }catch (Exception e){
            e.printStackTrace();
        }

        return resultString;
    }
//    private String sendGET(String URL,String cusName){
//        String resultString ="";
//        try{
//            resultString =  HttpUtil.get(URL, C.newMap(
//                    "fullName", cusName,"pageIndex","1","pageSize","999"
//            ));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return resultString;
//    }
}
