01
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.CUS_ID,
    p1.CUS_NAME,
    p1.CUST_TYPE,
    p1.CERT_TYPE,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    p1.PSN_CERT_CODE
    else
    p1.ENT_CERT_CODE
    end as CERT_CODE,
    p1.ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.PRD_TYPE,
    p1.CLA,
    p1.LOAN_CLA4,
    p1.SEVEN_RESULT,
    p1.USE_DEC,
    p1.ACCOUNT_STATUS,
    p1.INDIV_RSD_ADDR,
    p1.REPAYMENT_MODE,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    p1.UNPD_PRIN_BAL,
    p1.DELAY_INT_CUMU,
    p1.CUST_MGR,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    u1.PHONE,
    u1.CONTACT_NAME,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    u2.COM_CRD_GRADE
    else
    u3.CRD_GRADE
    end as CRD_GRADE
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
where p1.CREUNIT_NO = '0801'

-- 台帐分类（普通贷款/银团贷款/垫款/贴现/转贴现/银承/保函/置换贷款）(sql语句直接用中文名进行拼接)
@if(isNotEmpty(LN_TYPE)){
    and p1.LN_TYPE like #'%' + LN_TYPE + '%'#
@}

-- 贷款帐号
@if(isNotEmpty(LOAN_ACCOUNT)){
    and p1.LOAN_ACCOUNT like like #'%' + LOAN_ACCOUNT + '%'#
@}

-- 借据号
@if(isNotEmpty(BILL_NO)){
    and p1.BILL_NO like #'%' + BILL_NO + '%'#
@}

-- 合同号
@if(isNotEmpty(CONT_NO)){
    and p1.CONT_NO like #'%' + CONT_NO + '%'#
@}

-- 客户号
@if(isNotEmpty(CUS_ID)){
    and p1.CUS_ID like #'%' + CUS_ID + '%'#
@}

-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}

-- 台账状态
@if(isNotEmpty(ACCOUNT_STATUS)){
    and p1.ACCOUNT_STATUS= #ACCOUNT_STATUS#
@}

-- 五级分类标志
@if(isNotEmpty(CLA)){
    and p1.CLA = #CLA#
@}

-- 是否逾期
@if(timeout == '1' || timeout == 1){
    and (p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL)
@}else if(timeout == '0' || timeout == 0){
    and not (p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL)
@}

-- 联系人
@if(isNotEmpty(CONTACT_NAME)){
    and u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'#
@}

-- 联系电话
@if(isNotEmpty(PHONE)){
    and u1.PHONE like like #'%' + PHONE + '%'#
@}

-- 数据可视范围
@if(null != deplimit){
    and (p1.CUST_MGR in (#join(userlimit)#) or p1.MAIN_BR_ID in (#join(deplimit)#) )
@}


02
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.CUS_ID,
    p1.CUS_NAME,
    p1.CUST_TYPE,
    p1.CERT_TYPE,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    p1.PSN_CERT_CODE
    else
    p1.ENT_CERT_CODE
    end as CERT_CODE,
    p1.ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.PRD_TYPE,
    p1.CLA,
    p1.LOAN_CLA4,
    p1.SEVEN_RESULT,
    p1.USE_DEC,
    p1.ACCOUNT_STATUS,
    p1.INDIV_RSD_ADDR,
    p1.REPAYMENT_MODE,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    p1.UNPD_PRIN_BAL,
    p1.DELAY_INT_CUMU,
    p1.CUST_MGR,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    u1.PHONE,
    u1.CONTACT_NAME,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    u2.COM_CRD_GRADE
    else
    u3.CRD_GRADE
    end as CRD_GRADE
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT= #LOAN_ACCOUNT#

-- 数据可视范围
@if(null != deplimit){
    and (p1.CUST_MGR in (#join(userlimit)#) or p1.MAIN_BR_ID in (#join(deplimit)#) )
@}

03
===
select
@pageTag(){
    p1.LOAN_ACCOUNT,
    g1.GUAR_CONT_NO,
    g1.GUAR_CONT_TYPE,
    g1.GUAR_WAY,
    g1.BORROWER_NO,
    g1.BORROWER_NAME,
    g1.GUAR_NO,
    g1.GUAR_NAME,
    g1.CER_NO,
    g1.CER_TYPE,
    g1.CUR_TYPE,
    g1.GUAR_AMT,
    g1.USED_AMT,
    g1.GUAR_TERM,
    g1.GUAR_START_DATE,
    g1.GUAR_END_DATE,
    g1.GUAR_CONT_STATE,
    g1.CUSTOMER_MGR,
    g1.MAIN_BR_ID
@}
from RPT_M_RPT_SLS_ACCT as p1
left join GRT_LOANGUAR_INFO as g5 on p1.CONT_NO=g5.CONT_NO
left join GRT_GUAR_CONT as g1 on g5.GUAR_CONT_NO=g1.GUAR_CONT_NO
where p1.CREUNIT_NO = '0801'
--30010000011439980
and p1.LOAN_ACCOUNT= #LOAN_ACCOUNT#

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#) or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

04
===
select
@pageTag(){

p1.LOAN_ACCOUNT,
g6.GUARANTY_ID,
g1.GUAR_NO,
g1.GUAR_NAME,
g1.CER_TYPE,
g1.CER_NO,
g6.GAGE_NAME,

case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.RIGHT_CERT_TYPE_CODE
when p1.ASSURE_MEANS_MAIN = '20' then

g4.RIGHT_CERT_TYPE_CODE

when p1.ASSURE_MEANS_MAIN = '30' then

''

end as RIGHT_CERT_TYPE_CODE,

case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.RIGHT_CERT_NO

when p1.ASSURE_MEANS_MAIN = '20' then

g4.RIGHT_CERT_NO

when p1.ASSURE_MEANS_MAIN = '30' then

''

end as RIGHT_CERT_NO,

case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.CURRENCY

when p1.ASSURE_MEANS_MAIN = '20' then

g4.CURRENCY

when p1.ASSURE_MEANS_MAIN = '30' then

''
end as CURRENCY,

case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.CORE_VALUE

when p1.ASSURE_MEANS_MAIN = '20' then

g4.CORE_VALUE

when p1.ASSURE_MEANS_MAIN = '30' then

''

end as CORE_VALUE,

case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.DEPOT_STATUS

when p1.ASSURE_MEANS_MAIN = '20' then

g4.DEPOT_STATUS

when p1.ASSURE_MEANS_MAIN = '30' then

''
end as DEPOT_STATUS
@}

from
RPT_M_RPT_SLS_ACCT as p1
left join  GRT_LOANGUAR_INFO as g5 on g5.CONT_NO=p1.CONT_NO
left join GRT_GUAR_CONT as g1 on g5.GUAR_CONT_NO=g1.GUAR_CONT_NO
left join GRT_GUARANTY_RE as g6 on g6.GUAR_CONT_NO=g5.GUAR_CONT_NO
left join GRT_G_BASIC_INFO as g3 on g3.GUARANTY_ID=g6.GUARANTY_ID
left join GRT_P_BASIC_INFO as g4 on g4.GUARANTY_ID=g6.GUARANTY_ID
left join GRT_GUARANTEER as g2 on g2.GUARANTY_ID=g6.GUARANTY_ID
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT= #LOAN_ACCOUNT#

-- 数据可视范围
@if(null != deplimit){
and (
    p1.CUST_MGR in (#join(userlimit)#) or p1.MAIN_BR_ID in (#join(deplimit)#)
)
@}

201
===
select
@pageTag(){
    CUS_ID,
    CUS_NAME,
    CUS_TYPE,
    CERT_TYPE,
    CERT_CODE,
    CUS_BANK_REL,
    COM_HOLD_STK_AMT,
    INVEST_TYPE,
    COM_SUB_TYP,
    COM_SCALE,
    COM_HOLD_TYPE,
    COM_INS_CODE,
    COM_CLL_TYPE,
    COM_CLL_NAME,
    COM_EMPLOYEE,
    LEGAL_NAME,
    LEGAL_CERT_TYPE,
    LEGAL_CERT_CODE,
    LEGAL_PHONE,
    NAT_TAX_REG_CODE,
    NAT_TAX_REG_ORG,
    LOC_TAX_REG_CODE,
    LOC_TAX_REG_ORG,
    FNA_MGR,
    COM_OPERATOR,
    POST_ADDR,
    PHONE,
    FAX_CODE,
    BAS_ACC_BANK,
    BAS_ACC_NO,
    COM_CRD_TYP,
    COM_CRD_GRADE,
    COM_OPT_ST,
    COM_REL_DGR,
    COM_CITY_FLG,
    CUST_MGR,
    MAIN_BR_ID,
    TOTAL_ASSETS,
    TOTAL_SALES
@}
from
CUS_COM
where CREUNIT_NO = '0801'
-- 客户号
and CUS_ID= #CUS_ID#

-- 数据可视范围
@if(null != deplimit){
    and (
        CUST_MGR in (#join(userlimit)#)
        or MAIN_BR_ID in (#join(deplimit)#)
    )
@}

202
===
select
@pageTag(){
    INNER_CUS_ID,
    CUS_ID,
    MNG_BR_ID,
    CUS_TYPE,
    CUS_NAME,
    INDIV_SEX,
    CERT_TYPE,
    CERT_CODE,
    AGRI_FLG,
    CUS_BANK_REL,
    COM_HOLD_STK_AMT,
    BANK_DUTY,
    INDIV_NTN,
    INDIV_BRT_PLACE,
    INDIV_HOUH_REG_ADD,
    INDIV_DT_OF_BIRTH,
    INDIV_POL_ST,
    INDIV_EDT,
    INDIV_MAR_ST,
    POST_ADDR,
    PHONE,
    FPHONE,
    FAX_CODE,
    EMAIL,
    INDIV_RSD_ADDR,
    INDIV_RSD_ST,
    INDIV_SOC_SCR,
    INDIV_COM_NAME,
    INDIV_COM_TYP,
    INDIV_COM_FLD,
    INDIV_COM_PHN,
    INDIV_COM_FAX,
    INDIV_COM_ADDR,
    INDIV_COM_CNT_NAME,
    INDIV_COM_JOB_TTL,
    INDIV_CRTFCTN,
    INDIV_SAL_ACC_BANK,
    INDIV_SAL_ACC_NO,
    INDIV_SPS_NAME,
    INDIV_SPS_ID_TYP,
    INDIV_SPS_ID_CODE,
    INDIV_SCOM_NAME,
    INDIV_SPS_OCC,
    INDIV_SPS_DUTY,
    INDIV_SPS_PHN,
    INDIV_SPS_MPHN,
    INDIV_SPS_JOB_DT,
    COM_REL_DGR,
    CRD_GRADE,
    CRD_DATE,
    REMARK,
    CUST_MGR,
    MAIN_BR_ID,
    CUS_STATUS,
    INDIV_COM_FLD_NAME
@}
from
CUS_INDIV
where CREUNIT_NO = '0801'
-- 客户号
and CUS_ID= #CUS_ID#

-- 数据可视范围
@if(null != deplimit){
    and CUST_MGR in (#join(userlimit)#)
    and MAIN_BR_ID in (#join(deplimit)#)
@}

203
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    a1.PRD_PK,
    p1.BIZ_TYPE,
    a1.PRD_NAME,
    p1.PRD_TYPE,
    p1.CUS_ID,
    p1.CUS_NAME,
    p1.BIZ_TYPE_SUB,
    a1.ACCOUNT_CLASS,
    p1.LOAN_ACCOUNT,
    p1.LOAN_FORM,
    p1.LOAN_NATURE,
    a1.LOAN_TYPE_EXT,
    p1.ASSURE_MEANS_MAIN,
    a1.CUR_TYPE,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.TERM_TYPE,
    p1.ORIG_EXPI_DATE,
    a1.RECE_INT_CUMU,
    p1.ACTUAL_INT_CUMU,
    p1.DELAY_INT_CUMU,
    p1.REPAYMENT_MODE,
    p1.LOAN_DIRECTION,
    p1.EXTENSION_TIMES,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    a1.OVER_TIMES_CURRENT,
    a1.OVER_TIMES_TOTAL,
    a1.MAX_TIMES_TOTAL,
    p1.CLA,
    a1.CLA_DATE,
    a1.CLA_PRE,
    a1.CLA_DATE_PRE,
    a1.LATEST_REPAY_DATE,
    p1.CUST_MGR,
    a1.INPUT_BR_ID,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    p1.SETTL_DATE,
    p1.ACCOUNT_STATUS,
    p1.GL_CLASS,
    a1.ISCIRCLE,
    a1.RETURN_DATE,
    a1.REMARK
@}
from
RPT_M_RPT_SLS_ACCT as p1
left join ACC_LOAN as a1 on p1.LOAN_ACCOUNT=a1.LOAN_ACCOUNT
where p1.CREUNIT_NO = '0801'
-- 贷款帐号
and p1.LOAN_ACCOUNT= #LOAN_ACCOUNT#

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    }
@}

214
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    u1.CERT_TYPE,
    u1.CERT_CODE,
    u2.CUST_MGR,
    u2.MAIN_BR_ID
@}
from
CUS_COM as u2
left join CUS_BASE as u1 on u1.CUS_ID=u2.CUS_ID
where u2.CREUNIT_NO = '0801'

-- 客户号
@if(isNotEmpty(CUS_ID)){
    and u2.CUS_ID like #'%' + CUS_ID + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and u2.CUS_NAME like #'%' + CUS_NAME + '%'#
@}
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and u2.CERT_CODE like #'%' + CERT_CODE + '%'#
@}
-- 数据可视范围
@if(null != deplimit){
    and (
        u2.CUST_MGR in (#join(userlimit)#)
        or u2.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

215
===
select
@pageTag(){
    u4.CUS_ID,
    u4.COM_MRG_NAME,
    u4.COM_MRG_CERT_TYP,
    u4.COM_MRG_CERT_CODE,
    u4.COM_MRG_DUTY,
    u4.COM_MRG_EDT,
    u4.COM_MRG_PHN,
    u4.COM_MRG_ADRR
@}
from
CUS_COM_MANAGER as u4
left join CUS_COM as u2 on u4.CUS_ID=u2.CUS_ID
where u4.CREUNIT_NO = '0801'
-- 客户号
and u4.CUS_ID= #CUS_ID#

-- 数据可视范围
@if(null != deplimit){
    and (
        u2.CUST_MGR in (#join(userlimit)#)
        or u2.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

216
===
select
@pageTag(){
    u5.SEQ,
    u5.COM_ADDR_TYP,
    u5.COM_ADDR,
    u5.COM_PHN_CODE,
    u5.COM_FAX_CODE
@}
from
CUS_COM_CONT as u5
left join CUS_COM as u2 on u5.CUS_ID=u2.CUS_ID
where u5.CREUNIT_NO = '0801'
-- 客户号
and u5.CUS_ID= #CUS_ID#

-- 数据可视范围
@if(null != deplimit){
    and (
        u2.CUST_MGR in (#join(userlimit)#)
        or u2.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

217
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    u1.CERT_TYPE,
    u1.CERT_CODE,
    u3.CUST_MGR,
    u3.MAIN_BR_ID
@}
from CUS_INDIV as u3
left join CUS_BASE as u1 on u3.CUS_ID=u1.CUS_ID
where u3.CREUNIT_NO = '0801'
-- 客户号
@if(isNotEmpty(CUS_ID)){
    and u3.CUS_ID like #'%' + CUS_ID + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and u3.CUS_NAME like #'%' + CUS_NAME + '%'#
@}
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and u3.CERT_CODE like #'%' + CUS_CODE + '%'#
@}

-- 数据可视范围
@if(null != deplimit){
    and (
        u3.CUST_MGR in (#join(userlimit)#)
        or u3.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

218
===
select
@pageTag(){
    u6.INDIV_DEPOSITS,
    u6.INDIV_SUR_YEAR,
    u6.INDIV_ANN_INCM,
    u6.REMARK
@}
from
CUS_INDIV_INCOME as u6
left join CUS_INDIV as u3 on u6.CUS_ID=u3.CUS_ID
where u6.CREUNIT_NO = '0801'
-- 客户号
and u6.CUS_ID= #CUS_ID#

-- 数据可视范围
@if(null != deplimit){
    and (
        u3.CUST_MGR in (#join(userlimit)#)
        or u3.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

219
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.CUS_ID,
    p1.CUS_NAME,
    p1.CUST_TYPE,
    p1.CERT_TYPE,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    p1.PSN_CERT_CODE
    else
    p1.ENT_CERT_CODE
    end as CERT_CODE,
    p1.ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.PRD_TYPE,
    p1.CLA,
    p1.LOAN_CLA4,
    p1.SEVEN_RESULT,
    p1.USE_DEC,
    p1.ACCOUNT_STATUS,
    p1.INDIV_RSD_ADDR,
    p1.REPAYMENT_MODE,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    p1.UNPD_PRIN_BAL,
    p1.DELAY_INT_CUMU,
    p1.CUST_MGR,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    u1.PHONE,
    u1.CONTACT_NAME,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    u2.COM_CRD_GRADE
    else
    u3.CRD_GRADE
    end as CRD_GRADE
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
where p1.CREUNIT_NO = '0801'
-- 贷款分类（一般贷款台帐固定为“普通贷款”）
and p1.LN_TYPE = '普通贷款'

-- 贷款帐号
@if(isNotEmpty(LOAN_ACCOUNT)){
    and p1.LOAN_ACCOUNT like #'%' + LOAN_ACCOUNT + '%'#
@}

-- 借据号
@if(isNotEmpty(BILL_NO)){
    and p1.BILL_NO like #'%' + BILL_NO + '%'#
@}

-- 合同号
@if(isNotEmpty(CONT_NO)){
    and p1.CONT_NO like #'%' + CONT_NO + '%'#
@}

-- 客户号
@if(isNotEmpty(CUS_ID)){
    and p1.CUS_ID like #'%' + CUS_ID + '%'#
@}

-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}

-- 台账状态
@if(isNotEmpty(ACCOUNT_STATUS)){
    and p1.ACCOUNT_STATUS= #ACCOUNT_STATUS#
@}

-- 五级分类标志
@if(isNotEmpty(CLA)){
    and p1.CLA = #CLA#
@}

-- 是否逾期
@if(timeout == '1' || timeout == 1){
        and (p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL)
@}else if(timeout == '0' || timeout == 0){
        and not (p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL)
@}

-- 联系人
@if(isNotEmpty(CONTACT_NAME)){
    and u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'#
@}

-- 联系电话
@if(isNotEmpty(PHONE)){
    and u1.PHONE like #'%' + PHONE + '%'#
@}

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

220
===
select
@pageTag(){
    CONT_NO,
    CUST_NO as CUS_ID,
    CUST_NAME as CUS_NAME,
    PRD_NAME,
    CONT_AMT,
    CONT_STATE,
    LOAN_START_DATE,
    LOAN_END_DATE,
    CUST_MGR,
    MAIN_BR_ID
@}
from
CTR_LOAN_CONT
where CREUNIT_NO = '0801'
-- 合同号
and CONT_NO=#CONT_NO#

-- 数据可视范围
@if(null != deplimit){
    and (
        CUST_MGR in (#join(userlimit)#)
        and MAIN_BR_ID in (#join(deplimit)#)
    )
@}

--所有客户列表
229
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    u1.CERT_TYPE,
    u1.CERT_CODE,
    case
    when u1.CUS_TYPE like '1%' then
    u3.CUST_MGR
    else
    u2.CUST_MGR
    end as CUST_MGR,
    case
    when u1.CUS_TYPE like '1%' then
    u3.MAIN_BR_ID
    else
    u2.MAIN_BR_ID
    end as MAIN_BR_ID
@}
from
CUS_BASE as u1
left join CUS_COM as u2 on u1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on u1.CUS_ID = u3.CUS_ID
where u1.CREUNIT_NO = '0801'

-- 客户号
@if(isNotEmpty(CUS_ID)){
    and u1.CUS_ID like #'%' + CUS_ID + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and u1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and u1.CERT_CODE like #'%' + CERT_CODE + '%'#
@}
-- 数据可视范围
@if(null != deplimit){
    and (
        CUST_MGR in (#join(userlimit)#)
        or MAIN_BR_ID in (#join(deplimit)#)
    )
@}

--还款记录
230
===
select
@pageTag(){
    ACCT_NO,
    REC_NO,
    TRN_DATE,
    TELL_NO,
    TRCH_NO,
    JRNL_NO,
    TOT_INT,
    PRIN_AMT,
    INT_AMT,
    PIA_AMT,
    IIA_AMT,
    AFTER_INT_AMT
@}
from
BOCT_88
where CREUNIT_NO = '0801'
-- 贷款帐号（16位）
and ACCT_NO= #LOAN_ACCOUNT#


