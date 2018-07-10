//package com.beeasy.hzback.modules.system.entity;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.beeasy.hzback.core.entity.AbstractBaseEntity;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "t_workflow_ext_permission")
//@AllArgsConstructor
//@NoArgsConstructor
//public class WorkflowExtPermission extends AbstractBaseEntity{
//    public enum Type{
//        //指派者
//        POINTER(0,"指派岗位"),
//        //观察者
//        OBSERVER(1,"观察岗位");
//
//        private int value;
//        private String name;
//        Type(int value, String name){
//            this.value = value;
//            this.name = name;
//        }
//
//        public int getValue() {
//            return value;
//        }
//
//        public String toString(){
//            return this.name;
//        }
//    }
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    //关联工作流
//    @JSONField(serialize = false)
//    @ManyToOne
//    WorkflowModel workflowModel;
//
//    //权限类别
//    @Enumerated
//    Type type;
//
//    //关联岗位
//    Long qid;
//
//}
