package com.beeasy.hzback.modules.system.entity_kt

import com.beeasy.hzback.core.entity.AbstractBaseEntity
import com.beeasy.hzback.core.helper.JSONConverter
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "t_system_file")
@EntityListeners(AuditingEntityListener::class)
class SystemFile : AbstractBaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var fileName: String = ""

    //TODO: 字段类型长度可能错误
    @Column(columnDefinition = JSONConverter.blobType)
    var bytes: ByteArray? = null

    @Enumerated
    var type: Type = Type.TEMP

    @LastModifiedDate
    lateinit var lastModifyTime: Date

    var ext = ""

    @Column(length = 100)
    var filePath = ""

    enum class Type {
        FACE,
        MESSAGE,
        CLOUDDISK,
        WORKFLOW,
        TEMP
    }

    //    boolean removed = false;
}