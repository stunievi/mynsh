package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.JSONConverter;
import com.beeasy.hzback.core.helper.Object2Array;
import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.core.helper.StringConverter;
import javafx.util.converter.ByteStringConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "t_system_log")
@EntityListeners(AuditingEntityListener.class)
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;
    String userName;
    String method;

    @Column(columnDefinition = JSONConverter.type)
    @Convert(converter = JSONConverter.class)
    Object params;

    @CreatedDate
    Date addTime;
//    String ip;

}
