searchDicts
===
select 
@pageTag(){
* 
@}
from t_dict where 1 = 1
@if(isNotEmpty(name)){
    and name like #'%' + name + '%'#
@}
@pageIgnoreTag(){
    order by name
@}

查询系统日志
===
select 
@pageTag(){
log.*,
u.true_name
@}
from t_system_log log
left join t_user u on u.id = log.user_id
where 1 = 1
@if(isNotEmpty(userName)){
    and u.true_name like #'%'+userName+'%'# 
@}
@if(isNotEmpty(startTime)){
    and log.ADD_TIME>=#startTime# 
@}
@if(isNotEmpty(endTime)){
    and log.ADD_TIME<=#endTime#
@}
@if(isNotEmpty(method)){
    and log.method like #'%'+method+'%'# 
@}
@pageIgnoreTag(){
    order by log.add_time desc
@}

查询消息模板列表
===
select 
@pageTag(){
*
@}
from t_message_template
where 1 = 1
@if(isNotEmpty(name)){
    and name like #'%' + name + '%'#
@}
@pageIgnoreTag(){
    order by id desc
@}

查询实际控制人
===
select 
lm.*,
DB2INST1.func_get_dict('CERT_TYPE', type) as ctype
from t_loan_manager lm
where LOAN_ACCOUNT = #loanAccount#


查询短信发送历史列表
===
select 
@pageTag(){
log.*
, (di.v_value) as state
@}
from t_short_message_log log
left join t_dict di on di.name = '短信接口状态' and v_key = log.state
where 1 = 1
@if(isNotEmpty(phone)){
    and log.phone like #'%'+phone+'%'#
@}
@if(isNotEmpty(keyword)){
    and log.message like #'%'+keyword+'%'#
@}
@if(isNotEmpty(startDate)){
    and log.add_time >= timestamp('#text(startDate)#')
@}
@if(isNotEmpty(endDate)){
    and log.add_time <= timestamp('#text(endDate)#')
@}
@pageIgnoreTag(){
    order by log.add_time desc
@}

查询未读消息
===
select
@pageTag(){
*
@}
from t_system_notice
where user_id = #uid#
and state = 'UNREAD'
@pageIgnoreTag(){
order by add_time desc
@}

标为已读
===
update t_system_notice set state='READ' 
where type='SYSTEM' AND state='UNREAD' AND id=#id# AND user_id=#uid#


删除已读
===
delete from t_system_notice where type='SYSTEM' AND state='READ' AND user_id=#uid#
