search_group_cus_list
===
---集团客户列表
select 
@pageTag(){
    ROW_NUMBER()OVER(ORDER BY id) as number, list.*
@}
from t_group_cus_list as list
where 1=1
@if(isNotEmpty(CUS_NAME)){
    and list.cus_name like #'%' + CUS_NAME + '%'#
@}
@if(isNotEmpty(CERT_CODE)){
    and list.cert_code = #CERT_CODE#
@}
@if(isNotEmpty(LINK_NAME)){
    and list.link_name like #'%' + LINK_NAME + '%'#
@}
@if(isNotEmpty(LINK_CERT_CODE)){
    and list.link_cert_code = #LINK_CERT_CODE#
@}
@if(isNotEmpty(LINK_RULE)){
    and list.link_rule like #LINK_RULE#
@}
@if(isNotEmpty(DATA_FLAG)){
    and list.data_flag = #DATA_FLAG#
@}
@pageIgnoreTag(){
   order by list.link_name
@}

search_holder_link
===
---股东关联列表
select
@pageTag(){
    ROW_NUMBER()OVER(ORDER BY id) as number, link.*
@}
from T_RELATED_PARTY_LIST link where 1=1
    and LINK_RULE like '%12.%'
@if(isNotEmpty(RELATED_NAME)){
    and RELATED_NAME like #'%' + RELATED_NAME + '%'#
@}
@if(isNotEmpty(CERT_CODE)){
    and cert_code like #'%' + CERT_CODE + '%'#
@}
@if(isNotEmpty(LINK_RULE)){
    and link_rule like #'%' + LINK_RULE + '%'#
@}
@if(isNotEmpty(DATA_FLAG)){
    and DATA_FLAG like #'%' + DATA_FLAG + '%'#
@}
@pageIgnoreTag(){
   order by link_info
@}

查询资质客户关联方
===
select 
@pageTag(){
   cus.*,
   user.TRUE_NAME as USER_TRUE_NAME
@}
from T_QUAL_CUS_RELATED as cus 
left join t_user user on operator = user.id
where 1=1 and ('admin' = (select username from t_user where id = #uid#) or operator = #uid#)
--资质客户ID
and cus.QUAL_CUS_ID = #QUAL_CUS_ID#
@pageIgnoreTag(){
   order by cus.ADD_TIME 
@}