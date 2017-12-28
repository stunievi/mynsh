package com.beeasy.hzback.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 权限（增删改查等）
 *
 * @author 单红宇(365384722)
 * @myblog http://blog.csdn.net/catoop/
 * @create 2016年1月13日
 */
@Entity
@Table(name = "t_permission")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String permissionname;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;// 一个权限对应一个角色

    // 省略 get set

}
