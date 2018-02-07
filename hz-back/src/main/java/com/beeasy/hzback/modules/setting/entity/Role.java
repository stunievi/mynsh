package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_role")
public class Role implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty(message = "角色名不能为空")
    private String name;

    @JSONField(serialize = false)
    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;

    @Min(message = "需要最少一个职位", value = 1)
    private Integer max;

    @JSONField(serialize = false)
    @ManyToMany()
    @JoinTable(name = "t_USER_ROLE",joinColumns = {
            @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private Set<User> users;

}
