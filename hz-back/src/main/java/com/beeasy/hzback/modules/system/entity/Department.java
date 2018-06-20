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
    private String info;


    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Department parent;
    @Column(name = "parent_id")
    private Long parentId;

    @CreatedDate
    private Date addTime;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent")
    private List<Department> children = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Quarters> quarters = new LinkedHashSet<>();

    //部门编号
    private String code;

//    @Transient
//    public List<User> getUsers(){
//        return getQuarters()
//                .stream()
//                .map(q -> q.getUsers())
//                .flatMap(Set::stream)
//                .distinct()
//                .collect(Collectors.toList());
//    }

//    @Transient
//    public Long getParentId(){
//        return getParent() == null ? 0 : getParent().getId();
//    }


}
