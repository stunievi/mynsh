//package com.beeasy.hzback.modules.system.entity;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.beeasy.hzback.core.entity.AbstractBaseEntity;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@Table(name = "t_user_ext_permission")
//public class UserExternalPermission extends AbstractBaseEntity{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @JSONField(serialize = false)
//    @ManyToOne
//    User user;
//
//    @Enumerated
//    Permission permission;
//
//
//    public enum Permission{
//        COMMON_CLOUD_DISK(0,"common_cloud_disk");
//
//        private int key;
//        private String value;
//        Permission(int key, String value){
//            this.key = key;
//            this.value = value;
//        }
//
//        public int getValue() {
//            return key;
//        }
//        public String toString(){
//            return value;
//        }
//    }
//}
