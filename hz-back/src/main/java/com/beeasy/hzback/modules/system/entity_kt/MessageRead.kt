package com.beeasy.hzback.modules.system.entity_kt

import com.alibaba.fastjson.annotation.JSONField
import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.modules.system.entity.Message
import javax.persistence.*

@Entity
@Table(name = "t_message_read")
class MessageRead : AbstractBaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //    用户ID(这里不需要使用关联)
    @JSONField(serialize = false)
    @ManyToOne
    lateinit var user: User

    //    Long fromId = 0L;
    var toId: Long? = null
    //    Message.LinkType fromType = Message.LinkType.USER;
    var toType: Message.LinkType = Message.LinkType.USER

    var unreadNum = 0

    //会话ID, 不使用关联
    //    @ManyToOne
    //    MessageSession session;
}