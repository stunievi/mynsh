查询子文件ID
===
with rs(id,pid) as (
select id,pid as pid from c_file where id in (#join(ids)#) and uid = #uid#
union all
select o1.id, o1.pid from c_file o1, rs o2 where o1.pid = o2.id
) select * from rs