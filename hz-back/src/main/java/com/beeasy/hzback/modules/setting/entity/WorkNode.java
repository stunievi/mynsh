package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;
import com.beeasy.hzback.modules.setting.work_engine.ShenheNode;
import com.beeasy.hzback.modules.setting.work_engine.ZiliaoNode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.beetl.ext.fn.Json;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
//
@Entity
@Table(name = "t_work_node")
public class WorkNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cubiid;
}
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
////    private String type;
//
//    @Column(columnDefinition = "TEXT")
//    @Convert(converter = NodeConverter.class)
//    private BaseWorkNode data;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "work_id")
//    @JsonBackReference
//    private Work work;
//
//
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
////    public String getType() {
////        return type;
////    }
////
////    public void setType(String type) {
////        this.type = type;
////    }
//
//    public BaseWorkNode getData() {
//        return data;
//    }
//
//    public void setData(BaseWorkNode data) {
//        this.data = data;
//    }
//
//    public Work getWork() {
//        return work;
//    }
//
//    public void setWork(Work work) {
//        this.work = work;
//    }
//}
