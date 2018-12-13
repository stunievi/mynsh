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
    g1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(g1.MAIN_BR_ID) as MAIN_BR_NAME
@}
from RPT_M_RPT_SLS_ACCT as p1
left join GRT_LOANGUAR_INFO as g5 on p1.CONT_NO=g5.CONT_NO
left join GRT_GUAR_CONT as g1 on g5.GUAR_CONT_NO=g1.GUAR_CONT_NO
where p1.CREUNIT_NO = '0801'
--30010000011439980
and p1.LOAN_ACCOUNT= #LOAN_ACCOUNT#


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


101
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
    p1.CUS_NAME,
    func_get_dict('CUST_TYPE',p1.CUST_TYPE) as CUST_TYPE,
    func_get_dict('CERT_TYPE',p1.CERT_TYPE) as CERT_TYPE,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    p1.PSN_CERT_CODE
    else
    p1.ENT_CERT_CODE
    end as CERT_CODE,
    u1.PHONE,
    p1.CUST_MGR,
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID 
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


102
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
    p1.CUS_NAME,
    func_get_dict('CUST_TYPE',p1.CUST_TYPE) as CUST_TYPE,
    func_get_dict('CERT_TYPE',p1.CERT_TYPE) as CERT_TYPE,
    p1.CUST_MGR,
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID 
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


103
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
    p1.CUS_NAME,
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
    p1.USE_DEC,
    p1.LOAN_TERM  
@}
from RPT_M_RPT_SLS_ACCT as p1
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


104
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
    p1.CUS_NAME,
    g1.GUAR_NAME,
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
    p1.USE_DEC,
    p1.LOAN_TERM  
@}
from RPT_M_RPT_SLS_ACCT as p1 
left join GRT_LOANGUAR_INFO as g5 on p1.CONT_NO=g5.CONT_NO
left join GRT_GUAR_CONT as g1 on g5.GUAR_CONT_NO=g1.GUAR_CONT_CN_NO
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


105
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
	p1.CUS_NAME,
	func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
	p1.INDIV_RSD_ADDR,
	p1.LOAN_START_DATE,
	p1.LOAN_END_DATE,
	case
    when (length(u1.PHONE) = 11 and u1.PHONE like '1%') then
        u1.phone
    when (p1.LOAN_ACCOUNT like '3001%' and length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
        u3.MOBILE
    when (p1.LOAN_ACCOUNT like '3001%' and length(u3.PHONE) = 11 and u3.PHONE like '1%') then
        u3.PHONE
    when (p1.LOAN_ACCOUNT like '3001%' and length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
        u3.FPHONE
    when (p1.LOAN_ACCOUNT like '3002%' and length(u2.PHONE) = 11 and u2.PHONE like '1%') then
        u2.PHONE
    when (p1.LOAN_ACCOUNT like '3002%' and length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
        u2.LEGAL_PHONE
    else
        ''
	end as PHONE,
	p1.USE_DEC,
	func_get_dict('REPAYMENT_MODE',p1.REPAYMENT_MODE) as REPAYMENT_MODE,
	p1.UNPD_PRIN_BAL,
	p1.DELAY_INT_CUMU,
	p1.SEVEN_RESULT,
	p1.LOAN_TERM
@}
from RPT_M_RPT_SLS_ACCT as p1 
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join cus_com as u2 on p1.CUS_ID = u2.CUS_ID
left join cus_indiv as u3 on p1.CUS_ID = u3.CUS_ID
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


106
===
select 
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.CUS_ID,
    p1.CUS_NAME
@}
from RPT_M_RPT_SLS_ACCT as p1 
where p1.CREUNIT_NO = '0801'
and p1.LOAN_ACCOUNT=#LOAN_ACCOUNT#


201
===
select
@pageTag(){
    CUS_ID,
    CUS_NAME,
    func_get_dict('CUS_TYPE',CUS_TYPE) as CUS_TYPE,
    func_get_dict('CERT_TYPE',CERT_TYPE) as CERT_TYPE,
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
    func_get_dict('COM_CRD_GRADE',COM_CRD_GRADE) as COM_CRD_GRADE,
    COM_OPT_ST,
    COM_REL_DGR,
    COM_CITY_FLG,
    CUST_MGR,
    FUN_GET_USER_BY_CODE(CUST_MGR) as CUST_MGR_NAME,
    MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(MAIN_BR_ID) as MAIN_BR_NAME,
    TOTAL_ASSETS,
    TOTAL_SALES
@}
from
CUS_COM
where CREUNIT_NO = '0801'
-- 客户号
and CUS_ID= #CUS_ID#


202
===
select
@pageTag(){
    INNER_CUS_ID,
    CUS_ID,
    MNG_BR_ID,
    func_get_dict('CUS_TYPE',CUS_TYPE) as CUS_TYPE,
    CUS_NAME,
    INDIV_SEX,
    func_get_dict('CERT_TYPE',CERT_TYPE) as CERT_TYPE,
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
    func_get_dict('CRD_GRADE',CRD_GRADE) as CRD_GRADE,
    CRD_DATE,
    REMARK,
    CUST_MGR,
    FUN_GET_USER_BY_CODE(CUST_MGR) as CUST_MGR_NAME,
    MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(MAIN_BR_ID) as MAIN_BR_NAME,
    CUS_STATUS,
    INDIV_COM_FLD_NAME
@}
from
CUS_INDIV
where CREUNIT_NO = '0801'
-- 客户号
and CUS_ID= #CUS_ID#


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
    case when p1.LOAN_ACCOUNT like '3001%' then
    '对私'
    else
    '对公'
    end as $ct,
    p1.LOAN_FORM,
    p1.LOAN_NATURE,
    a1.LOAN_TYPE_EXT,
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
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
    func_get_dict('REPAYMENT_MODE',p1.REPAYMENT_MODE) as REPAYMENT_MODE,
    p1.LOAN_DIRECTION,
    p1.EXTENSION_TIMES,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    a1.OVER_TIMES_CURRENT,
    a1.OVER_TIMES_TOTAL,
    a1.MAX_TIMES_TOTAL,
    func_get_dict('CLA',p1.CLA) as CLA,
    a1.CLA_DATE,
    func_get_dict('CLA_PRE',a1.CLA_PRE) as CLA_PRE,
    a1.CLA_DATE_PRE,
    a1.LATEST_REPAY_DATE,
    p1.CUST_MGR,
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    a1.INPUT_BR_ID,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
    p1.SETTL_DATE,
    func_get_dict('ACCOUNT_STATUS',p1.ACCOUNT_STATUS) as ACCOUNT_STATUS,
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


214
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u2.PHONE) = 11 and u2.PHONE like '1%') then
            u2.PHONE
        when (length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
            u2.LEGAL_PHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u2.LEGAL_NAME)) != 0) then
            u2.LEGAL_NAME
        when (length(trim(u2.COM_OPERATOR)) != 0) then
            u2.COM_OPERATOR
        else
            ''
    end as CONTACT_NAME,
    u2.POST_ADDR,
    u2.CUST_MGR,
    FUN_GET_USER_BY_CODE(u2.CUST_MGR) as CUST_MGR_NAME,
    u2.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(u2.MAIN_BR_ID) as MAIN_BR_NAME,
    p1.LOAN_AMOUNT,
    p2.UNPD_PRIN_BAL
@}
from
CUS_COM as u2
left join CUS_BASE as u1 on u2.CUS_ID=u1.CUS_ID
left join (select MAX(LOAN_AMOUNT) as LOAN_AMOUNT,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p1 on u2.CUS_ID=p1.CUS_ID
left join (select MAX(UNPD_PRIN_BAL)as UNPD_PRIN_BAL,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p2 on u2.CUS_ID=p2.CUS_ID
where u2.CREUNIT_NO = '0801'
@if(isNotEmpty(own)){
    and exists(
        select 1 from t_cus_belong where uid = #uid# and cus_id = u2.cus_id
    )
@}
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
--电话
@if(isNotEmpty(PHONE)){
    and (u1.PHONE like #'%' + PHONE + '%'# 
        or u2.LEGAL_PHONE like #'%' + PHONE + '%'# 
        or u2.PHONE like #'%' + PHONE + '%'# )
@}
--联系人
@if(isNotEmpty(CONTACT_NAME)){
    and (u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'# 
        or u2.LEGAL_NAME like #'%' + CONTACT_NAME + '%'# 
        or u2.COM_OPERATOR like #'%' + CONTACT_NAME + '%'# )
@}
--贷款金额大于
@if(isNotEmpty(LOAN_AMOUNT)){
    and p1.LOAN_AMOUNT > #LOAN_AMOUNT#
@}
--拖欠本金大于
@if(isNotEmpty(UNPD_PRIN_BAL)){
    and p2.UNPD_PRIN_BAL > #UNPD_PRIN_BAL#
@}


215
===
select
@pageTag(){
    u4.CUS_ID,
    u4.COM_MRG_NAME,
    func_get_dict('CERT_TYPE',u4.COM_MRG_CERT_TYP) as COM_MRG_CERT_TYP,
    u4.COM_MRG_CERT_CODE,
    func_get_dict('COM_MRG_DUTY',u4.COM_MRG_DUTY) as COM_MRG_DUTY,
    func_get_dict('COM_MRG_EDT',u4.COM_MRG_EDT) as COM_MRG_EDT,
    u4.COM_MRG_PHN,
    u4.COM_MRG_ADRR
@}
from
CUS_COM_MANAGER as u4
left join CUS_COM as u2 on u4.CUS_ID=u2.CUS_ID
where u4.CREUNIT_NO = '0801'
-- 客户号
and u4.CUS_ID= #CUS_ID#


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


217
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
            u3.MOBILE
        when (length(u3.PHONE) = 11 and u3.PHONE like '1%') then
            u3.PHONE
        when (length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
            u3.FPHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u3.CUS_NAME)) != 0) then
            u3.CUS_NAME
        when (length(trim(u3.INDIV_COM_CNT_NAME)) != 0) then
            u3.INDIV_COM_CNT_NAME
        else
            ''
    end as CONTACT_NAME,
    u3.POST_ADDR,
    u3.CUST_MGR,
    FUN_GET_USER_BY_CODE(u3.CUST_MGR) as CUST_MGR_NAME,
    u3.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(u3.MAIN_BR_ID) as MAIN_BR_NAME,
    p1.LOAN_AMOUNT,
    p2.UNPD_PRIN_BAL
@}
from CUS_INDIV as u3
left join CUS_BASE as u1 on u3.CUS_ID=u1.CUS_ID
left join (select MAX(LOAN_AMOUNT) as LOAN_AMOUNT,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p1 on u3.CUS_ID=p1.CUS_ID
left join (select MAX(UNPD_PRIN_BAL)as UNPD_PRIN_BAL,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p2 on u3.CUS_ID=p2.CUS_ID
where u3.CREUNIT_NO = '0801'
@if(isNotEmpty(own)){
    and exists(
        select 1 from t_cus_belong where uid = #uid# and cus_id = u3.cus_id
    )
@}
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
--电话
@if(isNotEmpty(PHONE)){
    and (u1.PHONE like #'%' + PHONE + '%'# 
        or u3.MOBILE like #'%' + PHONE + '%'# 
        or u3.FPHONE like #'%' + PHONE + '%'# 
        or u3.PHONE like #'%' + PHONE + '%'# )
@}
--联系人
@if(isNotEmpty(CONTACT_NAME)){
    and (u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'# 
        or u3.CUS_NAME like #'%' + CONTACT_NAME + '%'# 
        or u3.INDIV_COM_CNT_NAME like #'%' + CONTACT_NAME + '%'# )
@}
--贷款金额大于
@if(isNotEmpty(LOAN_AMOUNT)){
    and p1.LOAN_AMOUNT > #LOAN_AMOUNT#
@}
--拖欠本金大于
@if(isNotEmpty(UNPD_PRIN_BAL)){
    and p2.UNPD_PRIN_BAL > #UNPD_PRIN_BAL#
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


219
===
select
@pageTag(){
    p1.BILL_NO,
        p1.CONT_NO,
        p1.LOAN_ACCOUNT,
        p1.CUS_ID,
        p1.CUS_NAME,
        value(p1.PSN_CERT_CODE, p1.ENT_CERT_CODE) as CERT_CODE,
        p1.CURRENCY,
        p1.LOAN_AMOUNT,
        p1.LOAN_BALANCE,
        p1.LOAN_START_DATE,
        p1.LOAN_END_DATE,
        p1.LOAN_TERM,
        p1.TERM_TYPE,
        p1.PRD_TYPE,
        p1.REALITY_IR_Y,
        p1.LOAN_CLA4,
        p1.SEVEN_RESULT,
        p1.USE_DEC,
        p1.INDIV_RSD_ADDR,
        p1.CAP_OVERDUE_DATE,
        p1.INTEREST_OVERDUE_DATE,
        p1.UNPD_PRIN_BAL,
        p1.DELAY_INT_CUMU,
        p1.CUST_MGR,
        p1.FINA_BR_ID,
        p1.MAIN_BR_ID,
        case
            when (length(u1.PHONE) = 11 and u1.PHONE like '1%') then
                u1.phone
            when (p1.LOAN_ACCOUNT like '3001%' and length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
                u3.MOBILE
            when (p1.LOAN_ACCOUNT like '3001%' and length(u3.PHONE) = 11 and u3.PHONE like '1%') then
                u3.PHONE
            when (p1.LOAN_ACCOUNT like '3001%' and length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
                u3.FPHONE
            when (p1.LOAN_ACCOUNT like '3002%' and length(u2.PHONE) = 11 and u2.PHONE like '1%') then
                u2.PHONE
            when (p1.LOAN_ACCOUNT like '3002%' and length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
                u2.LEGAL_PHONE
            else
                ''
        end as PHONE,
        case
            when (length(trim(u1.CONTACT_NAME)) != 0) then
                u1.CONTACT_NAME
            when (p1.LOAN_ACCOUNT like '3002%' and length(trim(u2.LEGAL_NAME)) != 0) then
                u2.LEGAL_NAME
            when (p1.LOAN_ACCOUNT like '3002%' and length(trim(u2.COM_OPERATOR)) != 0) then
                u2.COM_OPERATOR
            when (p1.LOAN_ACCOUNT like '3001%' and length(trim(u3.CUS_NAME)) != 0) then
                u3.CUS_NAME
            when (p1.LOAN_ACCOUNT like '3001%' and length(trim(u3.INDIV_COM_CNT_NAME)) != 0) then
                u3.INDIV_COM_CNT_NAME
            else
                ''
        end as CONTACT_NAME,
    value(d21.v_value,d22.v_value) as CRD_GRADE,
    d1.v_value as CUST_TYPE,
    d2.v_value as CERT_TYPE,d3.v_value as ASSURE_MEANS_MAIN,d4.v_value as CLA,d5.v_value as ACCOUNT_STATUS,d6.v_value as REPAYMENT_MODE,
    o1.name as FINA_BR_name, o2.name as main_br_name
    -- user
    @if(isNotEmpty(modelName)){
        , usr.id as pub_user_id
        , usr.true_name as pub_utname
        , usr.username as pub_uname
        --可以发布的模型名
        , FUNC_GET_MODEL_BY_LOAN_ACCOUNT(p1.loan_account, #modelName#) as PUB_MODEL_NAME
    @}
    --实际控制人
    @if(isNotEmpty(lm)){
        , lm.name as lm_name
        , lm.code as lm_code
        , lm.phone as lm_phone
        , lm.type as lm_type
    @}
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
left join t_dict d1 on d1.name = 'CUST_TYPE' and d1.V_KEY = p1.CUST_TYPE
left join t_dict d2 on d2.name = 'CERT_TYPE' and d2.V_KEY = p1.CERT_TYPE
left join t_dict d3 on d3.name = 'ASSURE_MEANS_MAIN' and d3.V_KEY = p1.ASSURE_MEANS_MAIN
left join t_dict d4 on d4.name = 'CLA' and d4.V_KEY = p1.CLA
left join t_dict d5 on d5.name = 'ACCOUNT_STATUS' and d5.V_KEY = p1.ACCOUNT_STATUS
left join t_dict d6 on d6.name = 'REPAYMENT_MODE' and d6.V_KEY = p1.REPAYMENT_MODE

left join t_dict d21 on d21.name = 'COM_CRD_GRADE' and d21.V_KEY = u2.COM_CRD_GRADE
left join t_dict d22 on d22.name = 'CRD_GRADE' and d22.V_KEY = u3.CRD_GRADE

left join t_user u on u.acc_code = p1.cust_mgr
left join t_org o1 on o1.acc_code = p1.FINA_BR_ID
left join t_org o2 on o2.acc_code = p1.MAIN_BR_ID
@if(isNotEmpty(modelName)){
    left join t_user usr on usr.acc_code = p1.cust_mgr
@}
@if(isNotEmpty(lm)){
    left join t_loan_manager lm on lm.loan_account = p1.loan_account
@}
where p1.CREUNIT_NO = '0801'
@if(isNotEmpty(own)){
    and (
    p1.loan_account in (select loan_account from t_loan_belong where uid = #uid#)
    or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')
    )
@}

-- 贷款分类（一般贷款台帐固定为“普通贷款”）
@if(isNotEmpty(LN_TYPE)){
    and p1.LN_TYPE = #LN_TYPE#
@}
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
and (UNPD_PRIN_BAL>0 or DELAY_INT_CUMU>0)
@}else if(timeout == '0' || timeout == 0){
and ((UNPD_PRIN_BAL=0) or (UNPD_PRIN_BAL is null)) and ((DELAY_INT_CUMU=0) or (DELAY_INT_CUMU is null))
@}

-- 联系人
@if(isNotEmpty(CONTACT_NAME)){
    and u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'#
@}

-- 联系电话
@if(isNotEmpty(PHONE)){
    and u1.PHONE like #'%' + PHONE + '%'#
@}

-- 贷款起始日（开始）
@if(isNotEmpty(LOAN_START_DATE_S)){
    and p1.LOAN_START_DATE >= #LOAN_START_DATE_S#
@}

-- 贷款起始日（结束）
@if(isNotEmpty(LOAN_START_DATE_E)){
    and p1.LOAN_START_DATE <= #LOAN_START_DATE_E#
@}

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE)){
    and p1.LOAN_ACCOUNT like #'%' + LOAN_TYPE + '%'#
@}

-- 贷款用途
@if(isNotEmpty(USE_DEC)){
    and p1.USE_DEC like #'%' + USE_DEC + '%'#
@}


-- 不良台账
@if(register == 'true'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) > 0
@}else if(register == 'false'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) = 0
@}

@pageIgnoreTag(){
   order by p1.ACCOUNT_STATUS 
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
    FUN_GET_USER_BY_CODE(CUST_MGR) as CUST_MGR_NAME,
    MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(MAIN_BR_ID) as MAIN_BR_NAME
@}
from
CTR_LOAN_CONT
where CREUNIT_NO = '0801'
-- 合同号
and CONT_NO=#CONT_NO#


229
===
select
@pageTag(){
    u1.CUS_ID,
        u1.CUS_NAME,
        u1.CERT_CODE,
        case
            when (length(u1.PHONE) = 11 and u1.PHONE like '1%') then
                u1.phone
            when (u1.CUS_TYPE like '1%' and length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
                u3.MOBILE
            when (u1.CUS_TYPE like '1%' and length(u3.PHONE) = 11 and u3.PHONE like '1%') then
                u3.PHONE
            when (u1.CUS_TYPE like '1%' and length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
                u3.FPHONE
            when (u1.CUS_TYPE like '2%' and length(u2.PHONE) = 11 and u2.PHONE like '1%') then
                u2.PHONE
            when (u1.CUS_TYPE like '2%' and length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
                u2.LEGAL_PHONE
            else
                ''
        end as PHONE,
        case
            when (length(trim(u1.CONTACT_NAME)) != 0) then
                u1.CONTACT_NAME
            when (u1.CUS_TYPE like '2%' and length(trim(u2.LEGAL_NAME)) != 0) then
                u2.LEGAL_NAME
            when (u1.CUS_TYPE like '2%' and length(trim(u2.COM_OPERATOR)) != 0) then
                u2.COM_OPERATOR
            when (u1.CUS_TYPE like '1%' and length(trim(u3.CUS_NAME)) != 0) then
                u3.CUS_NAME
            when (u1.CUS_TYPE like '1%' and length(trim(u3.INDIV_COM_CNT_NAME)) != 0) then
                u3.INDIV_COM_CNT_NAME
            else
                ''
        end as CONTACT_NAME,
        value(u3.POST_ADDR, u2.POST_ADDR) as POST_ADDR,
        value(u3.CUST_MGR, u2.CUST_MGR) as CUST_MGR,
        value(u3.MAIN_BR_ID, u2.MAIN_BR_ID) as MAIN_BR_ID,
        p1.LOAN_AMOUNT,
        p2.UNPD_PRIN_BAL,
        d1.V_VALUE as CUS_TYPE,
        d2.v_value as CERT_TYPE,
        value(u11.TRUE_NAME, u12.TRUE_NAME) as CUST_MGR_NAME,
        value(o21.NAME, o22.NAME) as MAIN_BR_NAME,
        u1.CUS_TYPE as SOURCE_CUS_TYPE
@}
from
CUS_BASE as u1
left join CUS_COM as u2 on u1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on u1.CUS_ID = u3.CUS_ID
left join (select MAX(LOAN_AMOUNT) as LOAN_AMOUNT,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p1 on u1.CUS_ID=p1.CUS_ID
left join (select MAX(UNPD_PRIN_BAL)as UNPD_PRIN_BAL,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p2 on u1.CUS_ID=p2.CUS_ID
left join t_dict d1 on d1.name = 'CUS_TYPE' and d1.V_KEY = u1.CUS_TYPE
left join t_dict d2 on d2.name = 'CERT_TYPE' and d2.V_KEY = u1.CERT_TYPE

left join t_user u11 on u11.acc_code = u3.CUST_MGR
left join t_user u12 on u12.acc_code = u2.CUST_MGR
left join t_org o21 on o21.acc_code = u3.MAIN_BR_ID
left join t_org o22 on o22.acc_code = u2.MAIN_BR_ID

where u1.CREUNIT_NO = '0801'
@if(isNotEmpty(own)){
    and exists(
        select 1 from t_cus_belong where uid = #uid# and cus_id = u1.cus_id
    )
@}
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
--电话
@if(isNotEmpty(PHONE)){
    and (u1.PHONE like #'%' + PHONE + '%'# 
        or u2.LEGAL_PHONE like #'%' + PHONE + '%'# 
        or u2.PHONE like #'%' + PHONE + '%'# 
        or u3.MOBILE like #'%' + PHONE + '%'# 
        or u3.FPHONE like #'%' + PHONE + '%'# 
        or u3.PHONE like #'%' + PHONE + '%'# )
@}
--联系人
@if(isNotEmpty(CONTACT_NAME)){
    and (u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'# 
        or u2.LEGAL_NAME like #'%' + CONTACT_NAME + '%'# 
        or u2.COM_OPERATOR like #'%' + CONTACT_NAME + '%'# 
        or u3.CUS_NAME like #'%' + CONTACT_NAME + '%'# 
        or u3.INDIV_COM_CNT_NAME like #'%' + CONTACT_NAME + '%'# )
@}
--贷款金额大于
@if(isNotEmpty(LOAN_AMOUNT)){
    and p1.LOAN_AMOUNT > #LOAN_AMOUNT#
@}
--拖欠本金大于
@if(isNotEmpty(UNPD_PRIN_BAL)){
    and p2.UNPD_PRIN_BAL > #UNPD_PRIN_BAL#
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

231
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    p1.cus_id,
    p1.cus_name,
    t1.NAME,
    t1.TYPE,
    t1.CODE,
    t1.PHONE,
    case
    when p1.LOAN_ACCOUNT like '3001%' then
    p1.PSN_CERT_CODE
    else
    p1.ENT_CERT_CODE
    end as CERT_CODE,
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.TERM_TYPE,
    p1.PRD_TYPE,
    p1.REALITY_IR_Y,
    func_get_dict('CLA',p1.CLA) as CLA,
    p1.LOAN_CLA4,
    p1.SEVEN_RESULT,
    p1.USE_DEC,
    func_get_dict('ACCOUNT_STATUS',p1.ACCOUNT_STATUS) as ACCOUNT_STATUS,
    p1.INDIV_RSD_ADDR,
    func_get_dict('REPAYMENT_MODE',p1.REPAYMENT_MODE) as REPAYMENT_MODE,
    p1.CAP_OVERDUE_DATE,
    p1.INTEREST_OVERDUE_DATE,
    p1.UNPD_PRIN_BAL,
    p1.DELAY_INT_CUMU,
    p1.CUST_MGR,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME
@}
from RPT_M_RPT_SLS_ACCT as p1
left join T_LOAN_MANAGER t1 on p1.LOAN_ACCOUNT=t1.LOAN_ACCOUNT
where p1.CREUNIT_NO = '0801'
-- 贷款分类（一般贷款台帐固定为“普通贷款”）
and p1.LN_TYPE = '普通贷款'

and ((p1.CUS_ID in 
(select
case
when g1.CUS_ID is not NULL then
g1.CUS_ID
when g2.CUS_ID is not NULL then
g2.CUS_ID
when g3.CUS_ID is not NULL then
g3.CUS_ID
else
null
end as CUS_ID
from
(select GUARANTY_ID from GRT_GUARANTY_RE
where 
GUAR_CONT_NO in 
(select GUAR_CONT_NO from GRT_LOANGUAR_INFO
where 
CONT_NO in 
(SELECT CONT_NO FROM RPT_M_RPT_SLS_ACCT p2
 left join T_LOAN_MANAGER t2 on p2.LOAN_ACCOUNT=t2.LOAN_ACCOUNT
 where
 1 = 1
-- (实际控制人）姓名
@if(isNotEmpty(NAME)){
    and t2.NAME like #'%' + NAME + '%'#
@}

-- (实际控制人）证件号码
@if(isNotEmpty(CODE)){
    and t2.CODE like #'%' + CODE + '%'#
@}

-- (实际控制人）联系电话
@if(isNotEmpty(PHONE)){
    and t2.PHONE like #'%' + PHONE + '%'#
@}
))) a
left join GRT_G_BASIC_INFO g1 on a.GUARANTY_ID= g1.GUARANTY_ID
left join GRT_P_BASIC_INFO g2 on a.GUARANTY_ID= g2.GUARANTY_ID
left join GRT_GUARANTEER g3 on a.GUARANTY_ID= g3.GUARANTY_ID))
or
(
1 = 1
-- (实际控制人）姓名
@if(isNotEmpty(NAME)){
    and t1.NAME like #'%' + NAME + '%'#
@}

-- (实际控制人）证件号码
@if(isNotEmpty(CODE)){
    and t1.CODE like #'%' + CODE + '%'#
@}

-- (实际控制人）联系电话
@if(isNotEmpty(PHONE)){
    and t1.PHONE like #'%' + PHONE + '%'#
@}
)
)

232
===
select
@pageTag(){
    g5.CONT_NO,
    g5.GUAR_CONT_NO,
    func_get_dict('GUAR_WAY',g5.GUAR_WAY) as GUAR_WAY,
    --g5.GUAR_WAY,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        1
    when g5.GUAR_WAY in ('20000','20001') then
        2
    when g5.GUAR_WAY in ('30001','30002','30002') then
        3
    else
        0
    end as GUAR_WAY_1,
    g5.GUAR_TEE,
    g1.GUAR_NAME,
    g5.GUAR_WAY,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        func_get_dict('CERT_TYPE',g3.CER_TYPE)
    when g5.GUAR_WAY in ('20000','20001') then
        func_get_dict('CERT_TYPE',g4.CER_TYPE)
    when g5.GUAR_WAY in ('30001','30002','30002') then
        func_get_dict('CERT_TYPE',g2.CER_TYPE)
    else
        ''
    end as CER_TYPE,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        g3.CER_NO
    when g5.GUAR_WAY in ('20000','20001') then
        g4.CER_NO
    when g5.GUAR_WAY in ('30001','30002','30002') then
        g2.CER_NO
    else
        ''
    end as CER_NO,
    g6.GAGE_NAME,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        func_get_dict('RIGHT_CERT_TYPE_CODE',g3.RIGHT_CERT_TYPE_CODE)
    when g5.GUAR_WAY in ('20000','20001') then
        func_get_dict('RIGHT_CERT_TYPE_CODE',g4.RIGHT_CERT_TYPE_CODE)
    else
        ''
    end as RIGHT_CERT_TYPE_CODE,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        g3.RIGHT_CERT_NO
    when g5.GUAR_WAY in ('20000','20001') then
        g4.RIGHT_CERT_NO
    else
        ''
    end as RIGHT_CERT_NO,
    g6.CURRENCY,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        g3.CORE_VALUE
    when g5.GUAR_WAY in ('20000','20001') then
        g4.CORE_VALUE
    else
        0
    end as CORE_VALUE,
    case
    when g5.GUAR_WAY in ('10000','10001') then
        func_get_dict('DEPOT_STATUS',g3.DEPOT_STATUS)
    when g5.GUAR_WAY in ('20000','20001') then
        func_get_dict('DEPOT_STATUS',g4.DEPOT_STATUS)
    else
        ''
    end as DEPOT_STATUS
@}
from
GRT_LOANGUAR_INFO g5
left join GRT_GUAR_CONT g1 on g5.GUAR_CONT_NO = g1.GUAR_CONT_NO
left join GRT_GUARANTY_RE g6 on g5.GUAR_CONT_NO = g6.GUAR_CONT_NO
left join GRT_G_BASIC_INFO g3 on g6.GUARANTY_ID = g3.GUARANTY_ID
left join GRT_P_BASIC_INFO g4 on g6.GUARANTY_ID = g4.GUARANTY_ID
left join GRT_GUARANTEER g2 on g6.GUARANTY_ID = g2.GUARANTY_ID
where g5.CREUNIT_NO = '0801'
-- 合同号
and g5.CONT_NO = #CONT_NO#
@pageIgnoreTag(){
    order by g5.CONT_NO
@}

233
===
select
count(distinct p1.LOAN_ACCOUNT) as ALL,
count(distinct p2.LOAN_ACCOUNT) as NORMAL,
count(distinct p3.LOAN_ACCOUNT) as DANGER,
count(distinct p4.LOAN_ACCOUNT) as BAD
from 
RPT_M_RPT_SLS_ACCT p1
left join (select LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT where CLA in ('10','20') and DELAY_INT_CUMU = 0 and UNPD_PRIN_BAL = 0) p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT where CLA in ('10','20') and (DELAY_INT_CUMU > 0 or UNPD_PRIN_BAL > 0)) p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT where CLA in ('30','40','50') ) p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
left join t_user u on u.acc_code = p1.CUST_MGR
left join t_department_manager dm on dm.acc_code = p1.MAIN_BR_ID
where
p1.CREUNIT_NO = '0801'
--贷款分类
and p1.LN_TYPE = '普通贷款'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
and (dm.uid = #uid# or u.id = #uid#)
