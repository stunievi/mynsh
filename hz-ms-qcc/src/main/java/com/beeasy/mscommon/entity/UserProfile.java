package com.beeasy.mscommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.Table;

@Table(name = "T_USER_PROFILE")
@Getter
@Setter
public class UserProfile extends TailBean {
    Long id;
    Long faceId;
    Long userId;
}
