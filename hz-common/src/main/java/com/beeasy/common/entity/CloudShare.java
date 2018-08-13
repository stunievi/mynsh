//package com.beeasy.common.entity;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.beeasy.common.helper.AbstractBaseEntity;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import javax.persistence.*;
//import java.util.*;
//
//import java.util.Date;
//
//@Entity
//@Getter
//@Setter
//@Table(name = "t_cloud_share")
//@EntityListeners(AuditingEntityListener.class)
//public class CloudShare extends AbstractBaseEntity{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @ManyToOne
//    CloudDirectoryIndex fileIndex;
//
//    @JSONField(serialize = false)
//    @ManyToOne
//    User toUser;
//
//    @CreatedDate
//    Date addTime;
//}
