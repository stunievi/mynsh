package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent")
    private List<Department> departments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "department")
    private List<Role> roles;

    @JSONField(serialize = false)
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "department")
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
    public boolean hasRole(Role role){
        return this.hasRole(role.getId());
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


    /**
     * 该部门是否拥有这个职员
     * @param user
     * @return
     */
    @Transient
    public boolean hasUser(User user){
        for(Role role : this.getRoles()){
            int a = 1;
        }
        return this.getRoles().stream().anyMatch(role -> user.hasRole(role));
    }




}
