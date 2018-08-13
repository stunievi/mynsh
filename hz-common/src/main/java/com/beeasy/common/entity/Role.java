package com.beeasy.common.entity;//package com.beeasy.hzback.modules.system.entity;
//
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_role")
public class Role extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //角色名
    String name;

    //角色说明
    String info;

    //排序
    @OrderBy(value = "DESC")
    int sort = 0;

    //是否可以删除
    boolean canDelete = true;

    @JSONField(serialize = false)
    @ManyToMany()
    @JoinTable(name = "t_user_role",
            joinColumns = {
                    @JoinColumn(name = "role_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id")
            }
    )
    List<User> users = new ArrayList<>();
}
