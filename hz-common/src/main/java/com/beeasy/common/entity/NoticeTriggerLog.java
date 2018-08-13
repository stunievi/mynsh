package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;
import java.util.*;
@Entity
@Getter
@Setter
@Table(name = "t_notice_trigger_log", indexes = {
    @Index(name = "scan", columnList = "triggerTime,ruleName,uuid")
})
public class NoticeTriggerLog extends AbstractBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //上次触发时间
    Date triggerTime;

    //对应规格名
    String ruleName;

    //对应主键
    String uuid;

}
