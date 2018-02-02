package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.SpringContextUtils;
import com.beeasy.hzback.modules.setting.dao.IRoleDao;
import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.print.Collation;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_work_flow")
public class WorkFlow implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JSONField(serialize = false)
    @NotNull(message = "所属部门不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 工作流模型原型
     */
    @Column(columnDefinition = "TEXT")
    @Convert(converter = Work.NodeConverter.class)
    private List<BaseWorkNode> model;


    /**
     * 工作流每个节点对应的可操作人，需和模型原型对应
     */
    @Column(columnDefinition = "TEXT")
    @Convert(converter = Work.NodeConverter.class)
    private List<Set<Integer>> dealers;

    /**
     * 工作流版本
     */
    private Double version;

    /**
     * 工作流名字，和版本一起用于区分具体的业务
     */
    private String name;



    /** converter **/
//    public static class NodeConverter implements AttributeConverter<List<Set<Integer>>,String>{
//
//        @Override
//        public String convertToDatabaseColumn(List<Set<Integer>> dealers) {
//            return JSON.toJSONString(dealers);
//        }
//
//
//        @Override
//        public List<Set<Integer>>convertToEntityAttribute(String s) {
//            IRoleDao roleDao = (IRoleDao) SpringContextUtils.getBean(IRoleDao.class);
//            TypeReference<List<Set<Integer>>> type = new TypeReference<List<Set<Integer>>>(){};
//            List<Set<Integer>> list = JSON.parseObject(s,type);
//
//            return list.stream().map(set -> roleDao.findAll(set)).collect(Collectors.toList());
//        }
//    }



    /** getter and setter **/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<BaseWorkNode> getModel() {
        return model;
    }

    public void setModel(List<BaseWorkNode> model) {
        this.model = model;
    }

    public List<Set<Integer>> getDealers() {
        return dealers;
    }

    public void setDealers(List<Set<Integer>> dealers) {
        this.dealers = dealers;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
