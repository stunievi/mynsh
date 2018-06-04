package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_instance_attribute")
public class WorkflowInstanceAttribute extends AbstractBaseEntity {
    @GeneratedValue
    @Id
    Long id;

    @JSONField(serialize = false)
    @ManyToOne
    WorkflowInstance instance;

    @Enumerated
    Type type;

    String attrKey;
    String attrValue;
    String attrCName;


    public enum Type{
        //固有
        INNATE(0,"固有");

        private int value;
        private String v;
        Type(int k, String v){
            this.value = k;
            this.v = v;
        }
        public int getValue() {
            return value;
        }
        public String toString(){
            return v;
        }
    }

    interface Dao extends JpaRepository<WorkflowInstanceAttribute,Long>{

    }

}
