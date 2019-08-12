package com.beeasy.hzbpm.entity;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BpmModel {

    //要么用ID避免节点递归的情况，
    //要么FASTJSON转成string避免节点递归

    public ObjectId formId;
    public String workflowName;

    //表单的原始模板
    public String template;

    //表单的展示模板
    public String rendered;

    //表单字段配置
    public Map<String, Map> fields;

    public Map<String, Node> nodes;

    //起始节点ID，只能有一个，多个的情况下禁止保存
    public String start;

    //结束节点ID，只能有一个，多个的情况下禁止保存
    public String end;

    //列表字段
    public List<String> listFields;



    public static class Node{
        //节点ID
        public String id;
        public String name;

        //节点对应的表单属性
        public List<String> allFields;

        //必填的字段name
        public List<String> requiredFields;

        //可以处理的人的ID
        public List<String> uids;

        //可以处理的组织架构ID
        public List<String> qids;
        public List<String> dids;
        public List<String> rids;


        //节点跳转的方向
        public List<NextNode> nextNodes = new ArrayList<>();

        // 超时时间设置
        public TimeoutSet timeoutSet;

        public boolean allowUpload;
        public boolean allowDownload;
        public boolean allowBack;
        public boolean departmentFirst;
    }

    /**
     * 在什么情况下可以跳转的节点
     */
    public static class NextNode{
        //节点的跳转条件配置
        //条件表达式
        public String expression;
//        Condition condition;

        //需要跳转的节点ID
        public String node;
    }

    /**
     * 超时时间设置
     */
    public static class TimeoutSet{
        //超时时间
        public String timeout;

        //最大超时时间
        public String maxTimeout;
    }


//    public static class Condition{
//
//    }

    /**
     * 节点的表单配置
     */
//    public static class NodeExt{
//        //所有需要填写的字段name
//
//    }


//    public static class FormField{
//        //form表里的字段配置
//        /**
//         *
//         * {
//         *                 "name" : "data_1",
//         *                 "type" : "text",
//         *                 "value" : "宏控件：当前用户姓名",
//         *                 "title" : "申请人姓名",
//         *                 "leipiplugins" : "macros",
//         *                 "orgtype" : "当前用户姓名",
//         *                 "expression" : "",
//         *                 "orgwidth" : "150",
//         *                 "style" : "width: 150px;"
//         *             },
//         *
//         */
//        public String name;
//    }
}



