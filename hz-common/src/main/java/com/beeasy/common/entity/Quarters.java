package com.beeasy.common.entity;//package com.beeasy.hzback.modules.system.entity;
//

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "t_quarters")
public class Quarters extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    Department department;
    @Column(name = "department_id")
    Long departmentId;

    @Column(columnDefinition = JSONConverter.VARCHAR_5O)
    String name;
    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String dName;

    @Column(columnDefinition = JSONConverter.VARCHAR_5O)
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
            List<User> users = new ArrayList<>();

    @Column(length = 50)
    String code;

    //是否主管
    boolean manager = false;

    @OrderBy(value = "DESC")
    int sort;

}
