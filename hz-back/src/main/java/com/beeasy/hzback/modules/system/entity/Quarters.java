package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "t_quarters")
public class Quarters extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    Department department;
    @Column(name = "department_id")
    Long departmentId;

    String name;
    String dName;

    String info;

    @JSONField(serialize = false)
    @ManyToMany()
    @JoinTable(name = "t_USER_QUARTERS",
            joinColumns = {
                    @JoinColumn(name = "QUARTERS_ID", referencedColumnName = "ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
            }
    )
//    @LazyCollection(LazyCollectionOption.EXTRA)
    Set<User> users = new LinkedHashSet<>();

    String code;

    //是否主管
    boolean manager = false;

}
