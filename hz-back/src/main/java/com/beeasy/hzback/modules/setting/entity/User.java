package com.beeasy.hzback.modules.setting.entity;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

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


    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "users")
    private Set<Role> roles;


    /**
     * 得到一个用户所有的工作留
     * @return
     */
    @JSONField(serialize = false)
    public List<WorkFlow> getWorkFlows(){
        return this
                .getRoles()
                .stream()
                .map(role -> role.getDepartment().getWorkFlows())
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 是否属于某个部门
     */
    public boolean isBelongToDepartment(Department department){
        return this.getRoles().stream().anyMatch(role -> role.getDepartment().getId().equals(department.getId()));
    }



}