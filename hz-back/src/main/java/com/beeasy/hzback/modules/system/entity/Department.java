package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_department")
@EntityListeners(AuditingEntityListener.class)
//@SQLDelete(sql = "update demo set deleted = 1 where id = ?")
//@Where(clause = "deleted = 0")
public class Department extends AbstractBaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OrderBy(value = "sort ASC, id ASC")
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "parent")
    private List<Department> children = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<Quarters> quarters = new LinkedHashSet<>();

    //部门编号
    private String code;

    private int sort;

    String accCode;
//    private boolean deleted = false;

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
