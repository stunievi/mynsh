01
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
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
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.PRD_TYPE,
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
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
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
    case
    when p1.LOAN_ACCOUNT like '3002%' then
    func_get_dict('COM_CRD_GRADE',u2.COM_CRD_GRADE)
    else
    func_get_dict('CRD_GRADE',u3.CRD_GRADE)
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
    func_get_dict('CUST_TYPE',p1.CUST_TYPE) as CUST_TYPE,
    func_get_dict('CERT_TYPE',p1.CERT_TYPE) as CERT_TYPE,
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
    p1.PRD_TYPE,
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
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    p1.FINA_BR_ID,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
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
    case
    when p1.LOAN_ACCOUNT like '3002%' then
    func_get_dict('COM_CRD_GRADE',u2.COM_CRD_GRADE)
    else
    func_get_dict('CRD_GRADE',u3.CRD_GRADE)
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
    g1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(g1.MAIN_BR_ID) as MAIN_BR_NAME
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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}


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

-- 数据可视范围
@if(null != deplimit){
    and CUST_MGR in (#join(userlimit)#)
    or MAIN_BR_ID in (#join(deplimit)#)
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

-- 数据可视范围
@if(null != deplimit){
    and (
        p1.CUST_MGR in (#join(userlimit)#)
        or p1.MAIN_BR_ID in (#join(deplimit)#)
    )
@}

214
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    u1.CERT_CODE,
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
    func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    u1.CERT_CODE,
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
    inq.*
    @if(isNotEmpty(modelName)){
        --可以发布的模型名
        , FUNC_CAN_PUB_BY_NAME(#uid#, inq.PUB_MODEL_NAME) as CAN_PUB
    @}
@}
from
(select
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
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
    func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
    p1.CURRENCY,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.LOAN_TERM,
    p1.PRD_TYPE,
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
    FUN_GET_USER_BY_CODE(p1.CUST_MGR) as CUST_MGR_NAME,
    p1.FINA_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.FINA_BR_ID) as FINA_BR_NAME,
    p1.MAIN_BR_ID,
    FUN_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
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
    case
    when p1.LOAN_ACCOUNT like '3002%' then
        func_get_dict('COM_CRD_GRADE',u2.COM_CRD_GRADE)
    else
        func_get_dict('CRD_GRADE',u3.CRD_GRADE)
    end as CRD_GRADE
    -- user
    , usr.id as pub_user_id
    , usr.true_name as pub_utname
    , usr.username as pub_uname
    @if(isNotEmpty(modelName)){
        --可以发布的模型名
        , FUNC_GET_MODEL_BY_LOAN_ACCOUNT(p1.loan_account, #modelName#) as PUB_MODEL_NAME
    @}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
left join t_user usr on usr.acc_code = p1.cust_mgr

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
        and ((p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL))
@}else if(timeout == '0' || timeout == 0){
        and not ((p1.CAP_OVERDUE_DATE<>'' and p1.CAP_OVERDUE_DATE<>NULL) or (p1.INTEREST_OVERDUE_DATE<>'' and p1.INTEREST_OVERDUE_DATE<>NULL))
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


-- 不良台账
@if(register == 'true'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产管理流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) > 0
@}else if(register == 'false'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产管理流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) = 0
@}
) as inq

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

-- 数据可视范围
@if(null != deplimit){
    and (
        CUST_MGR in (#join(userlimit)#)
        or MAIN_BR_ID in (#join(deplimit)#)
    )
@}

--所有客户列表
229
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
    func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    u1.CERT_CODE,
    func_get_dict('CUS_TYPE',u1.CUS_TYPE) as CUS_TYPE,
    u1.CUS_TYPE as SOURCE_CUS_TYPE,
    case
    when u1.CUS_TYPE like '1%' then
    u3.CUST_MGR
    else
    u2.CUST_MGR
    end as CUST_MGR,
    case
    when u1.CUS_TYPE like '1%' then
    FUN_GET_USER_BY_CODE(u3.CUST_MGR)
    else
    FUN_GET_USER_BY_CODE(u2.CUST_MGR)
    end as CUST_MGR_NAME,
    case
    when u1.CUS_TYPE like '1%' then
    u3.MAIN_BR_ID
    else
    u2.MAIN_BR_ID
    end as MAIN_BR_ID,
    case
    when u1.CUS_TYPE like '1%' then
    FUN_GET_ORG_BY_CODE(u3.MAIN_BR_ID)
    else
    FUN_GET_ORG_BY_CODE(u2.MAIN_BR_ID)
    end as MAIN_BR_NAME,
    p1.LOAN_AMOUNT,
    p2.UNPD_PRIN_BAL
@}
from
CUS_BASE as u1
left join CUS_COM as u2 on u1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on u1.CUS_ID = u3.CUS_ID
left join (select MAX(LOAN_AMOUNT) as LOAN_AMOUNT,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p1 on u1.CUS_ID=p1.CUS_ID
left join (select MAX(UNPD_PRIN_BAL)as UNPD_PRIN_BAL,CUS_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO ='0801' group by CUS_ID) as p2 on u1.CUS_ID=p2.CUS_ID
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
-- 数据可视范围
@if(null != deplimit){
    and (
        (
            u2.CUST_MGR in (#join(userlimit)#)
            or u2.MAIN_BR_ID in (#join(deplimit)#)
        ) or 
        (
            u3.CUST_MGR in (#join(userlimit)#)
            or u3.MAIN_BR_ID in (#join(deplimit)#) 
        )
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

231
===
select
@pageTag(){
    p1.BILL_NO,
    p1.CONT_NO,
    p1.LOAN_ACCOUNT,
    t1.NAME,
    t1.TYPE,
    p1.CODE,
    p1.PHONE,
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
    p1.TERM_TYPE,
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
    p1.MAIN_BR_ID
@}
from RPT_M_RPT_SLS_ACCT as p1
left join T_LOAN_MANAGER t1 on p1.LOAN_ACCOUNT=t1.LOAN_ACCOUNT
where p1.CREUNIT_NO = '0801'
-- 贷款分类（一般贷款台帐固定为“普通贷款”）
and p1.LN_TYPE = '普通贷款'

and ((p.CUS_ID in 
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

