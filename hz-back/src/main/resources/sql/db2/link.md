insert_group_cus
===
insert into t_group_cus_list(id,cus_name,add_time,cert_code,link_name,link_cert_code,link_rule,remark_1,remark_2,remark_3,data_flag)values(#id#,#cus_name#,#add_time#,#cert_code#,#link_name#,#link_cert_code#,#link_rule#,#remark_1#,#remark_2#,#remark_3#,#data_flag#)

insert_holder_link
===
insert into t_related_party_list(id,related_name,add_time,cert_code,link_rule,remark_1,remark_2,remark_3,data_flag)values(#id#,#related_name#,#add_time#,#cert_code#,#link_rule#,#remark_1#,#remark_2#,#remark_3#,#data_flag#)

delete_02_holder_link
===
delete from t_related_party_list where data_flag = '02'

delete_02_group_cus
===
delete from t_group_cus_list where data_flag = '02'

search_group_cus_list
===
---集团客户列表
select 
@pageTag(){
    ROW_NUMBER()OVER(ORDER BY id) as number, list.*
@}
from t_group_cus_list as list
where 1=1
@if(isNotEmpty(ID)){
    and list.id = #ID#
@}
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
@pageIgnoreTag(){
    order by link.add_time desc
@}

loan_related_search
===
---关联方查询日志
select
@pageTag(){
    *
@}
from T_LOAN_RELATED_SEARCH where OPERATOR = #uid#
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and CERT_CODE like #'%' + CERT_CODE + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and CUS_NAME like #'%' + CUS_NAME + '%'#
@}
@if(isNotEmpty(END_TIME)){
    and ADD_TIME<=#END_TIME#
@}
@if(isNotEmpty(START_TIME)){
    and ADD_TIME>=#START_TIME#
@}
@pageIgnoreTag(){
    order by add_time desc
@}

loan_related_search_log
===
---关联方查询日志
select
@pageTag(){
    search.add_time add_time,
    search.ratio ratio,
    search.cus_name cus_name,
    search.cert_code cert_code,
    search.operator operator,
    search.MAIN_BR_ID main_by_id,
    org.name oname,
    user.TRUE_NAME uname
@}
from T_LOAN_RELATED_SEARCH  as search
left join t_org org on MAIN_BR_ID = org.id 
left join t_user user on operator = user.id where 
('admin' = (select username from t_user where id = #uid#) or operator = #uid#)
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and CERT_CODE like #'%' + CERT_CODE + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and CUS_NAME like #'%' + CUS_NAME + '%'#
@}
@if(isNotEmpty(END_TIME)){
    and search.ADD_TIME<=#END_TIME#
@}
@if(isNotEmpty(START_TIME)){
    and search.ADD_TIME>=#START_TIME#
@}
@pageIgnoreTag(){
    order by search.add_time desc
@}