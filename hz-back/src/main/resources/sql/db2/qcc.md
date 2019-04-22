企查查日志查询
===
select
@pageTag(){
    log.*,
    d.v_value as type
@}
from T_QCC_LOG log 
left join t_dict d on d.name = 'qcc_log_type' and d.v_key = log.type
where 1=1
@if(isNotEmpty(type)){
    and TYPE=#type#
@}
@if(isNotEmpty(endTime)){
    and ADD_TIME<#endTime#
@}
@if(isNotEmpty(startTime)){
    and ADD_TIME>=#startTime#
@}
@if(isNotEmpty(userName)){
    and OPERATOR like #'%'+ userName+ '%'#
@}

对公客户
===
select 
@pageTag(){
*
@}
 from
(select a.*,row_number() over(partition by CUS_NAME order by CUS_NAME) rn from 
(select p1.* from RPT_M_RPT_SLS_ACCT p1
where 
p1.ACCOUNT_STATUS in ('1','6') 
and p1.GL_CLASS not like '0%'
and CUST_TYPE like '2%') a) b where rn =1
@if(isNotEmpty(CUS_ID)){
    and b.CUS_ID like #'%' +CUS_ID +'%'#
@}
@if(isNotEmpty(CUS_NAME)){
    and b.CUS_NAME like #'%'+ CUS_NAME + '%'#
@}
@if(isNotEmpty(CERT_CODE)){
    and b.ENT_CERT_CODE like #'%'+ CERT_CODE + '%'#
@}
@if(isNotEmpty(START_LOAN_AMOUNT)){
    and b.LOAN_AMOUNT>=#START_LOAN_AMOUNT#
@}
@if(isNotEmpty(END_LOAN_AMOUNT)){
    and b.LOAN_AMOUNT<=#START_LOAN_AMOUNT#
@}
@if(isNotEmpty(START_LOAN_BALANCE)){
    and b.LOAN_BALANCE>=#START_LOAN_BALANCE#
@}
@if(isNotEmpty(END_LOAN_BALANCE)){
    and b.LOAN_BALANCE>=#END_LOAN_BALANCE#
@}
@if(isNotEmpty(START_UNPD_PRIN_BAL)){
    and b.UNPD_PRIN_BAL>=#START_UNPD_PRIN_BAL#
@}
@if(isNotEmpty(END_UNPD_PRIN_BAL)){
    and b.UNPD_PRIN_BAL>=#END_UNPD_PRIN_BAL#
@}


