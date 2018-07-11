package com.beeasy.hzback.modules.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_auto_task_link")
public class AutoTaskLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String accCode;

    String modelName;

    @Enumerated(value = EnumType.STRING)
    TaskType type;

    public enum TaskType{
        MINI_WEI_PERSONAL,
        MINI_WEI_COMPANY,
        SALES_PERSONAL_MORTGAGE,
        SALES_PERSONAL_CONSUME,
        SALES_PERSONAL_MANAGE,
        HOME_BANK
    }
}
