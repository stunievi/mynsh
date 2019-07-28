package com.beeasy.hzbpm.entity;

import com.github.llyb120.nami.json.Obj;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BpmInstance extends Obj {

    //任务ID
    public ObjectId _id;

    //任务状态
    public String state;

    //工作流xml模型
    public ObjectId bpmId;

    //原始名字
    public String bpmName;

    //用户ID
    public Long pubUid;
    //用户名
    public String pubUName;

    //任务所属部门ID, 取用户所在部门ID，用符合授权的那一个，如果有多个符合的，选第一个
    public String depId;
    //任务所属部门名
    public String depName;

    //整理好的数据实体
    public BpmModel bpmModel;

    public Date createTime;
    public Date lastMoidfyTime;

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
    public Map<String,String> attrs;


    //数据提交历史（记录）
    public List<DataLog> logs;


    static class DataLog{
        //当时所在的节点ID
        String nodeId;

        //提交时间
        Date time;

        //提交人
        Long uid;

        //...
        //提交人是以什么身份进行提交的（当时这个节点的授权）

        //当时这个人提交的所有数据
        Map<String,String> attrs;
    }


    public static class CurrentNode{
        //当前节点的ID
        public String nodeId;

        //当前正在处理这个节点的人
        public List<Long> uids;
    }
}
