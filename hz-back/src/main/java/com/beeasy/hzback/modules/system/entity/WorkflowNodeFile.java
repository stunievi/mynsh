package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_node_file")
public class WorkflowNodeFile extends AbstractBaseEntity{
    public enum Type{
        FILE(0),
        IMAGE(1),
        VIDEO(2),
        AUDIO(3),
        SIGN(4)
        ;

        private int value;
        Type(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue
    Long id;

    @Enumerated
    Type type;

    String fileName;

    //关联文件ID
    Long fileId;
}
