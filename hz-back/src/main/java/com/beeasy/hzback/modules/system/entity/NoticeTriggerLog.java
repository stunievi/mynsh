package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_notice_trigger_log")
public class NoticeTriggerLog extends AbstractBaseEntity{
    @Id
    @GeneratedValue
    Long id;

    //上次触发时间
    Date triggerTime;

    //对应规格名
    String ruleName;

    //对应主键
    String uuid;

}
