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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Async
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
     * @param cusName
     * @param map
     */
    public Future<String> searchShiXin(String cusName, Map<String, Integer> map){

        JSONArray ja = sendURL(searchShiXinURL, cusName);
        int shixinInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");

            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();

            if(null == qccHistLogEntity){
                shixinInt = shixinInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "01");
                map.put("searchShiXin",shixinInt);
                System.out.println("searchShiXin 失信："+shixinInt);
            }
//        }
        }
        return new AsyncResult<>("");
    }

    /**
     * 被执行信息
     * @param cusName
     * @param map
     */
    public Future<String> searchZhiXing(String cusName, Map<String, Integer> map){

        JSONArray ja = sendURL(searchZhiXingURL, cusName);

        int zhiXingInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");

            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();

            if(null == qccHistLogEntity){
                zhiXingInt = zhiXingInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "02");
                map.put("searchZhiXing",zhiXingInt);
                System.out.println("searchZhiXing 被执行："+zhiXingInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 裁判文书
     * @param cusName
     * @param map
     */
    public Future<String> searchJudgmentDoc(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(searchJudgmentDocURL, cusName);
        int judgmentInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                judgmentInt = judgmentInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "03");
                map.put("searchJudgmentDoc",judgmentInt);
                System.out.println("searchJudgmentDoc 裁判文书："+judgmentInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 法院公告
     * @param cusName
     * @param map
     */
    public Future<String> searchCourtAnnouncement(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(searchCourtAnnouncementURL, cusName);
        int courtAnnouncementInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                courtAnnouncementInt = courtAnnouncementInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "04");
                map.put("searchCourtAnnouncement",courtAnnouncementInt);
                System.out.println("searchCourtAnnouncement 法院公告："+courtAnnouncementInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 开庭公告
     * @param cusName
     * @param map
     */
    public Future<String> searchCourtNotice(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(searchCourtNoticeURL, cusName);
        int courtNoticeInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                courtNoticeInt = courtNoticeInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "05");
                map.put("searchCourtNotice",courtNoticeInt);
                System.out.println("searchCourtNotice 开庭公告："+courtNoticeInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 司法拍卖
     * @param cusName
     * @param map
     */
    public Future<String> getJudicialSaleList(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(getJudicialSaleListURL, cusName);
        int judicialSaleListInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                judicialSaleListInt = judicialSaleListInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "06");
                map.put("getJudicialSaleList",judicialSaleListInt);
                System.out.println("getJudicialSaleList 司法拍卖："+judicialSaleListInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 环保处罚
     * @param cusName
     * @param map
     */
    public Future<String> getEnvPunishmentList(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(getEnvPunishmentListURL, cusName);
        int envPunishmentListInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("Id");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                envPunishmentListInt = envPunishmentListInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "07");
                map.put("getEnvPunishmentList",envPunishmentListInt);
                System.out.println("getEnvPunishmentList 环保处罚"+envPunishmentListInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 司法协助
     * @param cusName
     * @param map
     */
    public Future<String> getJudicialAssistance(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(getJudicialAssistanceURL, cusName);
        int judicialAssistanceInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String qccId = jo2.getString("ExecutionNoticeNum");
            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                judicialAssistanceInt = judicialAssistanceInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "08");
                map.put("judicialAssistance",judicialAssistanceInt);
                System.out.println("judicialAssistance 司法协助"+judicialAssistanceInt);
            }
        }
        return new AsyncResult<>("");
    }

    /**
     * 经营异常
     * @param cusName
     * @param map
     */
    public Future<String> getOpException(String cusName, Map<String, Integer> map){
        JSONArray ja = sendURL(getOpExceptionURL, cusName);
        int opExceptionInt = 0;
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jo2 = ja.getJSONObject(i);
            String decisionOffice = jo2.getString("DecisionOffice");

            String qccId = decisionOffice;
            String addDate = jo2.getString("AddDate");

            if(null != addDate){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                Timestamp ts = Timestamp.valueOf(addDate);
                String time = sdf.format(ts);
                qccId = qccId + time;
            }

            // 查询有无记录
            QccHistLog qccHistLogEntity = sqlManager.lambdaQuery(QccHistLog.class)
                    .andEq(QccHistLog::getQccId, qccId)
                    .andEq(QccHistLog::getFullName,cusName).single();
            if(null == qccHistLogEntity){
                opExceptionInt = opExceptionInt+1;
                // 保存数据
                saveEntity(cusName,qccId, "09");
                map.put("opException",opExceptionInt);
                System.out.println("opException 经营异常"+opExceptionInt);
            }
        }
        return new AsyncResult<>("");
    }

    private JSONArray sendURL(String URL, String cusName){
        // 发送请求
        String resultString = sendGET(URL, cusName);
        JSONObject objectStr = JSON.parseObject(resultString);

        JSONArray ja = objectStr.getJSONArray("Result");
        return ja;
    }

    // 保存数据
    private void saveEntity(String cusName, String qccId, String type){
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
    private String sendGET(String URL,String cusName){
        String resultString ="";
        try{
            resultString =  HttpUtil.get(URL, C.newMap(
                    "fullName", cusName,"pageIndex","1","pageSize","999"
            ));
        }catch (Exception e){
            e.printStackTrace();
        }

        return resultString;
    }
}
