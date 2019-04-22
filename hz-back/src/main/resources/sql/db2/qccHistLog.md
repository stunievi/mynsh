企查查历史信息列表id查询
===
select
    QCC_ID 
from QCC_HIST_LOG 
where 1=1
@if(isNotEmpty(qcc_id)){
    and QCC_ID=#qcc_id#
@}
@if(isNotEmpty(full_name)){
    and FULL_NAME=#full_name#
@}
group by QCC_ID having count(QCC_ID) > 1


