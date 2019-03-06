查询未读消息
===
select * from (
select distinct msg.*,mr.unread_num, row_number() over(partition by mr.TO_TYPE, mr.TO_ID, mr.USER_ID order by send_time desc) as rn from t_message msg
right join t_message_read mr on mr.user_id = msg.TO_ID and msg.to_type = 'USER' where mr.user_id = #uid#
) inq
where inq.rn between 1 and min(inq.UNREAD_NUM,20)

查询聊天记录
===
select 
@pageTag(){
    msg.*
     , u1.true_name as from_name
     , u2.true_name as to_name
@}
from t_message msg 
left join t_user u1 on u1.id = msg.from_id and msg.from_type = 'USER'
left join t_user u2 on u2.id = msg.to_id and msg.to_type = 'USER'
where 
--我发的
(msg.from_type = 'USER' and msg.from_id = #uid# and msg.to_type = 'USER' and msg.to_id = #touid#) 
--我收的
or (msg.to_type = 'USER' and msg.to_id = #uid# and msg.from_type = 'USER' and msg.from_id = #touid# )
@pageIgnoreTag(){
    order by send_time desc
@}

未读消息数递增
===
update t_message_read set unread_num = unread_num + 1 where user_id = #uid# and to_type = #totype# and to_id = #toid#


查找用户
===
select 
@pageTag(){
*
@}
from t_department_user 

查询系统通知
===
select
@pageTag(){
no.*
, value(d.v_value,no.type) as type
, value(d2.v_value,no.state) as state
, case when (no.from_uid = 0 or no.from_uid is null) then '系统' else u.true_name end as send_utname
, u2.true_name as rev_utname
@}
from t_system_notice no
left join t_user u on u.id = no.from_uid
left join t_user u2 on u2.id = no.user_id
left join t_dict d on d.name = '消息-类型' and d.v_key = no.type
left join t_dict d2 on d2.name = '消息-状态' and d2.v_key = no.state
where 
@if(isNotEmpty(sent)){
    from_uid = #uid#
@}else{
    user_id = #uid#
@}
@if(isNotEmpty(state)){
    and no.state = #state#
@}
@if(isNotEmpty(type)){
    and no.type = #type#
@}
@if(isNotEmpty(utname)){
    @if(isNotEmpty(sent)){
        and u2.true_name like #'%'+utname+'%'#
    @}else{
        and u.true_name like #'%'+utname+'%'#
    @}
@}
@pageIgnoreTag(){
    order by add_time desc
@}

查询消息
===
select no.*
, value(d.v_value,no.type) as type
, value(d2.v_value,no.state) as state
, case when no.from_uid = 0 then '系统' else u.true_name end as send_utname
, u2.true_name as rec_utname
from t_system_notice no
left join t_user u on u.id = no.from_uid
left join t_user u2 on u2.id = no.user_id
left join t_dict d on d.name = '消息-类型' and d.v_key = no.type
left join t_dict d2 on d2.name = '消息-状态' and d2.v_key = no.state
where no.id = #id#
