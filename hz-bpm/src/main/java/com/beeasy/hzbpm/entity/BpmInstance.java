package com.beeasy.hzbpm.entity;

import com.github.llyb120.nami.json.Obj;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BpmInstance {

    //任务ID
    public ObjectId _id;

    //给人看的ID
    public String id;

    //任务状态
    public String state;

    //工作流xml模型
    public ObjectId bpmId;

    //原始名字
    public String bpmName;

    //用户ID
    public String pubUid;
    //用户名
    public String pubUName;

    //任务所属部门ID, 取用户所在部门ID，用符合授权的那一个，如果有多个符合的，选第一个
    public String depId;
    //任务所属部门名
    public String depName;

    //整理好的数据实体
    public BpmModel bpmModel;

    public Date createTime;
    public Date lastModifyTime;

    //原始xml
    public String xml;

    public List<CurrentNode> currentNodes;


    //所有的属性集中在这里,直接初始化好,每当有人提交数据时，更新这个字段
    /**
     * {
     *     "字段1":"2017年10月10号",
     *     "$字段1"："ISODATE()"
     *     "字段2":"",
     *     "字段3":"",
     * }
     */
    public Map<String,Object> attrs;

//    public List<AddonFile> files;

    //数据提交历史（记录）
    public List<DataLog> logs;

//    public List<HandleLog> handleLogs;

    public static class DataLog{
        public ObjectId id;

        //当时所在的节点ID
        public String nodeId;

        //提交到该节点上的信息，通常是节点名
        public String msg;

        //提交时间
        public Date time;

        public Date startTime;
        public Date endTime;

        //提交人
        public String uid;
        public String uname;

//        public Map<String,String> aaaa;

        //日志类型
        public String type;

        //...
        //提交人是以什么身份进行提交的（当时这个节点的授权）

        //当时这个人提交的所有数据
        public Map<String,Object> attrs;

        public List<AddonFile> files;
    }

//    public static class HandleLog{
//        public String nodeId;
//        public String nodeName;
//        public String uid;
//        public String uname;
//        public Date startDate;
//        public Date endDate;
//    }

    public static class CurrentNode{
        //当前节点的ID
        public String nodeId;
        public String nodeName;

        //提交到该节点上的信息，通常是节点名
        public String msg;

        //当前正在处理这个节点的人
        public List<String> uids;
        public List<String> unames;

        //主办人
        public Map<String,String> mainUsers;
        //经办人
        public Map<String,String> supportUsers;


        // 当前时间
//        public Date nowTime;

        // 超时提醒时间
        public Date timeout;

        // 最大超时提醒时间
        public Date maxTimeout;

    }


    public static class AddonFile{
        public String id;
        public String name;
        public String creator;
        public String action;
    }
}
