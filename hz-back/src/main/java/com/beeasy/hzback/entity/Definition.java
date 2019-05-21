package com.beeasy.hzback.entity;


import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "t_defination")
@Getter
@Setter
public class Definition {
    @AssignID("simple")
    Long id;

    String name;
    String config;
}
