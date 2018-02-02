package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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
    private String name;

//    private Integer parentId;


    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "parent")
    private List<Department> departments = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "department")
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "department")
    private Set<WorkFlow> workFlows = new HashSet<>();

    @JSONField(serialize = false)
    @Transient
    public Set<User> getUsers(){
        Set<Role> roles = this.getRoles();
        Set<User> result = new HashSet<>();
        for(Role role : roles){
            result.addAll(role.getUsers());
        }
        return result;
    }

    @Transient
    public boolean hasRole(String roleName){
        Set<Role> roles = this.getRoles();
        for(Role role : roles){
            if(role.getName().equals(roleName)){
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean hasRole(Integer roleId){
        Set<Role> roles = this.getRoles();
        for(Role role : roles){
            if(role.getId().equals(roleId)){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Role getRole(Integer roleId){
        Set<Role> roles = this.getRoles();
        for(Role role : roles){
            if(role.getId().equals(roleId)){
                return role;
            }
        }
        return null;
    }


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

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<WorkFlow> getWorkFlows() {
        return workFlows;
    }

    public void setWorkFlows(Set<WorkFlow> workFlows) {
        this.workFlows = workFlows;
    }
}
