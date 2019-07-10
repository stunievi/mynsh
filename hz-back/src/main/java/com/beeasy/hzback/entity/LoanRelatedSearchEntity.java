package com.beeasy.hzback.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "T_LOAN_RELATED_SEARCH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRelatedSearchEntity {

    @AssignID("simple")
    Long id;
    String cusName;
    String certType;
    String certCode;
    String operator;
    String mainById;
    Date addTime;
    Double acceptAmt;


}
