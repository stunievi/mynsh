查询失信信息
===
select 
@pageTag(){
ID,
SOURCE_ID,
UNIQUE_NO,
NAME,
LI_AN_DATE,
AN_NO,
ORG_NO,
OWNER_NAME,
EXECUTE_GOV,
PROVINCE,
EXECUTE_UNITE,
YI_WU,
EXECUTE_STATUS,
ACTION_REMARK,
PUBLIC_DATE,
AGE,
SEXY,
UPDATE_DATE,
EXECUTE_NO,
PERFORMED_PART,
UNPERFORM_PART,
ORG_TYPE,
ORG_TYPE_NAME,
INPUT_DATE
@}
from QCC_SHIXIN
where inner_company_name = #fullName#

查询被执行信息
===
select 
@pageTag(){
ID,
SOURCE_ID,
NAME,
LI_AN_DATE,
AN_NO,
EXECUTE_GOV,
BIAO_DI,
STATUS,
PARTY_CARD_NUM,
UPDATE_DATE,
INPUT_DATE
@}
from QCC_ZHIXING
where inner_company_name = #fullName#

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
Court_Month,
INPUT_DATE
@}
from QCC_JUDGMENT_DOC
where inner_company_name = #fullName#
@if(!isEmpty(caseReason)){
    and Case_Reason = #caseReason#
@}
@if(!isEmpty(submitDateStart)){
    and #submitDateStart# <= submit_date
@}
@if(!isEmpty(submitDateEnd)){
    and #submitDateEnd# >= submit_date
@}

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
recorder,
INPUT_DATE
from 
QCC_JUDGMENT_DOC
where id = #id#

查询裁判文书详情-开庭公告
===
select
CASE_NO,
OPEN_DATE,
DEFENDANT,
CASE_REASON,
PROSECUTOR,
ID,
INPUT_DATE
from QCC_JUDGMENT_DOC_CN where QCC_DETAILS_ID = #id#

查询裁判文书详情-关联公司
===
select key_no,name from QCC_JUDGMENT_DOC_COM
where QCC_DETAILS_ID = #id#

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
id,
INPUT_DATE
@}
from QCC_COURT_ANNOUNCEMENT
where inner_company_name = #fullName#

查询法院公告详情
===
select 
COURT,
CONTENT,
SUBMIT_DATE,
PROVINCE,
CATEGORY,
PUBLISHED_DATE,
PARTY,
INPUT_DATE
from QCC_COURT_ANNOUNCEMENT
where Id = #id#

查询法院公告-当事人信息
===
select 
key_no,name
from QCC_COURT_ANNOUNCEMENT_PEOPLE
where id = #id#

查询开庭公告列表
===
select 
@pageTag(){
((select LISTAGG(name,'\t') from QCC_COURT_NOTICE_PEOPLE where id = no.id and type = '02' )) as DEFENDANT_LIST,
no.EXECUTE_GOV,
((select LISTAGG(name,'\t')  from QCC_COURT_NOTICE_PEOPLE where id = no.id and type = '01' )) as PROSECUTOR_LIST,
no.LI_AN_DATE,
no.CASE_REASON,
no.ID,
no.CASE_NO,
INPUT_DATE
@}
from 
QCC_COURT_NOTICE no where inner_company_name = #fullName#

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
CASE_NO,
INPUT_DATE
from 
QCC_COURT_NOTICE where id = #id#

查询开庭公告关联人
===
select name,key_no
from QCC_COURT_NOTICE_PEOPLE 
where id = #id# and type = #type#

查询司法协助信息
===
select
ja.inner_id,
ja.EXECUTED_BY,
ja.EQUITY_AMOUNT,
ja.ENFORCEMENT_COURT,
ja.EXECUTION_NOTICE_NUM,
ja.STATUS,
ja.INPUT_DATE
from QCC_JUDICIAL_ASSISTANCE ja
where ja.inner_company_name = #fullName#

查询司法协助EquityFreezeDetail
===
select 
d0.COMPANY_NAME as COMPANY_NAME,
d0.EXECUTION_MATTERS as EXECUTION_MATTERS,
d0.EXECUTION_DOC_NUM as EXECUTION_DOC_NUM,
d0.EXECUTION_VERDICT_NUM as EXECUTION_VERDICT_NUM,
d0.EXECUTED_PERSON_DOC_TYPE as EXECUTED_PERSON_DOC_TYPE,
d0.EXECUTED_PERSON_DOC_NUM as EXECUTED_PERSON_DOC_NUM,
d0.FREEZE_START_DATE as FREEZE_START_DATE,
d0.FREEZE_END_DATE as FREEZE_END_DATE,
d0.FREEZE_TERM as FREEZE_TERM,
d0.PUBLIC_DATE as PUBLIC_DATE,
d0.ja_inner_id
from QCC_EQUITY_FREEZE_DETAIL d0 
where d0.inner_company_name = #fullName# and FREEZE_TYPE = 1

查询司法协助EquityUnFreezeDetail
===
select
d1.EXECUTION_MATTERS as EXECUTION_MATTERS,
d1.EXECUTION_VERDICT_NUM as EXECUTION_VERDICT_NUM,
d1.EXECUTION_DOC_NUM as EXECUTION_DOC_NUM,
d1.EXECUTED_PERSON_DOC_TYPE as EXECUTED_PERSON_DOC_TYPE,
d1.EXECUTED_PERSON_DOC_NUM as EXECUTED_PERSON_DOC_NUM,
d1.UN_FREEZE_DATE as UN_FREEZE_DATE,
d1.PUBLIC_DATE as PUBLIC_DATE,
d1.THAW_ORGAN as THAW_ORGAN,
d1.THAW_DOC_NO as THAW_DOC_NO,
d1.ja_inner_id
from QCC_EQUITY_FREEZE_DETAIL d1 
where d1.inner_company_name = #fullName# and FREEZE_TYPE = 2

查询司法协助JudicialPartnersChangeDetail
===
select 
d2.EXECUTION_MATTERS as EXECUTION_MATTERS,
d2.EXECUTION_VERDICT_NUM as EXECUTION_VERDICT_NUM,
d2.EXECUTED_PERSON_DOC_TYPE as EXECUTED_PERSON_DOC_TYPE,
d2.EXECUTED_PERSON_DOC_NUM as EXECUTED_PERSON_DOC_NUM,
d2.ASSIGNEE as ASSIGNEE,
d2.ASSIST_EXEC_DATE as ASSIST_EXEC_DATE,
d2.ASSIGNEE_DOC_KIND as ASSIGNEE_DOC_KIND,
d2.ASSIGNEE_REG_NO as ASSIGNEE_REG_NO,
d2.STOCK_COMPANY_NAME as STOCK_COMPANY_NAME,
d2.ja_inner_id
from QCC_EQUITY_FREEZE_DETAIL d2
where d2.inner_company_name = #fullName# and FREEZE_TYPE = 3


查询企业经营异常信息
===
select 
ADD_REASON,
ADD_DATE,
ROMOVE_REASON,
REMOVE_DATE,
DECISION_OFFICE,
REMOVE_DECISION_OFFICE,
INPUT_DATE
from QCC_OP_EXCEPTION
where inner_company_name = #fullName#

查询司法拍卖列表
===
select
@pageTag(){
ID,
title as Name,
EXECUTE_GOV,
ACTION_REMARK,
YI_WU,
INPUT_DATE
@}
from QCC_JUDICIAL_SALE 
where inner_company_name = #fullName#

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
END_DATE,
INPUT_DATE
@}
from QCC_LAND_MORTGAGE
where inner_company_name = #fullName#

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
lm.INPUT_DATE,
re1.key_no as re1_key_no,
re1.name as re1_name,
re2.key_no as re2_key_no,
re2.name as re2_name
from QCC_LAND_MORTGAGE lm
left join QCC_LAND_MORTGAGE_PEOPLE re1 on re1.id = lm.id and re1.type = '02'
left join QCC_LAND_MORTGAGE_PEOPLE re2 on re2.id = lm.id and re2.type = '01'
where lm.id = #id#

查询环保处罚列表
===
select
@pageTag(){
ID,
CASE_NO,
PUNISH_DATE,
ILLEGAL_TYPE,
PUNISH_GOV,
INPUT_DATE
@}
from QCC_ENV_PUNISHMENT_LIST where inner_company_name = #fullName#

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
IMPLEMENTATION,
INPUT_DATE
from QCC_ENV_PUNISHMENT_LIST where
id = #id#

查询动产抵押
===
select
cm.REGISTER_NO,
cm.REGISTER_DATE,
cm.PUBLIC_DATE,
cm.REGISTER_OFFICE,
cm.DEBT_SECURED_AMOUNT,
cm.inner_id,
cm.INPUT_DATE,

ex1.Regist_No as ex1_Regist_No,
ex1.Regist_Date as ex1_Regist_Date,
ex1.Regist_Office as ex1_Regist_Office,
ex2.KIND as ex2_kind,
ex2.AMOUNT as ex2_AMOUNT,
ex2.ASSURANCE_SCOPE as ex2_ASSURANCE_SCOPE,
ex2.FULFILL_OBLIGATION as ex2_FULFILL_OBLIGATION,
ex2.REMARK as ex2_REMARK,
ex3.Cancel_Date as ex3_Cancel_Date,
ex3.Cancel_Reason as ex3_Cancel_Reason
from QCC_CHATTEL_MORTGAGE cm 
left join QCC_CMD_PLEDGE ex1 on ex1.CM_ID = cm.inner_id
left join QCC_CMD_SECURED_CLAIM ex2 on ex2.CM_ID = cm.inner_id
left join QCC_CMD_CANCEL_INFO ex3 on ex3.CM_ID = cm.inner_id
where cm.inner_company_name = #fullName#

查询动产抵押PledgeeList
===
select 
Name,
Identity_Type,
Identity_No,
cm_id
from QCC_CMD_PLEDGEE_LIST
where inner_company_name = #fullName#

查询动产抵押GuaranteeList
===
select 
NAME,
OWNERSHIP,
OTHER,
REMARK,
cm_id
from QCC_CMD_GUARANTEE_LIST						
where inner_company_name = #fullName#

查询动产抵押ChangeList
===
select
Change_Date,
Change_Content,
cm_id
from QCC_CMD_CHANGE_LIST
where inner_company_name = #fullName#

查询工商信息表
===
select 
KEY_NO,
NAME,
NO,
BELONG_ORG,
OPER_NAME,
START_DATE,
END_DATE,
STATUS,
PROVINCE,
UPDATED_DATE,
CREDIT_CODE,
REGIST_CAPI,
ECON_KIND,
ADDRESS,
SCOPE,
TERM_START,
TEAM_END,
CHECK_DATE,
ORG_NO,
IS_ON_STOCK,
STOCK_NUMBER,
STOCK_TYPE,
IMAGE_URL,
INPUT_DATE
from QCC_DETAILS
where inner_company_name = #fullName#

查询工商信息曾用名信息表
===
select 
NAME,
CHANGE_DATE
from QCC_DETAILS_ORIGINAL_NAME
where inner_company_name = #fullName#

查询工商信息股东信息表
===
select 
STOCK_NAME,
STOCK_TYPE,
STOCK_PERCENT,
SHOULD_CAPI,
SHOUD_DATE,
INVEST_TYPE,
INVEST_NAME,
REAL_CAPI,
CAPI_DATE
from QCC_DETAILS_PARTNERS
where inner_company_name = #fullName#

查询工商信息主要人员信息表
===
select
NAME,
JOB
from QCC_DETAILS_EMPLOYEES
where inner_company_name = #fullName#

查询工商信息分支机构表
===
select
COMPANY_ID,
REG_NO,
NAME,
BELONG_ORG,
CREDIT_CODE,
OPER_NAME
from QCC_DETAILS_BRANCHES
where inner_company_name = #fullName#

查询工商信息变更信息表
===
select
PROJECT_NAME,
BEFORE_CONTENT,
AFTER_CONTENT,
CHANGE_DATE
from QCC_DETAILS_CHANGE_RECORDS
where inner_company_name = #fullName#

查询工商信息联系信息表
===
select
WEB_SITE,
PHONE_NUMBER,
EMAIL
from QCC_DETAILS_CONTACT_INFO
where inner_company_name = #fullName#

查询工商信息行业信息表
===
select 
INDUSTRY_CODE,
INDUSTRY,
SUB_INDUSTRY_CODE,
SUB_INDUSTRY,
MIDDLE_CATEGORY_CODE,
MIDDLE_CATEGORY,
SMALL_CATEGORY_CODE,
SMALL_CATEGORY
from QCC_DETAILS_INDUSTRY
where inner_company_name = #fullName#


查询历史工商信息表
===
select 
Key_No,
his_data,
INPUT_DATE
from QCC_HIS_ECI
where inner_company_name = #fullName#

查询历史工商信息-历史主要人员-职位信息表
===
select 
Key_No,
Employee_Name,
JOB,
EL_INNER_ID
from QCC_HIS_ECI_EMPLOYEE_LIST_JOB						
where inner_company_name = #fullName#

查询历史对外投资信息表
===
select
@pageTag(){
CHANGE_DATE,
KEY_NO,
COMPANY_NAME,
OPER_NAME,
REGIST_CAPI || '万元人民币' as REGIST_CAPI,
ECON_KIND,
STATUS,
FUNDED_RATIO || '%' as FUNDED_RATIO,
START_DATE,
INPUT_DATE
@}
from QCC_HIS_INVESTMENT						
where inner_company_name = #fullName#
@if(!isEmpty(registCapiMin)){
    and INT(REGIST_CAPI) >= #registCapiMin#
@}
@if(!isEmpty(registCapiMax)){
    and INT(REGIST_CAPI) <= #registCapiMax#
@}
@if(!isEmpty(fundedRatioMin)){
    and INT(FUNDED_RATIO) >= #fundedRatioMin#
@}
@if(!isEmpty(fundedRatioMax)){
    and INT(FUNDED_RATIO) <= #fundedRatioMax#
@}



查询历史股东信息表
===
select
Change_Date_List
from QCC_HIS_SHARE_HOLDER						
where inner_company_name = #fullName#

查询历史股东-股东列表
===
select
@pageTag(){
PARTNER_NAME,
STOCK_PERCENT,
SHOULD_CAPI,
SHOULD_DATE,
SHOULD_TYPE,
Change_date_list,
INPUT_DATE
@}
from QCC_HIS_SHARE_HOLDER_DETAILS
where inner_company_name = #fullName#

查询历史失信信息表						
===
select
@pageTag(){
ID,
ACTION_REMARK,
EXECUTE_NO,
EXECUTE_STATUS,
EXECUTE_UNITE,
YI_WU,
PUBLIC_DATE,
CASE_NO,
EXECUTE_GOV,
AN_NO,
PROVINCE,
LI_AN_DATE,
ORG_NO,
ORG_TYPE,
ORG_TYPE_NAME,
NAME,
INPUT_DATE
@}
from QCC_HIS_SHIXIN						
where inner_company_name = #fullName#

查询历史被执行信息表						
===
select
@pageTag(){
BIAO_DI,
CASE_NO,
EXECUTE_GOV,
AN_NO,
PROVINCE,
LI_AN_DATE,
ORG_NO,
ORG_TYPE,
ORG_TYPE_NAME,
NAME,
INPUT_DATE
@}
from   QCC_HIS_ZHIXING						
where inner_company_name = #fullName#

查询历史法院公告信息表
===
select
@pageTag(){
ID,
CATEGORY,
CONTENT,
COURT,
PARTY,
PROVINCE,
PUBLISH_PAGE,
SUBMIT_DATE,
PUBLISH_DATE,
INPUT_DATE
@}
from QCC_HIS_COURT_NOTICE						
where inner_company_name = #fullName#


查询历史裁判文书信息表
===
select
@pageTag(){
ID,
COURT,
CASE_NAME,
SUBMIT_DATE,
CASE_NO,
CASE_TYPE,
CASE_ROLE,
COURT_YEAR
@}
from QCC_HIS_JUDGEMENT
where inner_company_name = #fullName#

查询历史开庭公告信息表
===
select
@pageTag(){
ID,
CASE_REASON,
PROSECUTOR_LIST,
DEFENDANT_LIST,
EXECUTE_GOV,
CASE_NO,
LI_AN_DATE,
INPUT_DATE
@} 
from QCC_HIS_SESSION_NOTICE
where inner_company_name = #fullName#

查询历史动产抵押信息表
===
select
@pageTag(){
REGISTER_NO,
REGISTER_DATE,
REGISTER_OFFICE,
DEBT_SECURED_AMOUNT,
STATUS,
INPUT_DATE
@}
from QCC_HIS_M_PLEDGE 
where inner_company_name = #fullName#

查询历史股权出质信息表
===
select
@pageTag(){
REGIST_NO,
PLEDGOR,
PLEDGEE,
PLEDGED_AMOUNT,
REG_DATE,
PUBLIC_DATE,
STATUS,
INPUT_DATE
@}
from QCC_HIS_PLEDGE
where inner_company_name = #fullName#

查询历史行政处罚-工商行政处罚信息表
===
select
DOC_NO,
PENALTY_TYPE,
CONTENT,
PENALTY_DATE,
PUBLIC_DATE,
OFFICE_NAME,
INPUT_DATE
from QCC_HIS_ADMIN_PENALTY_EL
where inner_company_name = #fullName#

查询历史行政处罚-信用中国行政处罚信息表
===
select
CASE_NO,
NAME,
LI_AN_DATE,
PROVINCE,
OWNER_NAME,
CASE_REASON,
INPUT_DATE
from QCC_HIS_ADMIN_PENALTY_CCL
where inner_company_name = #fullName#

查询历史行政许可-工商行政许可信息表
===
select
LICENS_DOC_NO,
LICENS_DOC_NAME,
LICENS_OFFICE,
LICENS_CONTENT,
VALIDITY_FROM,
VALIDITY_TO,
INPUT_DATE
from QCC_HIS_ADMIN_LICENS_EL
where inner_company_name = #fullName#

查询历史行政许可-信用中国行政许可信息表
===
select
CASE_NO,
NAME,
LI_AN_DATE,
PROVINCE,
OWNER_NAME,
INPUT_DATE
from QCC_HIS_ADMIN_LICENS_CCL
where inner_company_name = #fullName#

查询新增的公司信息表						
===
select
@pageTag(){
KEY_NO,
NAME,
OPER_NAME,
START_DATE,
STATUS,
NO,
CREDIT_CODE,
REGIST_CAPI,
ADDRESS,
INPUT_DATE
@}
from QCC_FRESH						
where Address like #'%' + keyword + '%'#

查询企业族谱
===
select 
NAME,
KEY_NO,
CATEGORY,
SHORT_NAME,
COUNT,
LEVEL,
inner_parent_id,
inner_id,
INPUT_DATE
from QCC_COMPANY_MAP
where inner_company_no = #keyNo#

查询股权结构图
===
select 
NAME,
KEY_NO,
CATEGORY,
COUNT,
FUNDED_RATIO,
IS_ABSOLUTE_CONTROLLER,
GRADE,
OPER_NAME,
IN_PARENT_ACTUAL_RADIO,
inner_id,
inner_parent_id,
INPUT_DATE
from QCC_CESM
where inner_company_no = #keyNo#

查询股权结构-实际控股信息表
===
select
NAME,
STOCK_TYPE,
KEY_NO,
SUB_CON_AMT,
FUNDED_RATIO,
INPUT_DATE
from QCC_CESM_ACLP
where inner_company_no = #keyNo#

查询投资图谱
===
select
NAME,
KEY_NO,
CATEGORY,
SHORT_NAME,
COUNT,
LEVEL,
inner_id,
inner_parent_id,
INPUT_DATE
from QCC_TREE_RELATION_MAP
where inner_company_no = #keyNo#


查询公司信息表
===
select
KEY_NO,
COMPANY_NAME,
NAME_COUNT,
INPUT_DATE
from QCC_HOLDING_COMPANY
where inner_company_name = #fullName#

查询控股公司列表信息表
===
select
@pageTag(){
KEY_NO,
NAME,
PERCENT_TOTAL,
LEVEL,
SHORT_STATUS,
START_DATE,
REGIST_CAPI,
IMAGE_URL,
ECON_KIND,
paths,
oper,
INPUT_DATE
@}
from QCC_HOLDING_COMPANY_NAMES
where inner_company_name = #fullName#

查询股权穿透十层信息表
===
select 
TERM_START,
TEAM_END,
CHECK_DATE,
KEY_NO,
NAME,
NO,
BELONG_ORG,
OPER_NAME,
START_DATE,
END_DATE,
STATUS,
PROVINCE,
UPDATED_DATE,
SHORT_STATUS,
REGIST_CAPI,
ECON_KIND,
ADDRESS,
SCOPE,
ORG_NO,
CREDIT_CODE,
Stock_Statistics,
INPUT_DATE
from QCC_SAD						
where inner_company_name = #fullName#

查询股权穿透十层股东信息表						
===
select
COMPANY_ID,
STOCK_NAME,
STOCK_TYPE,
STOCK_PERCENT,
IDENTIFY_TYPE,
IDENTIFY_NO,
SHOULD_CAPI,
SHOUD_DATE,
INPUT_DATE
from QCC_SAD_PARTNERS						
where inner_company_name = #fullName#

查询股权穿透十层股东列表
===
select 
KEY_NO,
NAME,
PATH_NAME,
REGIST_CAPI,
ECON_KIND,
STOCK_TYPE,
FUNDED_AMOUNT,
FUNDED_RATE,
INVEST_TYPE,
LEVEL,
inner_id,
inner_parent_id,
INPUT_DATE
from QCC_SAD_STOCK_LIST
where inner_company_name = #fullName#
