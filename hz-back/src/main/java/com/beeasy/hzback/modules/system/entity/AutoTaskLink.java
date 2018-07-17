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
        MINI_WEI,
        SALES_PERSONAL,
        HOME_BANK
    }
}
