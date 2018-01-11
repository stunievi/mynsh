package com.beeasy.hzback.modules.setting.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_department")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Department {
    @Id
    @GeneratedValue
    private Integer id;

    @NotEmpty(message = "部门名字不能为空")
    private String name;

    private Integer parentId;

    @Transient
    private List<Department> childrenDeparment;

}
