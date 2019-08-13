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