package com.beeasy.hzback.modules.system.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.SysNotice;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.entity.WfNodeDealer;
import com.beeasy.hzback.entity.WorkflowTask;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.mscommon.util.U;
import org.apache.camel.json.simple.JsonArray;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class WorkflowTaskService {
    @Autowired
    private SQLManager sqlManager;
    @Autowired
    NoticeService2 noticeService2;

//    @Scheduled(cron = "0 0/1 * * * ?")
    public void workflowTask() {

        List<JSONObject> res = (sqlManager.select("workflow.selectDealParam", JSONObject.class, C.newMap()));

        for (JSONObject re : res) {
            Iterator<String> sIterator = re.keySet().iterator();
            while (sIterator.hasNext()) {
                // 获得key
                String key = sIterator.next();

                if ("model".equals(key)) {

                    String nodeName = re.getString("currentNodeName");

                    Long id = re.getLong("id");
                    Long currentNodeInstanceId = re.getLong("currentNodeInstanceId");

                    String ja1 = re.getString(key);

                    JSONObject jsStr = JSONObject.parseObject(ja1);
                    JSONArray flow = jsStr.getJSONArray("flow");

                    for (Object obj : flow) {
                        JSONObject jo = (JSONObject) obj;
                        String noticeInterval = jo.getString("notice_interval");
                        String timeout = jo.getString("timeout");
                        if (null != noticeInterval && null != timeout) {

                            String name = jo.getString("name");
                            if (nodeName.equals(name)) {

                                int outDay = trans(timeout);
                                int outHours = trans(noticeInterval);
                                JSONArray ja2 = re.getJSONArray("nodes");
                                for (Object obj2 : ja2) {

                                    JSONObject jo2 = (JSONObject) obj2;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String timeFormat = sdf.format(jo2.getDate("addTime"));

                                    LocalDateTime nowDate = LocalDateTime.now();
                                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    LocalDateTime ldt = LocalDateTime.parse(timeFormat, df);
                                    Duration duration1 = Duration.between(ldt, nowDate);
                                    long min = duration1.toMinutes();

                                    /*LocalDate beginDate = LocalDate.of(y,m,d);
                                    Period p = Period.between(beginDate, LocalDate.now());*/
                                    //  如果天数大于配置天数
                                    if (min > outDay) {
//                                    if(p.getDays()>outDay){

                                        WorkflowTask entityTask = sqlManager.lambdaQuery(WorkflowTask.class).andEq(WorkflowTask::getTaskId, id).single();

                                        if (null == entityTask) {
                                            saveEntity(id, currentNodeInstanceId);
                                        } else {
                                            WorkflowTask workflow = sqlManager.lambdaQuery(WorkflowTask.class).andEq(WorkflowTask::getTaskId, id).andEq(WorkflowTask::getCurrentNodeInstanceId, currentNodeInstanceId).single();
                                            if (null != workflow) {

                                                Date da = workflow.getRemindTime();
                                                // 如果时间跟当前时间比较大于等于配置时间，则更新时间，发送消息提醒
                                                Instant instant = da.toInstant();
                                                ZoneId zoneId = ZoneId.systemDefault();

                                                LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
//                                                LocalDateTime now = LocalDateTime.now().plusDays(5);
                                                LocalDateTime now = LocalDateTime.now();
                                                Duration duration = Duration.between(localDateTime, now);
                                                long min2 = duration.toMinutes();//相差的分钟数

                                                String noticeTimeOut = jo.getString("notice_interval_out");
                                                if(null != noticeTimeOut){
                                                    long noticeTime = trans(noticeTimeOut);
                                                    Date taskTime = workflow.getTaskStartTime();
                                                    if(null != taskTime){
                                                        Instant instant2 = taskTime.toInstant();
                                                        ZoneId zoneId2 = ZoneId.systemDefault();
                                                        // 当前时间
                                                        LocalDateTime tNowTime = LocalDateTime.now();

                                                        LocalDateTime tTime = instant2.atZone(zoneId2).toLocalDateTime();
                                                        // 任务到期时间
                                                        LocalDateTime eTime = tTime.plusMinutes(noticeTime);

                                                        if(tNowTime.isAfter(eTime)){
                                                            break;
                                                        }
                                                    }

                                                }

                                                if (min2 >= outHours) {
//                                                    sqlManager.lambdaQuery(WorkflowTask.class).andEq(WorkflowTask::getTaskId, id).delete();
//                                                    saveEntity(id, currentNodeInstanceId);
                                                    sqlManager.lambdaQuery(WorkflowTask.class).andEq(WorkflowTask::getTaskId,id).andEq(WorkflowTask::getCurrentNodeInstanceId,currentNodeInstanceId).updateSelective(new WorkflowTask(){{setRemindTime(new Date());}});

                                                    // 发送消息
                                                    List<SysNotice> notices = new ArrayList<>();

                                                    List<JSONObject> deal = (sqlManager.select("workflow.得到任务节点审批人", JSONObject.class, C.newMap("insId", id, "nodeId", currentNodeInstanceId)));
                                                    for (JSONObject de : deal) {
                                                        long days = min / (60 * 24);
                                                        String str = "";
                                                        if(0 >= days){
                                                            str = "长时间";
                                                        }else if(0<days){
                                                            str = "超过"+days + "天";
                                                        }
                                                        Long uid = de.getLong("uid");
                                                        String renderStr = "您有任务" + str + "未处理，请及时处理！";
                                                        notices.add(
                                                                noticeService2.makeNotice(SysNotice.Type.WORKFLOW, uid, renderStr, C.newMap("taskId", id + ""))
                                                        );
                                                    }
                                                    if (notices.size() > 0) {
                                                        sqlManager.insertBatch(SysNotice.class, notices);
                                                    }
                                                }

                                            } else {
                                                sqlManager.lambdaQuery(WorkflowTask.class).andEq(WorkflowTask::getTaskId, id).delete();
                                                saveEntity(id, currentNodeInstanceId);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void saveEntity(Long id, Long currentNodeInstanceId) {
        WorkflowTask entity = new WorkflowTask();
        entity.setTaskId(id);
        entity.setCurrentNodeInstanceId(currentNodeInstanceId);
        entity.setRemindTime(new Date());
        entity.setId(U.getSnowflakeIDWorker().nextId());
        entity.setTaskStartTime(new Date());
        sqlManager.insert(entity);
    }

    public int trans(String request) {
        int re = 0;
        if ("".equals(request) || null == request) {
            return re;
        }
        String fl = request.substring(request.length() - 1, request.length());
        String pa = request.substring(0, request.length() - 1);
        switch (fl) {
            case "d":
                re = Integer.parseInt(pa) * 24 * 60;
                break;
            case "h":
                re = Integer.parseInt(pa) * 60;
                break;
            case "m":
                re = Integer.parseInt(pa);
                break;
            default:
                re = 0;
        }
        return re;
    }

    public void sendUrge(Long id){
        // 任务实例
        WfIns entity = sqlManager.lambdaQuery(WfIns.class).andEq(WfIns::getId,id).single();

        String a = entity.getNodes();
        JSONArray json = (JSONArray) JSONArray.parse(a);
        Object ob = json.get(json.size()-1);

        JSONObject jObject = (JSONObject) ob;
        Long nodesId = jObject.getLong("id");

        // 任务处理人
        WfNodeDealer dealerEntity = sqlManager.lambdaQuery(WfNodeDealer.class).andEq(WfNodeDealer::getInsId,id).andEq(WfNodeDealer::getNodeId,nodesId).single();

        if(null == dealerEntity){
            return;
        }
        Long uid = dealerEntity.getUid();

        String renderStr = "您有任务长时间未处理，请及时处理！";
        List<SysNotice> notices = new ArrayList<>();
        notices.add(
                noticeService2.makeNotice(SysNotice.Type.WORKFLOW, uid, renderStr, C.newMap("taskId", id + ""))
        );
        sqlManager.insertBatch(SysNotice.class, notices);

    }
}
