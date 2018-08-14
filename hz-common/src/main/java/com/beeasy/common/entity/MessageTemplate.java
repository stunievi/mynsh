package com.beeasy.common.entity;

import com.beeasy.common.helper.JSONConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;


@Getter
@Setter
@Entity
@Table(name = "t_message_template")
public class MessageTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = JSONConverter.type)
    String template;

    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String name;

    //占位符sql语句
    @Column(columnDefinition = JSONConverter.type)
    String placeholder;
}
