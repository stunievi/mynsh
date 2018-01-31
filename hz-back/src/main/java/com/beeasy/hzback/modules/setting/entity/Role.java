package com.beeasy.hzback.modules.setting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.util.Set;

@Entity
@Table(name = "t_role")
public class Role {

    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty(message = "角色名不能为空")
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Min(message = "需要最少一个职位", value = 1)
    private Integer max;

    @ManyToMany()
    @JoinTable(name = "t_USER_ROLE",joinColumns = {
            @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private Set<User> users;

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
