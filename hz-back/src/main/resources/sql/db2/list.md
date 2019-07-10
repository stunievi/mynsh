insert_group_cus
===
insert into t_group_cus_list(id,cus_name,add_time,cert_code,link_name,link_cert_code,link_rule,remark_1,remark_2,remark_3,data_flag)values(#id#,#cus_name#,#add_time#,#cert_code#,#link_name#,#link_cert_code#,#link_rule#,#remark_1#,#remark_2#,#remark_3#,#data_flag#)

insert_holder_link
===
insert into t_related_party_list(id,related_name,add_time,cert_code,link_rule,remark_1,remark_2,remark_3,data_flag)values(#id#,#related_name#,#add_time#,#cert_code#,#link_rule#,#remark_1#,#remark_2#,#remark_3#,#data_flag#)

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

search_holder_link
===
---股东关联列表
select
@pageTag(){
    ROW_NUMBER()OVER(ORDER BY id) as number, link.*
@}
from T_RELATED_PARTY_LIST link where 1=1
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