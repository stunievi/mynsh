package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_system_menu")
public class SystemMenu {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer parentId;
    private String name;
    private String href;
    private Integer sort;

    @JSONField(serialize = false)
    @ManyToMany()
    @JoinTable(name = "t_user_menu",joinColumns = {
            @JoinColumn(name = "MENU_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private List<User> users;


}
