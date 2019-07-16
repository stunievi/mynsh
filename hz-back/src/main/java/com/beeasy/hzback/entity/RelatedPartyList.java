package com.beeasy.hzback.entity;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Data;
import lombok.Getter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.C;
import org.osgl.util.S;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 关联方清单表（包含股东关联）
 */
@Table(name="T_RELATED_PARTY_LIST")
@Data
@Getter
public class RelatedPartyList extends ValidGroup {
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
