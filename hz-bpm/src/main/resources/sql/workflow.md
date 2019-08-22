查找节点处理人员
===
select distinct r1.*,
case when r1.uid = #uid# then 1000
when r1.otype = 'ROLE' then 100
when r1.pid = r2.pid then 10
else 0
end as ooo

from t_org_user r1 
left join t_org_user r2 on r2.uid = #uid# and r2.pid is not null 
where
(r1.uid in (#join(uids)#) or r1.oid in (#join(rids)#) or r1.oid in (#join(qids)#) or r1.pid in (#join(dids)#))
--必须和我有关联
@if(has(dep)){
   and (select count(1) from t_org_user where uid = #uid# and pid in (select pid from t_org_user where uid = r1.uid)) > 0
@}
order by ooo desc
fetch first 20 rows only

查找节点人员-新版
===
select distinct uid, uname,utname from (
--角色授權
select uid,uname,utname from t_org_user where otype = 'ROLE' and oid in(#join(rids)#)
union all
--人員授權
select id,username,true_name from t_user where id in (#join(uids)#)
union all
--部門授權
select  u.id as uid,u.username as uname,u.true_name as utname
from t_user_dep uo 
inner join t_user u on uo.uid = u.id
where uo.type = 'USER' and did in (#join(dids)#)
)
where 1 = 1
--自动选择流程发起人
@if(has(self)){
    and uid = #uid#
@}
--自动选择当前部门主管
@if(has(ms)){
    and uid in (
        select uid from t_user_dep where type = 'MANAGER' and did in (select did from t_user_dep where type = 'USER' and uid = #uid#)
    )
@}
--自动选择当前部门上级主管
@if(has(tms0)){
    and uid in (
        --上级主管领导
        select uid from t_user_dep where type = 'TOP_MANAGER0' and did in (select did from t_user_dep where type = 'USER' and uid = #uid#)
        union all
        --上级部门的主管领导
        select uid from t_user_dep where type = 'MANAGER' and did in (
            select parent_id from t_org where did in (select did from t_user_dep where type = 'USER' and uid = #uid#)
        ) 
    )
@}



查找部门人员-新版关联
===
select u.true_name as utname,u.username as uname, u.id as uid, u.phone
from t_user_dep uo 
inner join t_user u on uo.uid = u.id
where uo.did = #id#
@if(!isEmpty(type)){
and uo.type = #type#
@}