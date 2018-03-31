package com.beeasy.hzback.modules.setting.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.SystemMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;

    private String phone;
    private String email;

    @CreatedDate
    private Date addTime;

    private boolean baned;
//    @ManyToMany(fetch=FetchType.EAGER)
//    @JoinTable(nodeName = "t_user_role", joinColumns = { @JoinColumn(nodeName = "user_id") }, inverseJoinColumns = {
//            @JoinColumn(nodeName = "role_id") })
////    private List<Role> roleList;// 一个用户具有多个角色


    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "users")
    private transient Set<Role> roles;

    @JSONField(serialize = false)
    @ManyToMany
    @JoinTable(name = "t_USER_QUARTERS",
            joinColumns = {
                    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "QUARTERS_ID", referencedColumnName = "ID")
            })
    private List<Quarters> quarters = new ArrayList<>();


    @ManyToMany(mappedBy = "users")
    private transient List<SystemMenu> systemMenus;

    @OneToOne(mappedBy = "user")
    private transient UserProfile profile;
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


    @Transient
    public boolean hasRole(Role role){
        return this.getRoles().stream().anyMatch(r -> r.getId().equals(role.getId()));
    }

    @Transient
    public boolean hasQuarters(Integer id){
        return getQuarters().stream()
                    .anyMatch(q -> q.getId().equals(id));
    }



}