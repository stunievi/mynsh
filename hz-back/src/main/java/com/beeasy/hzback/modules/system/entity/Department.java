package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_department")
@EntityListeners(AuditingEntityListener.class)
public class Department extends AbstractBaseEntity{


    @Id
    @GeneratedValue
    private Long id;

    private String name;


    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    @CreatedDate
    private Date addTime;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent")
    private List<Department> children = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Quarters> quarters = new LinkedHashSet<>();



    @JSONField(serialize = false)
    @Transient
    public Set<User> getUsers(){
        return null;
//        return this.getRoles().stream()
//                .map(role -> role.getUsers())
//                .flatMap(Set::stream)
//                .collect(Collectors.toSet());
    }










}
