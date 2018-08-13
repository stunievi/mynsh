//package com.beeasy.common.entity;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.beeasy.common.helper.AbstractBaseEntity;
//import lombok.Getter;
//import lombok.Setter;
//import javax.persistence.*;
//import java.util.*;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "t_cloud_tag")
//public class CloudFileTag extends AbstractBaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @JSONField(serialize = false)
//    @ManyToOne
//    CloudDirectoryIndex index;
//
//    String tag;
//}
