package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.core.helper.ObjectConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "t_system_task")
public class SystemTask extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    Date runTime;
//
    String className;
//
    String taskKey;
//
    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Map<String,Object> params = new HashMap();
//
    Boolean threadLock = false;

    Date lockTime;

}
