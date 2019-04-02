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
