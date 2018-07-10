package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_message")
@EntityListeners(AuditingEntityListener.class)
public class Message extends AbstractBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long fromId;
    Long toId;

    @Enumerated
    LinkType fromType;
    @Enumerated
    LinkType toType;

    //消息类型, 文本/图片/文件/小视频/定位 等
    @Enumerated
    Type type = Type.TEXT;
    Long linkId;

    @CreatedDate
    Date sendTime;

    String content = "";
    String uuid;

    //PC端公共的UUID
    String commonUUID;


    public enum LinkType{
        USER(0);

        private int value;
        LinkType(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public enum Type{
        TEXT(0),
        FILE(1);

        private int value;
        Type(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

}
