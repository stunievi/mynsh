查询子文件ID
===
with rs(id,pid) as (
select id,pid as pid from c_file where id in (#join(ids)#) and uid = #uid#
union all
select o1.id, o1.pid from c_file o1, rs o2 where o1.pid = o2.id
) select * from rs

文件列表
===
select 
@pageTag(){
* 
@}
from c_file where uid = #uid#
and pid = #pid#

我的分享列表
===
select 
@pageTag(){
f.*, sh.last_modify as share_date, sh.id as sid
@}
from c_file_share sh
inner join c_file f
on f.id = sh.fid
where from_uid = #uid#

我的分享列表2
===
select 
@pageTag(){
* 
@}
from c_file where pid = #pid#
