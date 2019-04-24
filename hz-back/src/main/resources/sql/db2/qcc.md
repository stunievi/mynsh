企查查日志查询
===
select
@pageTag(){
    log.*,
    d.v_value as type,
    u.USERNAME as username
@}
from T_QCC_LOG log 
left join t_dict d on d.name = 'qcc_log_type' and d.v_key = log.type
left join t_user u on u.id=log.OPERATOR
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



