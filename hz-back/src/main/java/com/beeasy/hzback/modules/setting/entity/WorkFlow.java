package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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
    @Column(columnDefinition = "BLOB")
    @Convert(converter = Work.ByteConverter.class)
    private List<BaseWorkNode> model;


    /**
     * 工作流每个节点对应的可操作人，需和模型原型对应
     */
    @Column(columnDefinition = "BLOB")
    @Convert(converter = Work.ByteConverter.class)
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





}
