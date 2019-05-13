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

查询支行行长1
===
select uid from T_USER_ORG userOrg 
    right join (select id,name from T_ORG q 
    right join (select PARENT_ID from T_ORG org  
    right join (select uo.oid from t_user_org uo where uo.uid=#uid#) a 
    on a.oid= org.id) b on b.PARENT_ID=q.PARENT_ID
    where  
    q.name like '%支行行长%' or q.name like '%支行副行长%') c  on c.ID = userOrg.OID
    
查询支行行长2
===
select uid from T_USER_ORG userOrg 
    right join (select * from T_ORG d
     right join (select q.PARENT_ID as p_parent_id from T_ORG q 
    right join (select org.PARENT_ID as org_parent_id from T_ORG org  
    inner join (select uo.oid from t_user_org uo where uo.uid=#uid#) a 
    on a.oid= org.id) b on b.org_parent_id=q.ID) e on e.P_PARENT_ID=d.PARENT_ID
    where  
    d.name like '%支行行长%' or d.name like '%支行副行长%') c  on c.ID = userOrg.OID
    
保存企查查接口调用次数
===
insert into T_QCC_COUNT (ID,ADD_TIME,IF_NAME_EN,IF_NAME_CH,COUNT,ORDER_ID,DATA_ID) VALUES (#ID#,#ADD_TIME#,#IF_NAME_EN#,#IF_NAME_CH#,#COUNT#,#ORDER_ID#,#DATA_ID#)

查询企查查接口调用次数
===
select 
@pageTag(){
   if_name_ch,sum(count) as number 
@}
from T_QCC_COUNT where 1=1
@if(isNotEmpty(beginTime)){
    and ADD_TIME>=#beginTime#
@}
@if(isNotEmpty(endTime)){
    and ADD_TIME<=#endTime#
@}
group by if_name_ch



