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
    order by link.add_time desc
@}

loan_related_search
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


数据源限制
===
select value(var_value,'') from T_SYSTEM_VARIABLE where var_name = 'ods_br_id' fetch first 1 rows only

condition_loan
===
--查询数据范围（总行角色看所有，贷款机构看所属一级支行）
and (((select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')>0)
    or ((0=(select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')) and (p1.MAIN_BR_ID in 
        (select substr(acc_code,1,5) from T_ORG where PARENT_ID in (select ID from T_ORG where acc_code in (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)) and TYPE = 'DEPARTMENT') or (p1.MAIN_BR_ID in (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#) 
        or  (((09131 = (	SELECT	MAIN_BR_ID 	FROM	T_DEPARTMENT_USER 		WHERE	UID=#uid#) ) and  p1.MAIN_BR_ID in 009213) or      ((09323 = (	SELECT			MAIN_BR_ID 	FROM		T_DEPARTMENT_USER 		WHERE	UID=#uid#) ) and  p1.MAIN_BR_ID in 009375 ))))
))

loan_link_list
===
select
@pageTag(){
    ROW_NUMBER()OVER(ORDER BY p1.CUS_ID) as number,
    p1.LOAN_AMOUNT,
    p1.MAIN_BR_ID,
    DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
    p1.BILL_NO,
    p1.CUS_NAME as LOAN_CUS_NAME,
    p1.LOAN_ACCOUNT,
    d1.*,
    d2.v_value as ASSURE_MEANS_MAIN
@}
from RPT_M_RPT_SLS_ACCT as p1
left join
@if(LINK_MODEL=='groupCus'){
    T_GROUP_CUS_LIST
@}else if(LINK_MODEL=='linkList'){
    T_RELATED_PARTY_LIST
@}else if(LINK_MODEL=='stockHolder'){
    T_SHARE_HOLDER_LIST
@}
as d1 on d1.CERT_CODE = value(p1.PSN_CERT_CODE, p1.ENT_CERT_CODE)
left join t_dict d2 on d2.name = 'ASSURE_MEANS_MAIN' and d2.V_KEY = p1.ASSURE_MEANS_MAIN
where p1.CREUNIT_NO = (#use("数据源限制")#)
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
and d1.CERT_CODE is not NULL
and d1.CERT_CODE = value(p1.PSN_CERT_CODE, p1.ENT_CERT_CODE)
-- 开户证件号
@if(isNotEmpty(CERT_CODE)){
    and value(p1.PSN_CERT_CODE, p1.ENT_CERT_CODE) like #'%' + CERT_CODE + '%'#
@}
-- 客户号
@if(isNotEmpty(CUS_ID)){
    and p1.CUS_ID like #'%' + CUS_ID + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}
--查询数据范围
#use("condition_loan")#
@if(isNotEmpty(own)){
    and (
    p1.loan_account in (select loan_account from t_loan_belong where uid = #uid#)
    or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')
    )
@}
@pageIgnoreTag(){
   order by p1.ACCOUNT_STATUS 
@}


查询资质客户
===
select 
@pageTag(){
   cus.*,
   user.TRUE_NAME as USER_TRUE_NAME
@}
from t_qual_cus as cus 
left join t_user user on operator = user.id
where 1=1 and ('admin' = (select username from t_user where id = #uid#) or operator = #uid#)
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and cus.CUS_NAME like #'%' + CUS_NAME + '%'#
@}
--客户类型
@if(isNotEmpty(CUS_TYPE)){
    and cus.CUS_TYPE = #CUS_TYPE#
@}
-- 客户名称
@if(isNotEmpty(COMPANY_NAME)){
    and cus.COMPANY_NAME like #'%' + COMPANY_NAME + '%'#
@}
@pageIgnoreTag(){
   order by cus.ADD_TIME DESC
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
   order by cus.ADD_TIME DESC
@}