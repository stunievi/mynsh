查看列表
===
select 
@pageTag(){
record.* ,
u.true_name as utname
@}
from t_gps_record record
left join t_user u on u.id = record.uid
where record.uid = #uid# 
@pageIgnoreTag(){
order by record.add_time desc
@}

