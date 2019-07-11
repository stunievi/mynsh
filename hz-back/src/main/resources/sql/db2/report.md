condition
===
--查询数据范围（总行角色看所有，贷款机构看所属一级支行）
and (((select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')>0)
    or ((0=(select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')) and (p1.MAIN_BR_ID in 
        (select substr(acc_code,1,5) from T_ORG where PARENT_ID in (select ID from T_ORG where acc_code in (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)) and TYPE = 'DEPARTMENT') or (p1.MAIN_BR_ID in (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#)
        or  (((09131 = (	SELECT	MAIN_BR_ID 	FROM	T_DEPARTMENT_USER 		WHERE	UID=#uid#) ) and  p1.MAIN_BR_ID in 009213) or      ((09323 = (	SELECT			MAIN_BR_ID 	FROM		T_DEPARTMENT_USER 		WHERE	UID=#uid#) ) and  p1.MAIN_BR_ID in 009375 ))))
))

condition_cus
===
--查询数据范围（总行角色看所有，贷款机构看所属一级支行）
and (((select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')>0)
    or ((0=(select count(1) from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')) and ((u2.MAIN_BR_ID in 
        (select substr(acc_code,1,5) from T_ORG where PARENT_ID in (select ID from T_ORG where acc_code in (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)) and TYPE = 'DEPARTMENT') or (u2.MAIN_BR_ID in (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#))) or 
        (u3.MAIN_BR_ID in (select substr(acc_code,1,5) from T_ORG where PARENT_ID in (select ID from T_ORG where acc_code in (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)) and TYPE = 'DEPARTMENT') or (u3.MAIN_BR_ID in (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#))))
))

信贷中间表
===
with p1 as
(select *
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#
)


get_rpt_now
===
select
p1.SRC_SYS_DATE
from RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO = '0801'
--只取一条记录
FETCH FIRST 1 ROWS ONLY



cap_expect
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO = '0801'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--开始时间
@if(isNotEmpty(START_DATE)){
    and p1.LOAN_END_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and p1.LOAN_END_DATE<#END_DATE#
@}
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

int_expect
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.LOAN_BALANCE * p1.REALITY_IR_Y / 12 * (
  SELECT DAYS(to_date(#END_DATE#,'yyyyMMdd')) - DAYS(to_date(#START_DATE#,'yyyyMMdd'))
  FROM RPT_M_RPT_SLS_ACCT p2
  WHERE p2.BILL_NO = p1.BILL_NO
 ) / 30 as INT_CUMU
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO = '0801'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--贷款余额
and p1.LOAN_BALANCE>0
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

cap_overdue
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.UNPD_PRIN_BAL,
p1.CAP_OVERDUE_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--拖欠本金
and p1.UNPD_PRIN_BAL>0
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

int_overdue
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.DELAY_INT_CUMU,
p1.INTEREST_OVERDUE_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--欠息累积
and p1.DELAY_INT_CUMU>0
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_1
===
select
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as BAD_LOAN_BALANCE,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as HIDE_LOAN_BALANCE,
coalesce(sum(p1.DELAY_INT_CUMU)/10000,0) as DELAY_INT_CUMU,
coalesce(sum(p2.DELAY_INT_CUMU)/10000,0) as BAD_DELAY_INT_CUMU,
coalesce(sum(p3.DELAY_INT_CUMU)/10000,0) as HIDE_DELAY_INT_CUMU
@}
from RPT_M_RPT_SLS_ACCT_HIS as p1
left join (select LOAN_BALANCE,LOAN_ACCOUNT,DELAY_INT_CUMU from RPT_M_RPT_SLS_ACCT_HIS where CLA in ('30','40','50') and GL_CLASS not like '0%') as p2 on p1.LOAN_ACCOUNT=p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,DELAY_INT_CUMU from RPT_M_RPT_SLS_ACCT_HIS where CLA not in ('30','40','50') and (UNPD_PRIN_BAL>0 or DELAY_INT_CUMU>0) and GL_CLASS not like '0%') as p3 on p1.LOAN_ACCOUNT=p3.LOAN_ACCOUNT
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--贷款余额
and p1.LOAN_BALANCE >0
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}

report_6
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.INTEREST_OVERDUE_DATE,
p1.DELAY_INT_CUMU,
p1.UNPD_PRIN_BAL,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
DB2INST1.func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
p1.SRC_SYS_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
--欠息累积
and p1.DELAY_INT_CUMU>0
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_7
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.DELAY_INT_CUMU,
p1.INTEREST_OVERDUE_DATE,
p1.UNPD_PRIN_BAL,
p1.CAP_OVERDUE_DATE,
p1.SRC_SYS_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--五级分类（隐性不良）
and p1.CLA not in ('30','40','50')
--欠息累积or拖欠本金
and (p1.DELAY_INT_CUMU>0 or p1.UNPD_PRIN_BAL>0)
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_8
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.DELAY_INT_CUMU,
p1.INTEREST_OVERDUE_DATE,
p1.UNPD_PRIN_BAL,
p1.CAP_OVERDUE_DATE,
p1.SRC_SYS_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--五级分类（隐性不良）
and p1.CLA in ('30','40','50')
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_9
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
p1.SRC_SYS_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
and p1.ACCOUNT_STATUS = '1'
--五级分类（隐性不良）
and p1.CLA in ('30','40','50')
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--排序
--order by p1.MAIN_BR_ID

report_10
===
select 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('SEVEN_RESULT',r1.CLA_RESULT_PRE) as CLA_RESULT_PRE,
DB2INST1.func_get_dict('SEVEN_RESULT',r1.CLA_RESULT) as CLA_RESULT,
r1.CLA_DATE,
p1.SRC_SYS_DATE
@}
from RSC_TASK_INFO_HIS r1
left join RPT_M_RPT_SLS_ACCT p1 on r1.BILL_NO = p1.BILL_NO
where
--法人机构号（惠州农商银行0801）
r1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--开始时间
@if(isNotEmpty(START_DATE)){
    and r1.CLA_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and r1.CLA_DATE<#END_DATE#
@}
--五级上调
and r1.CLA_RESULT>r1.CLA_RESULT_PRE

report_11
===
select
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_1,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_2,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_3
@}
from RPT_M_RPT_SLS_ACCT_HIS p1
left join (select LOAN_BALANCE,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT_HIS where CREUNIT_NO = '0801' and LN_TYPE in ('普通贷款','银团贷款') and SRC_SYS_DATE= #SRC_SYS_DATE#) as p2 on p1.LOAN_ACCOUNT=p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT_HIS where CREUNIT_NO = '0801' and LN_TYPE in ('转贴现') and SRC_SYS_DATE= #SRC_SYS_DATE#) as p3 on p1.LOAN_ACCOUNT=p3.LOAN_ACCOUNT
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_12
===
select
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE
@}
from RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
-- 贷款分类
@if(isNotEmpty(LN_TYPE)){
    and p1.LN_TYPE= #LN_TYPE#
@}
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_12_1
===
select
@pageTag(){
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE
@}
from RPT_M_RPT_SLS_ACCT_HIS p2
where
--法人机构号（惠州农商银行0801
p2.CREUNIT_NO != '10086'
--表内资产
and p2.GL_CLASS not like '0%'
-- 贷款分类
@if(isNotEmpty(LN_TYPE)){
    and p2.LN_TYPE= #LN_TYPE#
@}
-- 主管机构
@if(null != MAIN_BR_ID){
    and p2.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p2.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_13
===
select
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE
@}
from RPT_M_RPT_SLS_ACCT p1
left join ACC_LOAN a1 on p1.LOAN_ACCOUNT = a1.LOAN_ACCOUNT
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--按揭标识
and a1.MORTGAGE_FLG = 1
-- 贷款分类
@if(isNotEmpty(LN_TYPE)){
    and p1.LN_TYPE= #LN_TYPE#
@}
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_13_1
===
select
@pageTag(){
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE
@}
from RPT_M_RPT_SLS_ACCT_HIS p2
left join ACC_LOAN a1 on p2.LOAN_ACCOUNT = a1.LOAN_ACCOUNT
where
--法人机构号（惠州农商银行0801
p2.CREUNIT_NO != '10086'
--表内资产
and p2.GL_CLASS not like '0%'
--按揭标识
and a1.MORTGAGE_FLG = 1
-- 贷款分类
@if(isNotEmpty(LN_TYPE)){
    and p2.LN_TYPE= #LN_TYPE#
@}
-- 主管机构
@if(null != MAIN_BR_ID){
    and p2.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p2.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_17
===
SELECT 
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30,
coalesce(sum(p4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_40
@}
FROM RPT_M_RPT_SLS_ACCT p1
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT where CLA in ('20') and ACCOUNT_STATUS = '1') p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT where CLA in ('30') and ACCOUNT_STATUS = '1') p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT where CLA in ('40') and ACCOUNT_STATUS = '1') p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_17_1
===
SELECT 
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30,
coalesce(sum(p4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_40
@}
FROM RPT_M_RPT_SLS_ACCT_HIS p1
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT_HIS where CLA in ('20') and ACCOUNT_STATUS = '1' and MAIN_BR_ID in (#join(MAIN_BR_ID)#) and SRC_SYS_DATE= #SRC_SYS_DATE#) p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT_HIS where CLA in ('30') and ACCOUNT_STATUS = '1' and MAIN_BR_ID in (#join(MAIN_BR_ID)#) and SRC_SYS_DATE= #SRC_SYS_DATE#) p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA from RPT_M_RPT_SLS_ACCT_HIS where CLA in ('40') and ACCOUNT_STATUS = '1' and MAIN_BR_ID in (#join(MAIN_BR_ID)#) and SRC_SYS_DATE= #SRC_SYS_DATE#) p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_19
===
SELECT 
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_10,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_10_BAD,
coalesce(sum(p4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30,
coalesce(sum(p5.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30_BAD,
coalesce(sum(p6.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20,
coalesce(sum(p7.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20_BAD,
coalesce(sum(p8.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_00,
coalesce(sum(p9.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_00_BAD
@}
FROM RPT_M_RPT_SLS_ACCT p1
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '10' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '10' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '30' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '30' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p5 on p1.LOAN_ACCOUNT = p5.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '20' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p6 on p1.LOAN_ACCOUNT = p6.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '20' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p7 on p1.LOAN_ACCOUNT = p7.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '00' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p8 on p1.LOAN_ACCOUNT = p8.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '00' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p9 on p1.LOAN_ACCOUNT = p9.LOAN_ACCOUNT
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--担保类型
and p1.ASSURE_MEANS_MAIN in ('00','10','20','30')
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_20
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.CUS_NAME,
ROUND(p1.LOAN_AMOUNT/10000,2),
ROUND(p1.LOAN_BALANCE/10000,2),
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
DB2INST1.func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
p1.LOAN_TERM
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
--贷款余额
and p1.LOAN_BALANCE > 0
--排序
order by p1.LOAN_AMOUNT DESC
fetch first 10 rows only

report_21
===
SELECT 
@pageTag(){
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(p2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_RE,
coalesce(sum(p3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_10_BAD,
coalesce(sum(p4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30,
coalesce(sum(p5.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_30_BAD,
coalesce(sum(p6.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20,
coalesce(sum(p7.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_20_BAD,
coalesce(sum(p8.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_00,
coalesce(sum(p9.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_00_BAD
@}
FROM RPT_M_RPT_SLS_ACCT p1
left join (select PP1.LOAN_BALANCE,PP1.LOAN_ACCOUNT,PP1.CLA,PP1.MAIN_BR_ID,a1.MORTGAGE_FLG,a1.PRD_USERDF_TYPE from RPT_M_RPT_SLS_ACCT as PP1 left join ACC_LOAN as a1 on PP1.LOAN_ACCOUNT=a1.LOAN_ACCOUNT where PP1.CREUNIT_NO != '10086' and PP1.ASSURE_MEANS_MAIN = '10' and PP1.MAIN_BR_ID in (#join(MAIN_BR_ID)#) and a1.PRD_USERDF_TYPE = '2004') p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '10' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '30' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '30' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p5 on p1.LOAN_ACCOUNT = p5.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '20' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p6 on p1.LOAN_ACCOUNT = p6.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '20' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p7 on p1.LOAN_ACCOUNT = p7.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '00' and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p8 on p1.LOAN_ACCOUNT = p8.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT,CLA,MAIN_BR_ID from RPT_M_RPT_SLS_ACCT where CREUNIT_NO = '0801' and ASSURE_MEANS_MAIN = '00' and CLA in ('30','40','50') and MAIN_BR_ID in (#join(MAIN_BR_ID)#)) p9 on p1.LOAN_ACCOUNT = p9.LOAN_ACCOUNT
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--担保类型
and p1.ASSURE_MEANS_MAIN in ('00','10','20','30')
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}

report_25
===
SELECT 
@pageTag(){
p1.CUS_NAME,
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
Round(p1.DELAY_INT_CUMU/10000,2) as DELAY_INT_CUMU
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--排序
order by p1.DELAY_INT_CUMU DESC
fetch first 5 rows only


report_26
===
select
coalesce(sum(p1.LOAN_AMOUNT)/10000,0) as TOTAL,
coalesce(sum(p2.LOAN_AMOUNT)/10000,0) as TOTAL_1,
coalesce(sum(p3.LOAN_AMOUNT)/10000,0) as INT_RATE_G_1,
coalesce(sum(p4.LOAN_AMOUNT)/10000,0) as INT_RATE_F_1,
coalesce(sum(p5.LOAN_AMOUNT)/10000,0) as INT_RATE_G_2,
coalesce(sum(p6.LOAN_AMOUNT)/10000,0) as INT_RATE_F_3
FROM RPT_M_RPT_SLS_ACCT p1
left join (select LOAN_AMOUNT,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where LN_TYPE in ('普通贷款','银团贷款') and BIZ_TYPE_DETAIL not like '%手楼按揭%') 
    p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select LOAN_AMOUNT,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where LN_TYPE in ('普通贷款','银团贷款') and BIZ_TYPE_DETAIL not like '%手楼按揭%' 
    and INT_RATE_TYPE = '1') 
    p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
left join (select LOAN_AMOUNT,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where LN_TYPE in ('普通贷款','银团贷款') and BIZ_TYPE_DETAIL not like '%手楼按揭%' 
    and INT_RATE_TYPE = '2') 
    p4 on p1.LOAN_ACCOUNT = p4.LOAN_ACCOUNT
left join (select LOAN_AMOUNT,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where LN_TYPE = '转贴现' ) 
    p5 on p1.LOAN_ACCOUNT = p5.LOAN_ACCOUNT
left join (select LOAN_AMOUNT,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where LN_TYPE in ('普通贷款','银团贷款') and BIZ_TYPE_DETAIL like '%手楼按揭%') 
    p6 on p1.LOAN_ACCOUNT = p6.LOAN_ACCOUNT   
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--贷款类型
and p1.LN_TYPE in ('普通贷款','银团贷款','转贴现')
--开始时间
@if(isNotEmpty(START_DATE)){
    and p1.LOAN_START_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and p1.LOAN_START_DATE<#END_DATE#
@}
--期限类型
@if(isNotEmpty(LOAN_TERM_MIN)){
    and p1.LOAN_TERM > #LOAN_TERM_MIN#
@}
@if(isNotEmpty(LOAN_TERM_MAX)){
    and p1.LOAN_TERM <= #LOAN_TERM_MAX#
@}

report_27
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.CUS_NAME,  
p1.LOAN_ACCOUNT, 
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.REALITY_IR_Y,
p1.TERM_TYPE,
DB2INST1.func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
--开始时间
@if(isNotEmpty(START_DATE)){
    and p1.LOAN_START_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and p1.LOAN_START_DATE<#END_DATE#
@}
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_28
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT,
p1.CUS_NAME,
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.DELAY_INT_CUMU,
p1.INTEREST_OVERDUE_DATE,
p1.UNPD_PRIN_BAL,
p1.CAP_OVERDUE_DATE
@}
FROM RSC_TASK_INFO_HIS r1
left join RPT_M_RPT_SLS_ACCT as p1 on r1.BILL_NO = p1.BILL_NO
where 
--法人机构号（惠州农商银行0801）
r1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--原七级分类结果(为正常类、关注类)
and r1.CLA_RESULT_PRE < '30'
--七级分类认定结果
and r1.CLA_RESULT>= '30'
--开始时间
@if(isNotEmpty(START_DATE)){
    and r1.CLA_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and r1.CLA_DATE<#END_DATE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and r1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID


report_29
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.LOAN_ACCOUNT, 
p1.CUS_NAME,  
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.TERM_TYPE,
p1.UNPD_PRIN_BAL,
p1.CAP_OVERDUE_DATE,
p1.DELAY_INT_CUMU,
p1.INTEREST_OVERDUE_DATE
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
--拖欠本金/欠息累计>0
and (p1.UNPD_PRIN_BAL>0 or p1.DELAY_INT_CUMU>0)
--产品类别
@if(isNotEmpty(PRD_TYPE)){
    and p1.PRD_TYPE= #PRD_TYPE#
@}
--贷款性质
@if(isNotEmpty(LOAN_NATURE)){
    and p1.LOAN_NATURE= #LOAN_NATURE#
@}
-- 源系统日期
@if(isNotEmpty(SRC_SYS_DATE)){
    and p1.SRC_SYS_DATE= #SRC_SYS_DATE#
@}
--查询数据范围
#use("condition")#
--排序
--order by p1.MAIN_BR_ID

report_30
===
select
coalesce(sum(p1.LOAN_AMOUNT)/10000,0) as LOAN_AMOUNT,
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce((sum(p1.UNPD_PRIN_BAL)+sum(p1.DELAY_INT_CUMU))/10000,0) as TOTAL,
coalesce(sum(p2.UNPD_PRIN_BAL)/10000,0) as UNPD_PRIN_BAL,
coalesce(sum(p3.DELAY_INT_CUMU)/10000,0) as DELAY_INT_CUMU
FROM RPT_M_RPT_SLS_ACCT p1
left join (select UNPD_PRIN_BAL,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where UNPD_PRIN_BAL>0) 
    p2 on p1.LOAN_ACCOUNT = p2.LOAN_ACCOUNT
left join (select DELAY_INT_CUMU,LOAN_ACCOUNT from RPT_M_RPT_SLS_ACCT 
    where DELAY_INT_CUMU>0) 
    p3 on p1.LOAN_ACCOUNT = p3.LOAN_ACCOUNT
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--贷款类型
and p1.LN_TYPE in ('普通贷款','银团贷款')
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
--期限类型
@if(isNotEmpty(LOAN_TERM_MIN)){
    and p1.LOAN_TERM > #LOAN_TERM_MIN#
@}
@if(isNotEmpty(LOAN_TERM_MAX)){
    and p1.LOAN_TERM <= #LOAN_TERM_MAX#
@}


report_31
===
select
coalesce(sum(p1.LOAN_AMOUNT)/10000,0) as LOAN_AMOUNT,
count(1) as ACCOUNT_NUM
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--贷款类型
and p1.LN_TYPE in ('普通贷款','银团贷款')
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
--开始时间
@if(isNotEmpty(START_DATE)){
    and p1.LOAN_START_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and p1.LOAN_START_DATE<#END_DATE#
@}
--期限类型
@if(isNotEmpty(LOAN_TERM_MIN)){
    and p1.LOAN_TERM > #LOAN_TERM_MIN#
@}
@if(isNotEmpty(LOAN_TERM_MAX)){
    and p1.LOAN_TERM <= #LOAN_TERM_MAX#
@}

report_32
===
select
coalesce(sum(p1.LOAN_AMOUNT)/10000,0) as LOAN_AMOUNT,
coalesce(sum(p1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
count(p1.LOAN_AMOUNT) as ACCOUNT_NUM
FROM RSC_TASK_INFO_HIS r1
left join RPT_M_RPT_SLS_ACCT as p1 on r1.BILL_NO = p1.BILL_NO
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--贷款类型
--and p1.LN_TYPE in ('普通贷款','银团贷款')
-- 主管机构
@if(null != MAIN_BR_ID){
    and p1.MAIN_BR_ID in (#join(MAIN_BR_ID)#)
@}
--原七级分类结果(为正常类、关注类)
and r1.CLA_RESULT_PRE < '30'
--七级分类认定结果
and r1.CLA_RESULT>= '30'
--开始时间
@if(isNotEmpty(START_DATE)){
    and r1.CLA_DATE>=#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and r1.CLA_DATE<=#END_DATE#
@}
--期限类型
@if(isNotEmpty(LOAN_TERM_MIN)){
    and p1.LOAN_TERM > #LOAN_TERM_MIN#
@}
@if(isNotEmpty(LOAN_TERM_MAX)){
    and p1.LOAN_TERM <= #LOAN_TERM_MAX#
@}

report_33_1
===
select
LOAN_ACCOUNT,
CUS_NAME,
REM_REPAYS,
LOAN_BALANCE
FROM RPT_M_RPT_SLS_ACCT
where
--法人机构号（惠州农商银行0801
CREUNIT_NO != '10086'
--表内资产
and GL_CLASS not like '0%'
-- 贷款帐号
@if(null != LOAN_ACCOUNT){
    and LOAN_ACCOUNT = #LOAN_ACCOUNT#
@}

report_33_2
===
select
b1.TRN_DATE as REPAY_START_DATE,
p1.LOAN_BALANCE,
b1.PRIN_AMT+b1.TOT_INT as TOT_AMT,
b1.PRIN_AMT,
b1.TOT_INT,
b1.INT_AMT,
b1.PIA_AMT,
b1.IIA_AMT,
b1.AFTER_INT_AMT,
b1.TRN_DATE
FROM BOCT_88 b1
left join (select substr(LOAN_ACCOUNT,1,16) as ACCT_NO,substr(SRC_SYS_DATE,1,6) as SRC_SYS_DATE,LOAN_BALANCE,LOAN_ACCOUNT,CUS_NAME,REPAYMENT_MODE,REM_REPAYS,GL_CLASS from RPT_M_RPT_SLS_ACCT_HIS) p1 on b1.ACCT_NO = p1.ACCT_NO
where
--法人机构号（惠州农商银行0801
b1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--匹配信贷中间表历史表对应月份的记录，从而查出该月台帐的贷款余额
and substr(b1.TRN_DATE,1,6) = p1.SRC_SYS_DATE
-- 贷款帐号
@if(null != LOAN_ACCOUNT){
    and b1.ACCT_NO = #LOAN_ACCOUNT#
@}
--排序
order by b1.TRN_DATE


report_33_3
===
select
@pageTag(){
    i1.REPAY_START_DATE,
    p1.LOAN_BALANCE,
    i1.TIMES,
    i1.REPAY_CYCLE,
    i1.REPAY_AMOUNT,
    i1.LOAN_ACCOUNT,
    i1.STATUS
@}
from
IQP_REPAY_SCH as i1
left join (select LOAN_ACCOUNT,LOAN_BALANCE,GL_CLASS from RPT_M_RPT_SLS_ACCT) p1 on i1.LOAN_ACCOUNT = p1.LOAN_ACCOUNT
where i1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
-- 贷款帐号（16位）
and i1.LOAN_ACCOUNT= #LOAN_ACCOUNT#
--还款日期不为空
and i1.REPAY_START_DATE <> '0000-00-00'
--发送状态
and i1.STATUS = '11'
--排序
order by i1.SERNO



report_34
===
SELECT 
@pageTag(){
p1.MAIN_BR_ID, 
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
p1.CUS_NAME,  
p1.LOAN_ACCOUNT, 
p1.LOAN_AMOUNT,
p1.LOAN_BALANCE,
p1.LOAN_START_DATE,
p1.LOAN_END_DATE,
p1.REALITY_IR_Y,
p1.TERM_TYPE,
DB2INST1.func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA,
p1.UNPD_PRIN_BAL,
p1.DELAY_INT_CUMU
@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'
--台帐状态
--and p1.ACCOUNT_STATUS = '1'
--有欠本欠息
and ((UNPD_PRIN_BAL>0) or (DELAY_INT_CUMU>0))
--是协议还款
and REPAYMENT_MODE = '202'

--排序
--order by p1.MAIN_BR_ID

rpt2
===
SELECT 
@pageTag(){
p1.*,
DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME,
DB2INST1.func_get_dict('ASSURE_MEANS_MAIN',p1.ASSURE_MEANS_MAIN) as ASSURE_MEANS_MAIN,
DB2INST1.func_get_dict('CLA',p1.CLA) as CLA

@}
FROM RPT_M_RPT_SLS_ACCT p1
where 
--法人机构号（惠州农商银行0801）
p1.CREUNIT_NO != '10086'
--表内资产
and p1.GL_CLASS not like '0%'

--排序
--order by p1.MAIN_BR_ID

rpt
===
select
    p1.*,
    DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME2
    -- user
    --实际控制人
        , lm.name as lm_name
        , lm.code as lm_code
        , lm.phone as lm_phone
        , lm.type as lm_type
        , lm.MMHTJYRQ_DATE as lm_MMHTJYRQ_DATE
        , d7.V_VALUE as lm_FCZ
        , lm.FCZ_DATE as lm_FCZ_DATE
        , d8.v_value as lm_REASON
        , lm.EXPLAIN as lm_EXPLAIN
        , lm.DEVELOPER_FULL_NAME as lm_DEVELOPER_FULL_NAME
        , lm.LP_FULL_NAME as lm_LP_FULL_NAME
        , lm.DSHJYZXQK as lm_DSHJYZXQK
        , u2.COM_MAIN_OPT_SCP
        , u2.COM_PART_OPT_SCP,
        value(d21.v_value,d22.v_value) as CRD_GRADE,
            d1.v_value as CUST_TYPE,
            d2.v_value as CERT_TYPE,
            d3.v_value as ASSURE_MEANS_MAIN,
            d4.v_value as CLA,
            d5.v_value as ACCOUNT_STATUS,
            d6.v_value as REPAYMENT_MODE
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
left join t_loan_manager lm on lm.loan_account = p1.loan_account

left join t_dict d1 on d1.name = 'CUST_TYPE' and d1.V_KEY = p1.CUST_TYPE
left join t_dict d2 on d2.name = 'CERT_TYPE' and d2.V_KEY = p1.CERT_TYPE
left join t_dict d3 on d3.name = 'ASSURE_MEANS_MAIN' and d3.V_KEY = p1.ASSURE_MEANS_MAIN
left join t_dict d4 on d4.name = 'CLA' and d4.V_KEY = p1.CLA
left join t_dict d5 on d5.name = 'ACCOUNT_STATUS' and d5.V_KEY = p1.ACCOUNT_STATUS
left join t_dict d6 on d6.name = 'REPAYMENT_MODE' and d6.V_KEY = p1.REPAYMENT_MODE
left join t_dict d7 on d7.name = 'FCZ_STATUS' and d7.V_KEY = lm.FCZ
left join t_dict d8 on d8.name = 'FCZ_REASON' and d8.V_KEY = lm.REASON

left join t_dict d21 on d21.name = 'COM_CRD_GRADE' and d21.V_KEY = u2.COM_CRD_GRADE
left join t_dict d22 on d22.name = 'CRD_GRADE' and d22.V_KEY = u3.CRD_GRADE

--历史台账
@if(isNotEmpty(history)){
    inner join T_WORKFLOW_INSTANCE hins on hins.state = 'FINISHED' and hins.model_name = #modelName# and hins.loan_account = p1.loan_account
@}


where p1.CREUNIT_NO != '10087'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')

--查询数据范围
#use("condition")#
    and (
    p1.loan_account in (select loan_account from t_loan_belong where uid = #uid#)
    or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')
    )
-- 贷款分类（一般贷款台帐固定为“普通贷款”）
@if(isNotEmpty(LN_TYPE)){
    and p1.LN_TYPE = #LN_TYPE#
@}
-- 贷款帐号
@if(isNotEmpty(LOAN_ACCOUNT)){
    and p1.LOAN_ACCOUNT like #'%' + LOAN_ACCOUNT + '%'#
@}

-- 借据号
@if(isNotEmpty(BILL_NO)){
    and p1.BILL_NO like #'%' + BILL_NO + '%'#
@}

-- 合同号
@if(isNotEmpty(CONT_NO)){
    and p1.CONT_NO like #'%' + CONT_NO + '%'#
@}

-- 客户号
@if(isNotEmpty(CUS_ID)){
    and p1.CUS_ID like #'%' + CUS_ID + '%'#
@}

-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}

-- 台账状态
@if(isNotEmpty(ACCOUNT_STATUS)){
    and p1.ACCOUNT_STATUS= #ACCOUNT_STATUS#
@}

-- 五级分类标志
@if(isNotEmpty(CLA)){
    and p1.CLA = #CLA#
@}

-- 是否逾期
@if(timeout == '1' || timeout == 1){
and (UNPD_PRIN_BAL>0 or DELAY_INT_CUMU>0)
@}else if(timeout == '0' || timeout == 0){
and ((UNPD_PRIN_BAL=0) or (UNPD_PRIN_BAL is null)) and ((DELAY_INT_CUMU=0) or (DELAY_INT_CUMU is null))
@}

-- 联系人
@if(isNotEmpty(CONTACT_NAME)){
    and u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'#
@}

-- 联系电话
@if(isNotEmpty(PHONE)){
    and u1.PHONE like #'%' + PHONE + '%'#
@}

-- 贷款起始日（开始）
@if(isNotEmpty(LOAN_START_DATE_S)){
    and p1.LOAN_START_DATE >= #LOAN_START_DATE_S#
@}

-- 贷款起始日（结束）
@if(isNotEmpty(LOAN_START_DATE_E)){
    and p1.LOAN_START_DATE <= #LOAN_START_DATE_E#
@}

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE)){
    and p1.LOAN_ACCOUNT like #'%' + LOAN_TYPE + '%'#
@}

-- 贷款用途
@if(isNotEmpty(USE_DEC)){
    and p1.USE_DEC like #'%' + USE_DEC + '%'#
@}

-- 产品细分类名 2019.5.20
@if(isNotEmpty(BIZ_TYPE_DETAIL)){
    and p1.BIZ_TYPE_DETAIL like #'%' + '按揭' + '%'#
@}
-- 出证状态
@if(isNotEmpty(FCZ)){
    and lm.FCZ =#FCZ#
@}

-- 不良台账
@if(!isEmpty(register)){
@if(register == 'true'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) > 0
@}else if(register == 'false'){
    and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) = 0
@}
@}


   order by p1.ACCOUNT_STATUS 
   
   
rpt_export
===
select
@pageTag(){
   p1.*,
   DB2INST1.fun_GET_ORG_BY_CODE(p1.MAIN_BR_ID) as MAIN_BR_NAME2
   -- user
   --实际控制人
       , lm.name as lm_name
       , lm.code as lm_code
       , lm.phone as lm_phone
       , lm.type as lm_type
       , lm.MMHTJYRQ_DATE as lm_MMHTJYRQ_DATE
       , d7.V_VALUE as lm_FCZ
       , lm.FCZ_DATE as lm_FCZ_DATE
       , d8.v_value as lm_REASON
       , lm.EXPLAIN as lm_EXPLAIN
       , lm.DEVELOPER_FULL_NAME as lm_DEVELOPER_FULL_NAME
       , lm.LP_FULL_NAME as lm_LP_FULL_NAME
       , lm.DSHJYZXQK as lm_DSHJYZXQK
       , u2.COM_MAIN_OPT_SCP
       , u2.COM_PART_OPT_SCP,
       value(d21.v_value,d22.v_value) as CRD_GRADE,
           d1.v_value as CUST_TYPE,
           d2.v_value as CERT_TYPE,
           d3.v_value as ASSURE_MEANS_MAIN,
           d4.v_value as CLA,
           d5.v_value as ACCOUNT_STATUS,
           d6.v_value as REPAYMENT_MODE
@}
from RPT_M_RPT_SLS_ACCT as p1
left join CUS_BASE as u1 on p1.CUS_ID=u1.CUS_ID
left join  CUS_COM as u2 on p1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID=u3.CUS_ID
left join t_loan_manager lm on lm.loan_account = p1.loan_account

left join t_dict d1 on d1.name = 'CUST_TYPE' and d1.V_KEY = p1.CUST_TYPE
left join t_dict d2 on d2.name = 'CERT_TYPE' and d2.V_KEY = p1.CERT_TYPE
left join t_dict d3 on d3.name = 'ASSURE_MEANS_MAIN' and d3.V_KEY = p1.ASSURE_MEANS_MAIN
left join t_dict d4 on d4.name = 'CLA' and d4.V_KEY = p1.CLA
left join t_dict d5 on d5.name = 'ACCOUNT_STATUS' and d5.V_KEY = p1.ACCOUNT_STATUS
left join t_dict d6 on d6.name = 'REPAYMENT_MODE' and d6.V_KEY = p1.REPAYMENT_MODE
left join t_dict d7 on d7.name = 'FCZ_STATUS' and d7.V_KEY = lm.FCZ
left join t_dict d8 on d8.name = 'FCZ_REASON' and d8.V_KEY = lm.REASON

left join t_dict d21 on d21.name = 'COM_CRD_GRADE' and d21.V_KEY = u2.COM_CRD_GRADE
left join t_dict d22 on d22.name = 'CRD_GRADE' and d22.V_KEY = u3.CRD_GRADE

--历史台账
@if(isNotEmpty(history)){
   inner join T_WORKFLOW_INSTANCE hins on hins.state = 'FINISHED' and hins.model_name = #modelName# and hins.loan_account = p1.loan_account
@}


where p1.CREUNIT_NO != '10087'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')

--表内资产
and p1.GL_CLASS not like '0%'

--查询数据范围
#use("condition")#
@if(isNotEmpty(own)){
    and (
    p1.loan_account in (select loan_account from t_loan_belong where uid = #uid#)
    or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')
    )
@}
-- 贷款分类（一般贷款台帐固定为“普通贷款”）
@if(isNotEmpty(LN_TYPE)){
   and p1.LN_TYPE = #LN_TYPE#
@}
-- 贷款帐号
@if(isNotEmpty(LOAN_ACCOUNT)){
   and p1.LOAN_ACCOUNT like #'%' + LOAN_ACCOUNT + '%'#
@}

-- 借据号
@if(isNotEmpty(BILL_NO)){
   and p1.BILL_NO like #'%' + BILL_NO + '%'#
@}

-- 合同号
@if(isNotEmpty(CONT_NO)){
   and p1.CONT_NO like #'%' + CONT_NO + '%'#
@}

-- 客户号
@if(isNotEmpty(CUS_ID)){
   and p1.CUS_ID like #'%' + CUS_ID + '%'#
@}

-- 客户名称
@if(isNotEmpty(CUS_NAME)){
   and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}

-- 台账状态
@if(isNotEmpty(ACCOUNT_STATUS)){
   and p1.ACCOUNT_STATUS= #ACCOUNT_STATUS#
@}

-- 五级分类标志
@if(isNotEmpty(CLA)){
   and p1.CLA = #CLA#
@}

-- 是否逾期
@if(timeout == '1' || timeout == 1){
and (UNPD_PRIN_BAL>0 or DELAY_INT_CUMU>0)
@}else if(timeout == '0' || timeout == 0){
and ((UNPD_PRIN_BAL=0) or (UNPD_PRIN_BAL is null)) and ((DELAY_INT_CUMU=0) or (DELAY_INT_CUMU is null))
@}

-- 联系人
@if(isNotEmpty(CONTACT_NAME)){
   and u1.CONTACT_NAME like #'%' + CONTACT_NAME + '%'#
@}

-- 联系电话
@if(isNotEmpty(PHONE)){
   and u1.PHONE like #'%' + PHONE + '%'#
@}

-- 贷款起始日（开始）
@if(isNotEmpty(LOAN_START_DATE_S)){
   and p1.LOAN_START_DATE >= #LOAN_START_DATE_S#
@}

-- 贷款起始日（结束）
@if(isNotEmpty(LOAN_START_DATE_E)){
   and p1.LOAN_START_DATE <= #LOAN_START_DATE_E#
@}

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE)){
   and p1.LOAN_ACCOUNT like #'%' + LOAN_TYPE + '%'#
@}

-- 贷款用途
@if(isNotEmpty(USE_DEC)){
   and p1.USE_DEC like #'%' + USE_DEC + '%'#
@}

-- 产品细分类名 2019.5.20
@if(isNotEmpty(BIZ_TYPE_DETAIL)){
   and p1.BIZ_TYPE_DETAIL like #'%' + '按揭' + '%'#
@}
-- 出证状态
@if(isNotEmpty(FCZ)){
   and lm.FCZ =#FCZ#
@}
-- 未按时出证原因
@if(isNotEmpty(REASON)){
    and lm.REASON = #REASON#
@}

-- 不良台账
@if(!isEmpty(register)){
@if(register == 'true'){
   and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) > 0
@}else if(register == 'false'){
   and (select count(*) from t_workflow_instance ins where ins.model_name in ('不良资产主流程') and ins.state <> 'FINISHED' and ins.loan_account = p1.LOAN_ACCOUNT) = 0
@}
@}
   

mortgage_g
===
SELECT 
@pageTag(){
g5.CONT_NO,
g3.*,
DB2INST1.func_get_dict('CUS_TYPE',g3.CUS_TYP) as CUS_TYP

@}
FROM GRT_G_BASIC_INFO g3
left join GRT_GUARANTY_RE g6 on g3.GUARANTY_ID = g6.GUARANTY_ID
left join GRT_LOANGUAR_INFO g5 on g6.GUAR_CONT_NO = g5.GUAR_CONT_NO
where 
--法人机构号（惠州农商银行0801）
g5.CREUNIT_NO != '10086'
-- 合同号
@if(null != CONT_NO){
    and g5.CONT_NO = #CONT_NO#
@}

mortgage_p
===
SELECT 
@pageTag(){
g5.CONT_NO,
g4.*

@}
FROM GRT_P_BASIC_INFO g4
left join GRT_GUARANTY_RE g6 on g4.GUARANTY_ID = g6.GUARANTY_ID
left join GRT_LOANGUAR_INFO g5 on g6.GUAR_CONT_NO = g5.GUAR_CONT_NO
where 
--法人机构号（惠州农商银行0801）
g5.CREUNIT_NO != '10086'
-- 合同号
@if(null != CONT_NO){
    and g5.CONT_NO = #CONT_NO#
@}

mortgage_er
===
SELECT 
@pageTag(){
g5.CONT_NO,
g2.*

@}
FROM GRT_GUARANTEER g2
left join GRT_GUARANTY_RE g6 on g2.GUARANTY_ID = g6.GUARANTY_ID
left join GRT_LOANGUAR_INFO g5 on g6.GUAR_CONT_NO = g5.GUAR_CONT_NO
where 
--法人机构号（惠州农商银行0801）
g5.CREUNIT_NO != '10086'
-- 合同号
@if(null != CONT_NO){
    and g5.CONT_NO = #CONT_NO#
@}


app_userstatus_1
===
select count(1) as IS_HOMEBANK
from t_global_permission_center 
where uid = #uid# and type = 'DATA_SEARCH_CONDITION'
union all
SELECT count(1) as IS_LOANBANK
FROM T_LOAN_ORG 
WHERE MAIN_BR_ID_OLD in (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#)
union all
SELECT count(1) as IS_LOANBANK_M
FROM T_LOAN_ORG 
WHERE MAIN_BR_ID_OLD in (select substr(acc_code,1,5) from T_ORG where PARENT_ID in (select ID from T_ORG where acc_code in (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)) and TYPE = 'DEPARTMENT')

app_userstatus_2
===
select distinct PARENT_ID,MAIN_BR_NAME_NEW_LVE_ONE 
from T_LOAN_ORG 
where MAIN_BR_ID_OLD in 
    (select substr(acc_code,1,5) from T_ORG 
    where PARENT_ID in 
        (select ID from T_ORG 
        where acc_code in 
            (SELECT MAIN_BR_ID FROM T_DEPARTMENT_USER WHERE UID=#uid#)
        ) 
        and TYPE = 'DEPARTMENT'
    ) 
    or (MAIN_BR_ID_OLD in 
        (SELECT substr(MAIN_BR_ID,1,5) FROM T_DEPARTMENT_USER WHERE UID=#uid#)
    )


app_cla
===
with pp as
(select LOAN_BALANCE,d1.v_value as CLA
FROM RPT_M_RPT_SLS_ACCT p1
left join t_dict d1 on d1.name = 'CLA' and d1.V_KEY = p1.CLA
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#globalUse("accloan.condition_loan")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)
select 
pp.CLA,
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
count(1) as LOAN_NUM
from
pp
group by pp.CLA


app_cus_1
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
--    DB2INST1.func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    d1.v_value as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u1.PHONE) = 11 and u1.PHONE like '1%') then
            u1.phone
        when (u1.CUS_TYPE like '1%' and length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
            u3.MOBILE
        when (u1.CUS_TYPE like '1%' and length(u3.PHONE) = 11 and u3.PHONE like '1%') then
            u3.PHONE
        when (u1.CUS_TYPE like '1%' and length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
            u3.FPHONE
        when (u1.CUS_TYPE like '2%' and length(u2.PHONE) = 11 and u2.PHONE like '1%') then
            u2.PHONE
        when (u1.CUS_TYPE like '2%' and length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
            u2.LEGAL_PHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u1.CONTACT_NAME)) != 0) then
            u1.CONTACT_NAME
        when (u1.CUS_TYPE like '2%' and length(trim(u2.LEGAL_NAME)) != 0) then
            u2.LEGAL_NAME
        when (u1.CUS_TYPE like '2%' and length(trim(u2.COM_OPERATOR)) != 0) then
            u2.COM_OPERATOR
        when (u1.CUS_TYPE like '1%' and length(trim(u3.CUS_NAME)) != 0) then
            u3.CUS_NAME
        when (u1.CUS_TYPE like '1%' and length(trim(u3.INDIV_COM_CNT_NAME)) != 0) then
            u3.INDIV_COM_CNT_NAME
        else
            ''
    end as CONTACT_NAME,
    case
        when (u1.CUS_TYPE like '1%') then
            u3.POST_ADDR   
        else
            u2.POST_ADDR
    end as POST_ADDR
@}
from
CUS_BASE as u1
left join CUS_COM as u2 on u1.CUS_ID=u2.CUS_ID
left join CUS_INDIV as u3 on u1.CUS_ID = u3.CUS_ID
left join t_dict d1 on d1.name = 'CERT_TYPE' and d1.V_KEY = u1.CERT_TYPE
where u1.CREUNIT_NO != '10086'
--查询数据范围
#use("condition_cus")#

-- 客户名称/联系人
@if(isNotEmpty(CUS_NAME)){
    and ((u1.CUS_NAME like #'%' + CUS_NAME + '%'#)
        or (u1.CONTACT_NAME like #'%' + CUS_NAME + '%'# 
               or u2.LEGAL_NAME like #'%' + CUS_NAME + '%'# 
               or u2.COM_OPERATOR like #'%' + CUS_NAME + '%'# 
               or u3.CUS_NAME like #'%' + CUS_NAME + '%'# 
               or u3.INDIV_COM_CNT_NAME like #'%' + CUS_NAME + '%'# )
    )
@}

app_cus_2
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
--    DB2INST1.func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    d1.v_value as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u2.PHONE) = 11 and u2.PHONE like '1%') then
            u2.PHONE
        when (length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
            u2.LEGAL_PHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u2.LEGAL_NAME)) != 0) then
            u2.LEGAL_NAME
        when (length(trim(u2.COM_OPERATOR)) != 0) then
            u2.COM_OPERATOR
        else
            ''
    end as CONTACT_NAME,
    u2.POST_ADDR
@}
from
CUS_COM as u2
left join CUS_BASE as u1 on u2.CUS_ID = u1.CUS_ID
left join CUS_INDIV as u3 on u2.CUS_ID = u3.CUS_ID
left join t_dict d1 on d1.name = 'CERT_TYPE' and d1.V_KEY = u1.CERT_TYPE
where u1.CREUNIT_NO != '10086'
--查询数据范围
#use("condition_cus")#

-- 客户名称/联系人
@if(isNotEmpty(CUS_NAME)){
    and ((u1.CUS_NAME like #'%' + CUS_NAME + '%'#)
        or (u1.CONTACT_NAME like #'%' + CUS_NAME + '%'# 
               or u2.LEGAL_NAME like #'%' + CUS_NAME + '%'# 
               or u2.COM_OPERATOR like #'%' + CUS_NAME + '%'#)
    )
@}

app_cus_3
===
select
@pageTag(){
    u1.CUS_ID,
    u1.CUS_NAME,
--    DB2INST1.func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    d1.v_value as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u3.MOBILE) = 11 and u3.MOBILE like '1%') then
            u3.MOBILE
        when (length(u3.PHONE) = 11 and u3.PHONE like '1%') then
            u3.PHONE
        when (length(u3.FPHONE) = 11 and u3.FPHONE like '1%') then
            u3.FPHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u3.CUS_NAME)) != 0) then
            u3.CUS_NAME
        when (length(trim(u3.INDIV_COM_CNT_NAME)) != 0) then
            u3.INDIV_COM_CNT_NAME
        else
            ''
    end as CONTACT_NAME,
    u3.POST_ADDR
@}
from CUS_INDIV as u3
left join CUS_BASE as u1 on u3.CUS_ID=u1.CUS_ID
left join CUS_COM as u2 on u3.CUS_ID = u2.CUS_ID
left join t_dict d1 on d1.name = 'CERT_TYPE' and d1.V_KEY = u1.CERT_TYPE
where u1.CREUNIT_NO != '10086'
--查询数据范围
#use("condition_cus")#

-- 客户名称/联系人
@if(isNotEmpty(CUS_NAME)){
    and ((u1.CUS_NAME like #'%' + CUS_NAME + '%'#)
        or (u1.CONTACT_NAME like #'%' + CUS_NAME + '%'# 
            or u3.CUS_NAME like #'%' + CUS_NAME + '%'# 
            or u3.INDIV_COM_CNT_NAME like #'%' + CUS_NAME + '%'# )
    )
@}

app_cus_4
===
select
@pageTag(){
    p1.CUS_ID,
    p1.CUS_NAME,
--    DB2INST1.func_get_dict('CERT_TYPE',u1.CERT_TYPE) as CERT_TYPE,
    d1.v_value as CERT_TYPE,
    u1.CERT_CODE,
    case
        when (length(u2.PHONE) = 11 and u2.PHONE like '1%') then
            u2.PHONE
        when (length(u2.LEGAL_PHONE) = 11 and u2.LEGAL_PHONE like '1%') then
            u2.LEGAL_PHONE
        else
            ''
    end as PHONE,
    case
        when (length(trim(u2.LEGAL_NAME)) != 0) then
            u2.LEGAL_NAME
        when (length(trim(u2.COM_OPERATOR)) != 0) then
            u2.COM_OPERATOR
        else
            ''
    end as CONTACT_NAME,
    u2.POST_ADDR
@}
from
(select * from(select a.*,row_number() over(partition by CUS_NAME order by CUS_NAME) rn from (select p2.* from RPT_M_RPT_SLS_ACCT p2 where p2.ACCOUNT_STATUS in ('1','6') and p2.GL_CLASS not like '0%'and CUST_TYPE like '2%') a) b where rn =1) as p1
left join CUS_BASE as u1 on p1.CUS_ID = u1.CUS_ID
left join CUS_COM as u2 on p1.CUS_ID = u2.CUS_ID
left join CUS_INDIV as u3 on p1.CUS_ID = u3.CUS_ID
left join t_dict d1 on d1.name = 'CERT_TYPE' and d1.V_KEY = u1.CERT_TYPE
where p1.CREUNIT_NO != '10086'
--查询数据范围
#use("condition_cus")#

-- 客户名称/联系人
@if(isNotEmpty(CUS_NAME)){
    and ((u1.CUS_NAME like #'%' + CUS_NAME + '%'#)
        or (u1.CONTACT_NAME like #'%' + CUS_NAME + '%'# 
               or u2.LEGAL_NAME like #'%' + CUS_NAME + '%'# 
               or u2.COM_OPERATOR like #'%' + CUS_NAME + '%'#)
    )
@}



app_loan_1
===
select
@pageTag(){
    p1.LOAN_ACCOUNT,
    p1.CUS_NAME,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    d1.v_value as CLA,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.CAPINT_OVERDUE_DATE,
    p1.ASSURE_MEANS_MAIN
@}
from RPT_M_RPT_SLS_ACCT as p1
left join t_dict d1 on d1.name = 'CLA' and d1.V_KEY = p1.CLA
where p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and p1.CUS_NAME like #'%' + CUS_NAME + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

--贷款五级类型：正常贷款
@if(isNotEmpty(LOAN_CLA_TYPE_01)){
    and p1.CLA in ('10','20')
@}
--贷款五级类型：逾期贷款
@if(isNotEmpty(LOAN_CLA_TYPE_02)){
    and ((p1.DELAY_INT_CUMU > 0) or (p1.UNPD_PRIN_BAL > 0))
@}
--贷款五级类型：隐性不良贷款
@if(isNotEmpty(LOAN_CLA_TYPE_03)){
    and ((p1.DELAY_INT_CUMU > 0) or (p1.UNPD_PRIN_BAL > 0))
    and p1.CLA in ('10','20') 
    and p1.CAPINT_OVERDUE_DATE<>''
    and DAYS(to_date(#LOAN_CLA_TYPE_03#,'yyyyMMdd')) - DAYS(to_date(p1.CAPINT_OVERDUE_DATE,'yyyyMMdd'))>=90
@}
--贷款五级类型：不良贷款
@if(isNotEmpty(LOAN_CLA_TYPE_04)){
    and p1.CLA in ('30','40','50')
@}

--前十大户
@if(isNotEmpty(GET_FLAG_01)){
    order by p1.LOAN_BALANCE DESC
    fetch first 10 rows only
@}

--本月新增(区分正常/逾期/隐性不良/不良)
@if(isNotEmpty(GET_FLAG_02)){
    @if(isNotEmpty(LOAN_CLA_TYPE_01)){
        and p1.LOAN_START_DATE>#START_DATE#
        and p1.LOAN_START_DATE<#END_DATE#
    @}
    @if(isNotEmpty(LOAN_CLA_TYPE_02)){
        and p1.CAPINT_OVERDUE_DATE<>''
        and p1.CAPINT_OVERDUE_DATE>#START_DATE#
        and p1.CAPINT_OVERDUE_DATE<#END_DATE#
    @}
    @if(isNotEmpty(LOAN_CLA_TYPE_03)){
        and p1.CAPINT_OVERDUE_DATE<>''
        and DAYS(to_date(#LOAN_CLA_TYPE_03#,'yyyyMMdd')) - DAYS(to_date(p1.CAPINT_OVERDUE_DATE,'yyyyMMdd'))>=90
        and DAYS(to_date(#LOAN_CLA_TYPE_03#,'yyyyMMdd')) - DAYS(to_date(p1.CAPINT_OVERDUE_DATE,'yyyyMMdd'))<=120
    @}
    
@}

--本月到期
@if(isNotEmpty(GET_FLAG_03)){
    and p1.LOAN_END_DATE>#START_DATE#
    and p1.LOAN_END_DATE<#END_DATE#
@}

--本月新增催收
@if(isNotEmpty(GET_FLAG_04)){
    and p1.LOAN_ACCOUNT in 
        (select LOAN_ACCOUNT from T_WORKFLOW_INSTANCE where MODEL_NAME = '催收' 
            and ADD_TIME>to_date(#START_DATE#,'yyyyMMdd') 
            and ADD_TIME<to_date(#END_DATE#,'yyyyMMdd') )
@}

--本月新增诉讼
@if(isNotEmpty(GET_FLAG_05)){
    and p1.LOAN_ACCOUNT in 
        (select LOAN_ACCOUNT from T_WORKFLOW_INSTANCE where MODEL_NAME = '诉讼' 
            and ADD_TIME>to_date(#START_DATE#,'yyyyMMdd') 
            and ADD_TIME<to_date(#END_DATE#,'yyyyMMdd') )
@}

--本月新增利息减免
@if(isNotEmpty(GET_FLAG_06)){
    and p1.LOAN_ACCOUNT in 
        (select LOAN_ACCOUNT from T_WORKFLOW_INSTANCE where MODEL_NAME = '利息减免' 
            and ADD_TIME>to_date(#START_DATE#,'yyyyMMdd') 
            and ADD_TIME<to_date(#END_DATE#,'yyyyMMdd') )
@}

--本月新增抵债资产接收
@if(isNotEmpty(GET_FLAG_07)){
    and p1.LOAN_ACCOUNT in 
        (select LOAN_ACCOUNT from T_WORKFLOW_INSTANCE where MODEL_NAME = '抵债资产接收' 
            and ADD_TIME>to_date(#START_DATE#,'yyyyMMdd') 
            and ADD_TIME<to_date(#END_DATE#,'yyyyMMdd') )
@}

--本月新增资产处置
@if(isNotEmpty(GET_FLAG_08)){
    and p1.LOAN_ACCOUNT in 
        (select LOAN_ACCOUNT from T_WORKFLOW_INSTANCE where MODEL_NAME = '资产处置' 
            and ADD_TIME>to_date(#START_DATE#,'yyyyMMdd') 
            and ADD_TIME<to_date(#END_DATE#,'yyyyMMdd') )
@}


app_loan_2
===
SELECT 
@pageTag(){
    p1.LOAN_ACCOUNT,
    p1.CUS_NAME,
    p1.LOAN_AMOUNT,
    p1.LOAN_BALANCE,
    d1.v_value as CLA,
    p1.LOAN_START_DATE,
    p1.LOAN_END_DATE,
    p1.CAPINT_OVERDUE_DATE,
    p1.ASSURE_MEANS_MAIN  
@}
FROM RSC_TASK_INFO_HIS r1
left join RPT_M_RPT_SLS_ACCT as p1 on r1.BILL_NO = p1.BILL_NO
left join t_dict d1 on d1.name = 'CLA' and d1.V_KEY = p1.CLA
where p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

--贷款五级类型：不良贷款
@if(isNotEmpty(LOAN_CLA_TYPE_04)){
    and p1.CLA in ('30','40','50')
@}

--原七级分类结果(为正常类、关注类)
and r1.CLA_RESULT_PRE < '30'
--七级分类认定结果
and r1.CLA_RESULT>= '30'
--开始时间
@if(isNotEmpty(START_DATE)){
    and r1.CLA_DATE>#START_DATE#
@}
--结束时间
@if(isNotEmpty(END_DATE)){
    and r1.CLA_DATE<#END_DATE#
@}

app_loan_class
===
with pp as
(select LOAN_BALANCE,LOAN_ACCOUNT,CLA,DELAY_INT_CUMU,UNPD_PRIN_BAL,CAPINT_OVERDUE_DATE
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)

select 
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(pp1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_ZC,
coalesce(sum(pp2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_ZCYQ,
coalesce(sum(pp3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_YXBL,
coalesce(sum(pp4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_BL
from
pp
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20')) 
    as pp1 on pp.LOAN_ACCOUNT=pp1.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20') 
    and ((DELAY_INT_CUMU > 0) or (UNPD_PRIN_BAL > 0))) 
    as pp2 on pp.LOAN_ACCOUNT=pp2.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20') 
    and ((DELAY_INT_CUMU > 0) or (UNPD_PRIN_BAL > 0))
    and CAPINT_OVERDUE_DATE<>''
    and DAYS(to_date(#LOAN_CLA_TYPE_03#,'yyyyMMdd')) - DAYS(to_date(CAPINT_OVERDUE_DATE,'yyyyMMdd'))>=90
    ) 
    as pp3 on pp.LOAN_ACCOUNT=pp3.LOAN_ACCOUNT
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('30','40','50')) 
    as pp4 on pp.LOAN_ACCOUNT=pp4.LOAN_ACCOUNT
    

app_loan_class_zc
===
with pp as
(select LOAN_BALANCE,LOAN_ACCOUNT,CLA,DELAY_INT_CUMU,UNPD_PRIN_BAL,CAPINT_OVERDUE_DATE
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)

select 
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(pp1.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_COUNT
from
pp
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20')) 
    as pp1 on pp.LOAN_ACCOUNT=pp1.LOAN_ACCOUNT

    
    
app_loan_class_zcyq
===
with pp as
(select LOAN_BALANCE,LOAN_ACCOUNT,CLA,DELAY_INT_CUMU,UNPD_PRIN_BAL,CAPINT_OVERDUE_DATE
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)

select 
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(pp2.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_COUNT
from
pp
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20') 
    and ((DELAY_INT_CUMU > 0) or (UNPD_PRIN_BAL > 0))) 
    as pp2 on pp.LOAN_ACCOUNT=pp2.LOAN_ACCOUNT
    
    
app_loan_class_yxbl
===
with pp as
(select LOAN_BALANCE,LOAN_ACCOUNT,CLA,DELAY_INT_CUMU,UNPD_PRIN_BAL,CAPINT_OVERDUE_DATE
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)

select 
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(pp3.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_COUNT
from
pp
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('10','20') 
    and ((DELAY_INT_CUMU > 0) or (UNPD_PRIN_BAL > 0))
    and CAPINT_OVERDUE_DATE<>''
    and DAYS(to_date(#LOAN_CLA_TYPE_03#,'yyyyMMdd')) - DAYS(to_date(CAPINT_OVERDUE_DATE,'yyyyMMdd'))>=90
    ) 
    as pp3 on pp.LOAN_ACCOUNT=pp3.LOAN_ACCOUNT

    
app_loan_class_bl
===
with pp as
(select LOAN_BALANCE,LOAN_ACCOUNT,CLA,DELAY_INT_CUMU,UNPD_PRIN_BAL,CAPINT_OVERDUE_DATE
FROM RPT_M_RPT_SLS_ACCT p1
where
--法人机构号（惠州农商银行0801
p1.CREUNIT_NO != '10086'
--普通贷款
and p1.LN_TYPE in ('普通贷款','银团贷款')
--表内资产
and p1.GL_CLASS not like '0%'
--查询数据范围
#use("condition")#

-- 贷款类型
@if(isNotEmpty(LOAN_TYPE_C)){
    and p1.LOAN_ACCOUNT like #LOAN_TYPE_C + '%'#
@}

-- 一级支行名称
@if(isNotEmpty(MAIN_PARENT_NAME)){
    and p1.MAIN_BR_ID in (select MAIN_BR_ID_OLD from T_LOAN_ORG where MAIN_BR_NAME_NEW_LVE_ONE = #MAIN_PARENT_NAME#)
@}

)

select 
coalesce(sum(pp.LOAN_BALANCE)/10000,0) as LOAN_BALANCE,
coalesce(sum(pp4.LOAN_BALANCE)/10000,0) as LOAN_BALANCE_COUNT
from
pp
left join (select LOAN_BALANCE,LOAN_ACCOUNT from pp where CLA in ('30','40','50')) 
    as pp4 on pp.LOAN_ACCOUNT=pp4.LOAN_ACCOUNT




acc_guanlian_count
===
select
count(1) as count_number
from
	t_related_party_list 
where 
LINK_RULE between 1.1 and 3.0
@if(isNotEmpty(CUS_NAME)){
    and RELATED_NAME =  #CUS_NAME#
@}
@if(isNotEmpty(CERT_CODE)){
    and CERT_CODE =  #CERT_CODE#
@}




app_guanlian_list
===
select
@pageTag(){
search.add_time add_time,
search.cus_name cus_name,
search.cert_code cert_code,
search.operator operator,
search.MAIN_BR_ID main_by_id,
org.name oname ,
user.TRUE_NAME uname
@}
from T_LOAN_RELATED_SEARCH  as search
left  join t_org org on MAIN_BR_ID = org.id 
 left join t_user user on operator = user.id where 
 ('admin' = (select username from t_user where id = #uid#)or operator = #uid#)
-- 证件号码
@if(isNotEmpty(CERT_CODE)){
    and CERT_CODE like #'%' + CERT_CODE + '%'#
@}
-- 客户名称
@if(isNotEmpty(CUS_NAME)){
    and CUS_NAME like #'%' + CUS_NAME + '%'#
@}







uid_oname_search
===
select tab.uname uname,tab.uid uid,t.name oname,t.ACC_CODE from t_org t full join 
(select org.id oid,org.name oname,org.parent_id opid,u.id uid,u.true_name  uname from t_user as u full 
   join t_user_org as uo on u.id = uo.uid  full join t_org as
 org on uo.oid = org.id where org.type = 'QUARTERS')  tab on t.id = tab.opid
