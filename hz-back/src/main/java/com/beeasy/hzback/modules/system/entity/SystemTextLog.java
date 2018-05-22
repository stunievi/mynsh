package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "t_system_text_log")
@EntityListeners(AuditingEntityListener.class)
public class SystemTextLog extends AbstractBaseEntity {
    public enum Type{
        WORKFLOW(0);
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

    String content;

    @Enumerated
    Type type;

    Long linkId;

    @CreatedDate
    Date addTime;

    @JSONField(serialize = false)
    @ManyToOne
    User user;

    @Transient
    public Long getUserId(){
        return user == null ? 0 : user.getId();
    }
}
