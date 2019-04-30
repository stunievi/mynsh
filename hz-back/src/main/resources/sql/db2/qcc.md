企查查日志查询
===
select
@pageTag(){
    log.*,
    d.v_value as type,
    u.TRUE_NAME as username
@}
from T_QCC_LOG log 
left join t_dict d on d.name = 'qcc_log_type' and d.v_key = log.type
left join t_user u on u.id=log.OPERATOR
where 1=1
@if(isNotEmpty(type)){
    and log.TYPE=#type#
@}
@if(isNotEmpty(endTime)){
    and log.ADD_TIME<=#endTime#
@}
@if(isNotEmpty(startTime)){
    and log.ADD_TIME>=#startTime#
@}
@if(isNotEmpty(userName)){
    and u.true_name like #'%'+ userName+ '%'#
@}
@pageIgnoreTag(){
    order by log.add_time desc
@}



