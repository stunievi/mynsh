package com.beeasy.common.entity;//package com.beeasy.hzback.modules.system.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.JSONConverter;
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

    @Column(unique = true, columnDefinition = JSONConverter.VARCHAR_20)
    private String username;

    @Column(length = 50)
    @JSONField(serialize = false)
    private String password;

    @Column(length = 20)
    private String trueName;
    @Column(length = 20)
    private String phone;
    @Column(length = 50)
    private String email;

    private String letter;

    @CreatedDate
    private Date addTime;

    private boolean baned;
    private boolean su = false;
    private boolean newUser = true;

    String accCode;


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

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<RolePermission> permissions = new ArrayList<>();

    @JSONField(serialize = false)
    @OneToMany(mappedBy = "user")
    private List<MessageRead> readList = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<UserExternalPermission> externalPermissions = new ArrayList<>();

//    @JSONField(serialize = false)
//    @OneToMany(mappedBy = "user")
//    private List<GlobalPermissionCenter> gpCenters = new ArrayList<>();


//    @Transient
//    public boolean hasQuarters(long id){
//        return getQuarters().stream()
//                    .anyMatch(q -> q.getId().equals(id));
//    }



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