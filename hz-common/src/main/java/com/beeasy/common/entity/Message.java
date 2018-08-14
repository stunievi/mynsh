package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import com.beeasy.common.helper.JSONConverter;
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
public class Message extends AbstractBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long fromId;
    Long toId;

    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String fromName;
    @Column(columnDefinition = JSONConverter.VARCHAR_20)
    String toName;

    @Enumerated
    LinkType fromType;
    @Enumerated
    LinkType toType;

    //消息类型, 文本/图片/文件/小视频/定位 等
    @Column(length = 20)
    @Enumerated
    Type type = Type.TEXT;
    Long linkId;

    @CreatedDate
    Date sendTime;

    String content = "";

    @Column(length = 50)
    String uuid;

    public enum LinkType {
        USER(0);

        private int value;

        LinkType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public enum Type {
        TEXT(0),
        FILE(1);

        private int value;

        Type(int value) {
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
