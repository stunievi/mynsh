package com.beeasy.hzback.modules.system.entity;

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
    @GeneratedValue
    Integer id;

    String name;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = StringConverter.class)
    String template;


}
