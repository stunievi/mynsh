package com.beeasy.hzback.entity;

import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_QCC_HIS_LOG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QccHistLog extends TailBean implements ValidGroup {

    @AssignID("simple")
    Long id;

    String fullName;
    String qccId;
    String qccOtherId;
    String type;
    Date addTime;

}
