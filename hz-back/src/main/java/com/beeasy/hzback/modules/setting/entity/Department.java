package com.beeasy.hzback.modules.setting.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "t_department")
@EntityListeners(AuditingEntityListener.class)
public class Department implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty(message = "部门名字不能为空")
    @Column(unique = true)
    private String name;

//    private Integer parentId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    public Department parent;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent")
    private Set<Department> departments = new HashSet<Department>(0);


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }
}
