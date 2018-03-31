package com.beeasy.hzback.modules.setting.entity;

import javax.persistence.*;

//import com.sun.xml.internal.rngom.parse.host.Base;
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
//    @JoinColumn(nodeName = "work_id")
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
