package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_system_variable")
public class SystemVariable extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, columnDefinition = JSONConverter.VARCHAR_5O)
    String varName;

    String varValue;

    //是否可以删除
    boolean canDelete = true;
}
