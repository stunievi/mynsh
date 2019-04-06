查询失信信息
===
select 
@pageTag(){
*
@}
from QCC_SHIXIN
where company_name = #searchKey#

查询被执行信息
===
select 
@pageTag(){
*
@}
from QCC_ZHIXING
where company_name = #searchKey#

查询裁判文书列表
===
select
@pageTag(){
id,
court,
Case_Name,
Case_No,
Case_Type,
Submit_Date,
Update_Date,
Is_Prosecutor,
Is_Defendant,
Court_Year,
Case_Role,
Court_Level,
Case_Reason,
Case_Reason_Type,
Court_Month
@}
from QCC_JUDGMENT_DOC
where company_name = #searchKey#

查询裁判文书详情
===
select 
id,
case_name,
case_no,
case_type,
content,
court,
create_date,
submit_date,
update_date,
appellor,
judge_date,
case_reason,
trial_round,
defendant_list,
prosecutor_list,
is_valid,
content_clear,
judge_result,
party_info,
trial_procedure,
court_consider,
plaintiff_request,
defendant_reply,
court_inspect,
plaintiff_request_of_first,
defendant_reply_of_first,
court_inspect_of_first,
court_consider_of_first,
appellant_request,
appellee_arguing,
execute_process,
collegiate_bench,
judege_date,
recorder
from 
QCC_JUDGMENT_DOC
where id = #id#

查询裁判文书详情-开庭公告
===
select * from JG_JD_NOTICE_RE where CN_ID = #id#

查询裁判文书详情-关联公司
===
select key_no,name from JG_JD_COM_RE where jd_id = #id#

查询法院公告列表
===
select
@pageTag(){
upload_date,
court,
content,
category,
published_date,
published_page,
party,
id
@}
from QCC_COURT_ANNOUNCEMENT
where COMPANY_NAME = #companyName#

查询法院公告详情
===
select 
COURT,
CONTENT,
SUBMIT_DATE,
PROVINCE,
CATEGORY,
PUBLISH_DATE,
PARTY
from QCC_COURT_ANNOUNCEMENT
where Id = #id#

查询法院公告-当事人信息
===
select 
key_no,name
from 

查询开庭公告列表
===
select 
@pageTag(){
((select LISTAGG(name,'\t') from JG_NOTICE_PEOPLE_RE where CN_ID = no.id and type = '02' )) as DEFENDANT_LIST,
no.EXECUTE_GOV,
((select LISTAGG(name,'\t')  from JG_NOTICE_PEOPLE_RE where CN_ID = no.id and type = '01' )) as PROSECUTOR_LIST,
no.LI_AN_DATE,
no.CASE_REASON,
no.ID,
no.CASE_NO
@}
from 
QCC_COURT_NOTICE no where company_name = #searchKey#

查询开庭公告详情
===
select
PROVINCE,
CASE_REASON,
SCHEDULE_TIME,
EXECUTE_GOV,
UNDERTAKE_DEPARTMENT,
EXECUTE_UNITE,
CHIEF_JUDGE,
OPEN_TIME,
CASE_NO
from 
QCC_COURT_NOTICE where id = #id#

查询开庭公告关联人
===
select name,key_no
from JG_NOTICE_PEOPLE_RE 
where CN_ID = #id# and type = #type#

查询司法协助信息
===
select
ja.EXECUTED_BY,
ja.EQUITY_AMOUNT,
ja.ENFORCEMENT_COURT,
ja.EXECUTION_NOTICE_NUM,
ja.STATUS,
d1.COMPANY_NAME as d1_COMPANY_NAME,
d1.EXECUTION_MATTERS as d1_EXECUTION_MATTERS,
d1.EXECUTION_DOC_NUM as d1_EXECUTION_DOC_NUM,
d1.EXECUTION_VERDICT_NUM as d1_EXECUTION_VERDICT_NUM,
d1.EXECUTED_PERSON_DOC_TYPE as d1_EXECUTED_PERSON_DOC_TYPE,
d1.EXECUTED_PERSON_DOC_NUM as d1_EXECUTED_PERSON_DOC_NUM,
d1.FREEZE_START_DATE as d1_FREEZE_START_DATE,
d1.FREEZE_END_DATE as d1_FREEZE_END_DATE,
d1.FREEZE_TERM as d1_FREEZE_TERM,
d1.PUBLIC_DATE as d1_PUBLIC_DATE,

d2.EXECUTION_MATTERS as d2_EXECUTION_MATTERS,
d2.EXECUTION_VERDICT_NUM as d2_EXECUTION_VERDICT_NUM,
d2.EXECUTION_DOC_NUM as d2_EXECUTION_DOC_NUM,
d2.EXECUTED_PERSON_DOC_TYPE as d2_EXECUTED_PERSON_DOC_TYPE,
d2.EXECUTED_PERSON_DOC_NUM as d2_EXECUTED_PERSON_DOC_NUM,
d2.UN_FREEZE_DATE as d2_UN_FREEZE_DATE,
d2.PUBLIC_DATE as d2_PUBLIC_DATE,
d2.THAW_ORGAN as d2_THAW_ORGAN,
d2.THAW_DOC_NO as d2_THAW_DOC_NO,

d3.EXECUTION_MATTERS as d3_EXECUTION_MATTERS,
d3.EXECUTION_VERDICT_NUM as d3_EXECUTION_VERDICT_NUM,
d3.EXECUTED_PERSON_DOC_TYPE as d3_EXECUTED_PERSON_DOC_TYPE,
d3.EXECUTED_PERSON_DOC_NUM as d3_EXECUTED_PERSON_DOC_NUM,
d3.ASSIGNEE as d3_ASSIGNEE,
d3.ASSIST_EXEC_DATE as d3_ASSIST_EXEC_DATE,
d3.ASSIGNEE_DOC_KIND as d3_ASSIGNEE_DOC_KIND,
d3.ASSIGNEE_REG_NO as d3_ASSIGNEE_REG_NO,
d3.STOCK_COMPANY_NAME as d3_STOCK_COMPANY_NAME

from QCC_JUDICIAL_ASSISTANCE ja
left join QCC_EQUITY_FREEZE_DETAIL d1 on ja.inner_id = d1.ja_id and d1.FREEZE_TYPE = 1
left join QCC_EQUITY_FREEZE_DETAIL d2 on d2.ja_id = ja.inner_id and d2.FREEZE_TYPE = 2
left join QCC_EQUITY_FREEZE_DETAIL d3 on d3.ja_id = ja.inner_id and d3.FREEZE_TYPE = 3
where ja.company_name = #keyWord#

查询企业经营异常信息
===
select 
ADD_REASON,
ADD_DATE,
ROMOVE_REASON,
REMOVE_DATE,
DECISION_OFFICE,
REMOVE_DECISION_OFFICE
from QCC_OP_EXCEPTION
where company_name = #keyNo#

查询司法拍卖列表
===
select
@pageTag(){
ID,
title as Name,
EXECUTE_GOV,
ACTION_REMARK,
YI_WU
@}
from QCC_JUDICIAL_SALE 
where company_name = #keyWord#

查询司法拍卖详情
===
select
title,context
from QCC_JUDICIAL_SALE
where id = #id#

查询土地抵押列表
===
select
@pageTag(){
ID,
ADDRESS,
ADMINISTRATIVE_AREA,
MORTGAGE_ACREAGE,
MORTGAGE_PURPOSE,
START_DATE,
END_DATE
@}
from QCC_LAND_MORTGAGE
where company_name = #keyWord#

查询土地抵押详情
===
select
lm.LAND_SIGN,
lm.LAND_NO,
lm.ADMINISTRATIVE_AREA,
lm.ACREAGE,
lm.ADDRESS,
lm.OBLIGEE_NO,
lm.USUFRUCT_NO,
lm.MORTGAGOR_NATURE,
lm.MORTGAGE_PURPOSE,
lm.NATURE_AND_TYPE,
lm.MORTGAGE_ACREAGE,
lm.ASSESSMENT_PRICE,
lm.MORTGAGE_PRICE,
lm.ON_BOARD_START_TIME,
lm.ON_BOARD_END_TIME,
re1.key_no as re1_key_no,
re1.name as re1_name,
re2.key_no as re2_key_no,
re2.name as re2_name
from QCC_LAND_MORTGAGE lm
left join JG_LM_PEOPLE_RE re1 on re1.cn_id = lm.id and re1.type = '02'
left join JG_LM_PEOPLE_RE re2 on re2.cn_id = lm.id and re2.type = '01'
where id = #id#

查询环保处罚列表
===
select
@pageTag(){
ID,
CASE_NO,
PUNISH_DATE,
ILLEGAL_TYPE,
PUNISH_GOV 
@}
from QCC_ENV_PUNISHMENT_LIST where
company_name = #keyWord#

查询环保处罚详情
===
select
CASE_NO,
ILLEGAL_TYPE,
PUNISH_REASON,
PUNISH_BASIS,
PUNISHMENT_RESULT,
PUNISH_DATE,
PUNISH_GOV,
IMPLEMENTATION
from QCC_ENV_PUNISHMENT_LIST where
id = #id#
