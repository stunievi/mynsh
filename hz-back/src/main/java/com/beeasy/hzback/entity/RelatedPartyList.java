package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

/**
 * 关联方清单表（包含股东关联）
 */
@Table(name="T_RELATED_PARTY_LIST")
@Data
public class RelatedPartyList {
    @AssignID("simple")
    Long id;

    Date addTime;
    String relatedName; //关联人名称
    String linkRule;    // 关联类型
    String linkInfo;   // 关联信息（要素）
    String certCode;    // 关联人证件号码
    String remark_1;      //备注信息1（关系说明）
    String remark_2;      //备注信息2（相关信息）
    String remark_3;      //备注信息3（控制程度）
    String dataFlag;      //数据标志
}
