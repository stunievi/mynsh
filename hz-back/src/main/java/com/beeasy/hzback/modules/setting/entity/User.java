package com.beeasy.hzback.modules.setting.entity;


import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
@Data
public class User {

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

    public User() {
        super();
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    // 省略 get set 方法

//    @Transient
//    public Set<String> getRolesName() {
//        List<Role> roles = getRoleList();
//        Set<String> set = new HashSet<String>();
//        for (Role role : roles) {
//            set.add(role.getRolename());
//        }
//        return set;
//    }

}