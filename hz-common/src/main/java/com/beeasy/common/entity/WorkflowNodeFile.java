package com.beeasy.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_file")
public class WorkflowNodeFile extends AbstractBaseEntity {
    public enum Type {
        FILE(0),
        IMAGE(1),
        VIDEO(2),
        AUDIO(3),
        SIGN(4),
        POSITION(5);

        private int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated
    Type type;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowNodeInstance nodeInstance;

    //关联用户ID
    @JSONField(serialize = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne
    User user;
    @Column(name = "user_id")
    Long userId;

    String fileName;
    String content;
    String ext;

    //关联文件ID
    Long fileId;

    //标签, 空格分割
    String tags = "";

    @Transient
    String token;
}
