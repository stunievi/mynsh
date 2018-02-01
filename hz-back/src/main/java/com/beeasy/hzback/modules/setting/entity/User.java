package com.beeasy.hzback.modules.setting.entity;


import com.beeasy.hzback.core.helper.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotEmpty(message = "用户名不能为空")
    @Column(unique = true)
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;

    @CreatedDate
    private Date addTime;

    private boolean baned;
//    @ManyToMany(fetch=FetchType.EAGER)
//    @JoinTable(name = "t_user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
//            @JoinColumn(name = "role_id") })
////    private List<Role> roleList;// 一个用户具有多个角色


    @ManyToMany(mappedBy = "users")
    private Set<Role> roles;

    public User() {
        super();
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    // 省略 get set 方法


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public boolean isBaned() {
        return baned;
    }

    public void setBaned(boolean baned) {
        this.baned = baned;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}