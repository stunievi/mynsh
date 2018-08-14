package com.beeasy.common.entity;

import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_auto_task_link")
public class AutoTaskLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 20)
    String accCode;

    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String modelName;

    @Column(length = 20)
    @Enumerated(value = EnumType.STRING)
    TaskType type;

    public enum TaskType {
        MINI_WEI,
        SALES_PERSONAL,
        HOME_BANK
    }
}
