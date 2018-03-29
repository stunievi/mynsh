package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "t_quarters")
public class Quarters extends AbstractBaseEntity {

    @Id
    @GeneratedValue
    Integer id;

    @JSONField(serialize = false)
    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    String name;

    String info;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "quarters")
//    @LazyCollection(LazyCollectionOption.EXTRA)
    List<User> users;

}
