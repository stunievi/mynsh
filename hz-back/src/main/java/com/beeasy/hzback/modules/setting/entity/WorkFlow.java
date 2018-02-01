package com.beeasy.hzback.modules.setting.entity;

import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "t_work_flow")
public class WorkFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "所属部门不能为空")
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
    private List<Set<User>> dealers;

    /**
     * 工作流版本
     */
    private Double version;



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

    public List<Set<User>> getDealers() {
        return dealers;
    }

    public void setDealers(List<Set<User>> dealers) {
        this.dealers = dealers;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }


}
