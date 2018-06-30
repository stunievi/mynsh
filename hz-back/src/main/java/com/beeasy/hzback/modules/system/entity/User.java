package com.beeasy.hzback.modules.system.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.helper.JSONConverter;
import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.modules.system.service.IUserService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Getter
@Setter
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @JSONField(serialize = false)
    private String password;

    private String trueName;
    private String phone;
    private String email;

    private String letter;

    @CreatedDate
    private Date addTime;

    private boolean baned;


    @JSONField(serialize = false)
//    @Convert(converter = ObjectConverter.class)
//    @Column(columnDefinition = "BLOB")
    @Column(columnDefinition = JSONConverter.type)
    private String publicKey;

    @JSONField(serialize = false)
//    @Convert(converter = ObjectConverter.class)
//    @Column(columnDefinition = "BLOB")
    @Column(columnDefinition = JSONConverter.type)
    private String privateKey;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Quarters> quarters = new ArrayList<>();

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "users")
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RolePermission> permissions = new ArrayList<>();

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "user")
    private List<MessageRead> readList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserExternalPermission> externalPermissions = new ArrayList<>();

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "user")
    private List<GlobalPermissionCenter> gpCenters = new ArrayList<>();

    @JSONField(serialize = false)
    @Transient
    public boolean hasQuarters(long id){
        return getQuarters().stream()
                    .anyMatch(q -> q.getId().equals(id));
    }


    @JSONField(serialize = false)
    @Transient
    public Optional<RolePermission> getMethodPermission(){
        return getPermissions()
                .stream()
                .filter(rolePermission -> rolePermission.getType().equals(IUserService.PermissionType.METHOD))
                .findAny();
    }

    @JSONField(serialize = false)
    @Transient
    public Optional<RolePermission> getMenuPermission(){
        return getPermissions()
                .stream()
                .filter(rolePermission -> rolePermission.getType().equals(IUserService.PermissionType.MENU))
                .findAny();
    }

//    @Transient
//    public List<Department> getDepartments(){
//        List<Department> departments = getQuarters().stream()
//               .map(q -> q.getDepartment())
//                .collect(Collectors.toList());
//        return departments;
//    }

//    @Transient
//    public List<Long> getDepartmentIds(){
//        return getQuarters().stream()
//                .map(q -> q.getDepartment().getId())
//                .collect(Collectors.toList());
//    }
//
//    @Transient
//    public List<Long> getQuartersIds(){
//        return getQuarters().stream().map(Quarters::getId).collect(Collectors.toList());
//    }




}