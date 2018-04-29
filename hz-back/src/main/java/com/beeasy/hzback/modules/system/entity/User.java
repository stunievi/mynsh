package com.beeasy.hzback.modules.system.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.service.IUserService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


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

    @CreatedDate
    private Date addTime;

    private boolean baned;
//    @ManyToMany(fetch=FetchType.EAGER)
//    @JoinTable(nodeName = "t_user_role", joinColumns = { @JoinColumn(nodeName = "user_id") }, inverseJoinColumns = {
//            @JoinColumn(nodeName = "role_id") })
////    private List<Role> roleList;// 一个用户具有多个角色




    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<Quarters> quarters = new LinkedHashSet<>();

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RolePermission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<MessageRead> readList = new ArrayList<>();

//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    private List<CloudDirectoryIndex> folders = new ArrayList<>();

    /**
     * 得到一个用户所有的工作留
     * @return
     */
//    @JSONField(serialize = false)
//    public List<WorkFlow> getWorkFlows(){
//        return this
//                .getRoles()
//                .stream()
//                .map(role -> role.getDepartment().getWorkFlows())
//                .flatMap(Set::stream)
//                .distinct()
//                .collect(Collectors.toList());
//    }

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

    @JSONField(serialize = false)
    @Transient
    public List<Department> getDepartments(){
        List<Department> departments = getQuarters().stream()
               .map(q -> q.getDepartment())
                .distinct()
                .collect(Collectors.toList());
        return departments;
    }



}