package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.JSONConverter;
import com.beeasy.hzback.core.helper.StringConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_message_template")
public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String template;

    String name;

    //占位符sql语句
    @Column(columnDefinition = JSONConverter.type)
    String placeholder;
}
