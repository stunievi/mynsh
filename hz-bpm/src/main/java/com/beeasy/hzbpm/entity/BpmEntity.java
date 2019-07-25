package com.beeasy.hzbpm.entity;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BpmEntity {


    //要么用ID避免节点递归的情况，
    //要么FASTJSON转成string避免节点递归

    ObjectId formId;

    //表单的原始模板
    String template;

    //表单的展示模板
    String rendered;

    //表单字段配置
    @Deprecated
    Map<String, FormField> fields;

    Map<String, Node> nodes;

    //起始节点ID，只能有一个，多个的情况下禁止保存
    public String start;

    //结束节点ID，只能有一个，多个的情况下禁止保存
    public String end;


    public static class Node{
        //节点ID
        String id;
        //节点对应的表单属性
        NodeExt ext;

        //可以处理的人的ID
        List<Long> uids;

        //可以处理的组织架构ID
        List<Long> qids;
        List<Long> dids;
        List<Long> rids;


        //节点跳转的方向
        List<NextNode> nextNodes = new ArrayList<>();
    }

    /**
     * 在什么情况下可以跳转的节点
     */
    public static class NextNode{
        //节点的跳转条件配置
        //条件表达式
        String expression;
//        Condition condition;

        //需要跳转的节点ID
        String node;
    }


//    public static class Condition{
//
//    }

    /**
     * 节点的表单配置
     */
    public static class NodeExt{
        //所有需要填写的字段name
        List<String> allFields;

        //必填的字段name
        List<String> requiredFields;
    }


    public static class FormField{
        //form表里的字段配置
        /**
         *
         * {
         *                 "name" : "data_1",
         *                 "type" : "text",
         *                 "value" : "宏控件：当前用户姓名",
         *                 "title" : "申请人姓名",
         *                 "leipiplugins" : "macros",
         *                 "orgtype" : "当前用户姓名",
         *                 "expression" : "",
         *                 "orgwidth" : "150",
         *                 "style" : "width: 150px;"
         *             },
         *
         */
    }
}


