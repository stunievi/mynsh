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
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "t_department")
@EntityListeners(AuditingEntityListener.class)
public class Department implements Serializable{

    private static final long serialVersionUID = 1L;

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
    private List<Department> departments = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "department")
    private List<Role> roles;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "department")
    private Set<WorkFlow> workFlows;

    @JSONField(serialize = false)
    @Transient
    public Set<User> getUsers(){
        return this.getRoles().stream()
                .map(role -> role.getUsers())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Transient
    public boolean hasRole(String roleName){
        List<Role> roles = this.getRoles();
        for(Role role : roles){
            if(role.getName().equals(roleName)){
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean hasRole(Integer roleId){
        List<Role> roles = this.getRoles();
        for(Role role : roles){
            if(role.getId().equals(roleId)){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Role getRole(Integer roleId){
        List<Role> roles = this.getRoles();
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Set<WorkFlow> getWorkFlows() {
        return workFlows;
    }

    public void setWorkFlows(Set<WorkFlow> workFlows) {
        this.workFlows = workFlows;
    }
}
