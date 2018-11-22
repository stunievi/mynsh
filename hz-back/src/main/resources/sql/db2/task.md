deleteTriggerLogs
===
delete from t_notice_trigger_log

deleteNotices
===
delete from t_system_notice


selectRule1UL
===
select 
		    p1.LOAN_ACCOUNT,
               p1.LOAN_ACCOUNT as 贷款帐号,
               p1.LOAN_AMOUNT as 贷款金额,
               p1.LOAN_BALANCE as 贷款余额,
               p1.CUS_ID,
               p1.CUS_NAME as 客户名称,
               tt.PHONE
		from 
			RPT_M_RPT_SLS_ACCT as p1 
			inner join t_loan_manager tt on p1.LOAN_ACCOUNT = tt.LOAN_ACCOUNT and tt.phone is not null and tt.phone <> ''
			left JOIN t_notice_trigger_log tl ON tl.RULE_NAME = 'rule1' AND tl.UUID = p1.LOAN_ACCOUNT AND VARCHAR_FORMAT(
				CURRENT TIMESTAMP,
				'YYYYMM'
			) = VARCHAR_FORMAT(
				tl.TRIGGER_TIME,
				'YYYYMM'
			)
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			and tl.id is null
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--表内资产
			and p1.GL_CLASS not like '0%'
			--台帐状态
			and p1.ACCOUNT_STATUS = '1'
			--触发时间(每月xxx日）
			and DAY(current timestamp) = coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_1'),0)
			and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_1_ON'),'off'))
			
			
selectRule2UL
===
select 
			p1.LOAN_ACCOUNT,
               p1.LOAN_ACCOUNT as 贷款帐号,
               p1.LOAN_AMOUNT as 贷款金额,
               p1.LOAN_BALANCE as 贷款余额,
               p1.CUS_ID,
               p1.CUS_NAME as 客户名称,
               tt.PHONE,
               log.RULE_NAME,
               log.TRIGGER_TIME
		from 
			RPT_M_RPT_SLS_ACCT as p1 
			inner join t_loan_manager tt on p1.LOAN_ACCOUNT = tt.LOAN_ACCOUNT and tt.PHONE is not null and tt.PHONE <>''
			left join acc_loan as a1 on p1.LOAN_ACCOUNT = a1.LOAN_ACCOUNT
			left join (select UUID,RULE_NAME,max(TRIGGER_TIME) as TRIGGER_TIME from T_NOTICE_TRIGGER_LOG group by UUID,RULE_NAME) log  on log.UUID = p1.LOAN_ACCOUNT and log.RULE_NAME = 'rule2'
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--表内资产
			and p1.GL_CLASS not like '0%'
			--台帐状态
			and p1.ACCOUNT_STATUS = '1' 
			--还款方式
			and p1.REPAYMENT_MODE<>'101'
			--没有日志
			--and log.UUID is null
			--还款日距离当前时间<I_AHEAD_DAY	
			and (a1.RETURN_DATE<>'') 
			and Integer(trim(a1.RETURN_DATE))-DAY(current timestamp)>0
			and Integer(trim(a1.RETURN_DATE))-DAY(current timestamp)<coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_2'),0)
			--没有对应的记录或者记录的TRIGGER_TIME距离当前时间超过I_AHEAD_DAY天
			and (log.TRIGGER_TIME is null or (days(current timestamp)-days(log.TRIGGER_TIME)>coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_2'),0)))
            and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_2_ON'),'off'))



selectRule3UL
===
select 
			p1.CUST_MGR,
               tt.id as UID,
               p1.CUS_NAME as 客户名称,
               p1.LOAN_ACCOUNT,
               p1.LOAN_ACCOUNT as 贷款帐号,
               p1.LOAN_AMOUNT as 贷款金额,
               p1.LOAN_BALANCE as 贷款余额,
               b1.TOT_INT as 实收利息,
               b1.PRIN_AMT as 实收本金,
               b1.TRN_DATE as 实收日期,
               log.UUID
               ,b1.SRC_SYS_DATE
               ,b1.ACCT_NO
               ,b1.REC_NO
		from 
			(select substr(LOAN_ACCOUNT,1,16) as LOAN_ACCOUNT_16,temp1.* from RPT_M_RPT_SLS_ACCT temp1) p1
			left join BOCT_88 as b1 on p1.LOAN_ACCOUNT_16 = b1.ACCT_NO
			left join T_USER as tt on p1.cust_mgr = tt.acc_code
			left join T_NOTICE_TRIGGER_LOG log  on log.uuid = b1.ACCT_NO and log.uuid2 = b1.REC_NO and log.RULE_NAME = 'rule3'
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--台帐状态
			and p1.ACCOUNT_STATUS='1'
			--表内资产
            and p1.GL_CLASS not like '0%'
			--有还款记录
			and p1.LOAN_ACCOUNT_16 in (select ACCT_NO from BOCT_88 where CREUNIT_NO='0801')
			--只针对7天内还款记录进行通知
			and (b1.SRC_SYS_DATE <>''  and (days(current timestamp)-days(to_date(b1.SRC_SYS_DATE,'yyyymmdd'))<7))
			--检查表没有对应的记录
			and (log.id is null)
            and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_3_ON'),'off'))


selectRule4-1UL
===
select
			p1.CUST_MGR,
			tt.id,
			tu.id as uid,
			p1.MAIN_BR_ID,
			p1.LOAN_ACCOUNT,
			p1.LOAN_ACCOUNT as 贷款帐号,
			p1.LOAN_AMOUNT as 贷款金额,
			p1.LOAN_BALANCE as 贷款余额,
			p1.CUS_ID,
			p1.CUS_NAME as 客户名称,
			p1.LOAN_END_DATE 到期日,
			tt.PHONE,
			log.RULE_NAME,
			log.TRIGGER_TIME		
		from 
			RPT_M_RPT_SLS_ACCT as p1 
			left join t_loan_manager tt on p1.LOAN_ACCOUNT = tt.LOAN_ACCOUNT and tt.PHONE is not null and tt.PHONE <>''
			inner join T_USER as tu on p1.CUST_MGR = tu.acc_code
			left join acc_loan as a1 on p1.LOAN_ACCOUNT = a1.LOAN_ACCOUNT
			left join (select UUID,RULE_NAME,max(TRIGGER_TIME) as TRIGGER_TIME from T_NOTICE_TRIGGER_LOG group by UUID,RULE_NAME) log  on log.UUID = p1.LOAN_ACCOUNT and log.RULE_NAME = 'rule4'
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--表内资产
			and p1.GL_CLASS not like '0%'
			--台帐状态
			and p1.ACCOUNT_STATUS='1'
			--贷款到期日期-当前日期<I_AHEAD_DAY天
			and (p1.LOAN_END_DATE<>'' and days(to_date(p1.LOAN_END_DATE,'yyyymmdd'))-days(current timestamp) between 0 and coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_4'),0))
			--没有对应的记录或者记录的TRIGGER_TIME距离当前时间超过I_AHEAD_DAY天*/
			and (log.uuid is null or (days(current timestamp)-days(log.TRIGGER_TIME)>coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_4'),0)))
            and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_4_ON'),'off'))

selectRule4-2UL
===
select
   p1.CUST_MGR,
   DM.uid as mid,
   p1.MAIN_BR_ID,
   p1.LOAN_ACCOUNT,
   p1.LOAN_ACCOUNT as 贷款帐号,
   p1.LOAN_AMOUNT as 贷款金额,
   p1.LOAN_BALANCE as 贷款余额,
   p1.CUS_ID,
   p1.CUS_NAME as 客户名称,
   p1.LOAN_END_DATE 到期日,
   log.RULE_NAME,
   log.TRIGGER_TIME  
  from 
   RPT_M_RPT_SLS_ACCT as p1 
   inner join T_DEPARTMENT_MANAGER as DM on p1.MAIN_BR_ID = DM.acc_code
   left join acc_loan as a1 on p1.LOAN_ACCOUNT = a1.LOAN_ACCOUNT
   left join (select UUID,RULE_NAME,max(TRIGGER_TIME) as TRIGGER_TIME from T_NOTICE_TRIGGER_LOG group by UUID,RULE_NAME) log  on log.UUID = p1.LOAN_ACCOUNT and log.RULE_NAME = 'rule4'
  where 
   --法人机构号（惠州农商银行0801）
   p1.CREUNIT_NO = '0801'
   --贷款分类
   and p1.LN_TYPE = '普通贷款'
   --表内资产
   and p1.GL_CLASS not like '0%'
   --台帐状态
   and p1.ACCOUNT_STATUS='1'
   --贷款到期日期-当前日期<I_AHEAD_DAY天
   and (p1.LOAN_END_DATE<>'' and days(to_date(p1.LOAN_END_DATE,'yyyymmdd'))-days(current timestamp) between 0 and coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_4'),0))
   --没有对应的记录或者记录的TRIGGER_TIME距离当前时间超过I_AHEAD_DAY天*/
   and (log.UUID is null or (days(current timestamp)-days(log.TRIGGER_TIME)>coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_4'),0)))
			


selectRule5-1UL
===
select
			p1.CUST_MGR,
			tu.id as uid,
			p1.MAIN_BR_ID,
			p1.LOAN_ACCOUNT,
			p1.LOAN_ACCOUNT as 贷款帐号,
			p1.LOAN_AMOUNT as 贷款金额,
			p1.LOAN_BALANCE as 贷款余额,
			p1.CUS_ID,
			p1.CUS_NAME as 客户名称,
			p1.CAPINT_OVERDUE_DATE as 本息逾期日,
			p1.CAP_OVERDUE_DATE as 本金逾期起始日,
			p1.INTEREST_OVERDUE_DATE as 利息逾期起始日,
			p1.DELAY_INT_CUMU as 拖欠本金,
			p1.UNPD_PRIN_BAL 欠息累积,
			tt.PHONE,
			log.RULE_NAME,
			log.TRIGGER_TIME
		from 
			RPT_M_RPT_SLS_ACCT as p1 
			left join t_loan_manager tt on p1.LOAN_ACCOUNT = tt.LOAN_ACCOUNT and tt.PHONE is not null and tt.PHONE <>''
			inner join T_USER as tu on p1.CUST_MGR = tu.acc_code
			left join (select UUID,RULE_NAME,max(TRIGGER_TIME) as TRIGGER_TIME from T_NOTICE_TRIGGER_LOG group by UUID,RULE_NAME) log  on log.UUID = p1.LOAN_ACCOUNT and log.RULE_NAME = 'rule5'
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--表内资产
			and p1.GL_CLASS not like '0%'
			--台帐状态
			and p1.ACCOUNT_STATUS='1'
			--累计欠息/拖欠本金>0
			and (p1.DELAY_INT_CUMU >0 or p1.UNPD_PRIN_BAL>0) 
			--拖欠本金起始日/利息逾期起始日在xxx以内
			and (
			    (p1.CAP_OVERDUE_DATE <>'' and (days(current timestamp)-days(to_date(p1.CAP_OVERDUE_DATE,'yyyymmdd'))<coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5'),0)))
			    or (p1.INTEREST_OVERDUE_DATE <>'' and days(current timestamp)-days(to_date(p1.INTEREST_OVERDUE_DATE,'yyyymmdd'))<coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5'),0))
			    )
			--没有对应的记录或者记录的TRIGGER_TIME距离当前时间超过xxx天
			and (log.UUID is null)
			and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5_ON'),'off'))
			
selectRule5-2UL			
===
select
			p1.CUST_MGR,
			DM.uid as mid,
			p1.MAIN_BR_ID,
			p1.LOAN_ACCOUNT,
			p1.LOAN_ACCOUNT as 贷款帐号,
			p1.LOAN_AMOUNT as 贷款金额,
			p1.LOAN_BALANCE as 贷款余额,
			p1.CUS_ID,
			p1.CUS_NAME as 客户名称,
			p1.CAPINT_OVERDUE_DATE as 本息逾期日,
			p1.CAP_OVERDUE_DATE as 本金逾期起始日,
			p1.INTEREST_OVERDUE_DATE as 利息逾期起始日,
			p1.DELAY_INT_CUMU as 拖欠本金,
			p1.UNPD_PRIN_BAL 欠息累积,
			log.RULE_NAME,
			log.TRIGGER_TIME
		from 
			RPT_M_RPT_SLS_ACCT as p1 
			inner join T_DEPARTMENT_MANAGER as DM on p1.MAIN_BR_ID = DM.acc_code
			left join (select UUID,RULE_NAME,max(TRIGGER_TIME) as TRIGGER_TIME from T_NOTICE_TRIGGER_LOG group by UUID,RULE_NAME) log  on log.UUID = p1.LOAN_ACCOUNT and log.RULE_NAME = 'rule5'
		where 
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--贷款分类
			and p1.LN_TYPE = '普通贷款'
			--表内资产
			and p1.GL_CLASS not like '0%'
			--台帐状态
			and p1.ACCOUNT_STATUS='1'
			--累计欠息/拖欠本金>0
			and (p1.DELAY_INT_CUMU >0 or p1.UNPD_PRIN_BAL>0) 
			--拖欠本金起始日/利息逾期起始日在xxx以内
			and (
			    (p1.CAP_OVERDUE_DATE <>'' and (days(current timestamp)-days(to_date(p1.CAP_OVERDUE_DATE,'yyyymmdd'))<coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5'),0)))
			    or (p1.INTEREST_OVERDUE_DATE <>'' and days(current timestamp)-days(to_date(p1.INTEREST_OVERDUE_DATE,'yyyymmdd'))<coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5'),0))
			    )
			--没有对应的记录或者记录的TRIGGER_TIME距离当前时间超过xxx天
			and (log.UUID is null)
			and UPPER('on') = UPPER(coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_5_ON'),'off'))
	
selectRule6UL
===
select 
dl.uid
, dl.id as DID
, dl.utname
, dm.uid as mid
, ins.title
, ins.id
, days(current timestamp) - days(dl.last_modify)  as EXPR_DAYS
from t_workflow_instance ins
left join t_department_manager dm on dm.ID = ins.dep_id
inner join t_wf_ins_dealer dl on dl.ins_id = ins.id and ins.current_node_instance_id = dl.node_id and dl.type in ('CAN_DEAL','DID_DEAL')
where ins.state = 'DEALING' and days(current timestamp) - days(dl.last_modify) > coalesce((select var_value from t_system_variable where var_name = 'MSG_RULE_6'),0)
and not exists(select 1 from t_notice_trigger_log where RULE_NAME = 'rule6' and UUID = dl.id)

	
selectRule7UL
===
SELECT
	ins.id ,
	ins.title,
	ins.dep_id,
	ins.DEAL_USER_ID,
    u.id as uid,
	u.USERNAME                                          AS uname,
	u.TRUE_NAME                                         AS true_name,
	days(CURRENT TIMESTAMP) - days(ins.plan_start_time) AS expr_days,
	(	SELECT
			LISTAGG(dm.uid,',') WITHIN GROUP(
		ORDER BY
			uid) 
	FROM
		t_department_manager dm 
	WHERE
		dm.id = ins.dep_id) AS managers 
	FROM
		t_workflow_instance ins 
        inner JOIN t_user u 
			ON u.id = ins.DEAL_USER_ID 
	WHERE
		ins.deal_user_id IS NOT NULL AND
		ins.state = 'DEALING' AND
		days(CURRENT TIMESTAMP) - days(ins.plan_start_time) > COALESCE((SELECT
																			var_value 
																		FROM
																			t_system_variable 
																		WHERE
																			var_name 
																			= 
																			'MSG_RULE_7'
		),0) AND
		(	SELECT
				COUNT(*) 
			FROM
				t_notice_trigger_log 
			WHERE
				rule_name = 'rule7' AND
				uuid = ins.id ) = 0

selectRule10UL
===
SELECT DISTINCT
	ins.*
FROM
	t_workflow_instance ins
WHERE
	ins.state = 'DEALING'
AND(
	SELECT
		count(1)
	FROM
		t_workflow_instance child
	WHERE
		child.parent_id = ins.id
	AND (
	    child.model_name in ('催收','诉讼') 
	    --or (
	    --    child.model_name = '诉讼' and (select count(1) from t_workflow_node_instance ni where ni.instance_id = child.id and ni.node_name = '法院立案' and ins.current_node_model_name <> '法院立案' ) > 0  
        --)
    )
) = 0
AND CURRENT TIMESTAMP > ins.plan_start_time
AND CURRENT TIMESTAMP BETWEEN (plan_start_time + #day0# days - #day1# day)
AND plan_start_time + #day0# days
AND ins.model_name = '不良资产管理流程'
AND(
	SELECT
		count(*)
	FROM
		t_user_token ut
	WHERE
		ut.user_id = ins.deal_user_id
	AND CURRENT TIMESTAMP < ut.expr_time
) > 0
AND(
	SELECT
		count(*)
	FROM
		t_notice_trigger_log tl
	WHERE
		tl.rule_name = 'rule10'
	AND tl.uuid = ins.id
	AND CURRENT TIMESTAMP < tl.trigger_time + 30 MINUTES
) = 0


selectRule11UL
===
SELECT
	* 
FROM
	(	SELECT
			ins.id   AS id ,
            ins.title ,
			(	SELECT
					user_id 
				FROM
					t_workflow_node_instance_dealer 
				WHERE
					node_instance_id = ins.current_node_instance_id 
					AND
					TYPE IN ('DID_DEAL' ,
					'OVER_DEAL') )       AS deal_user_id ,
			to_date((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_start' 
						AND
						attr.NODE_ID = ins.current_node_instance_id),'YYYYMMDD') AS stime ,
			to_date((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_end' 
						AND
						attr.NODE_ID = ins.current_node_instance_id),'YYYYMMDD') AS etime 
		FROM
			t_workflow_instance ins 
			LEFT JOIN t_workflow_node_instance ni 
			ON ni.id = ins.current_node_instance_id
			--- LEFT JOIN t_workflow_node_attribute attr ON ins.current_node_instance_id = attr.node_id
		WHERE
			ins.state = 'DEALING' 
			AND
			ins.model_name = '诉讼' 
			AND
			ni.node_name = '诉前保全'
			--- AND attr.attr_key IN('dt_start' , 'dt_end')
			AND
			(	SELECT
					COUNT(*) 
				FROM
					t_user_token ut 
				WHERE
					ut.user_id = ins.deal_user_id 
					AND
					CURRENT TIMESTAMP < ut.expr_time ) > 0 
			AND
			(	SELECT
					COUNT(*) 
				FROM
					t_notice_trigger_log tl 
				WHERE
					tl.rule_name = 'rule11' 
					AND
					tl.uuid = ins.id 
					AND
					CURRENT TIMESTAMP < tl.trigger_time + 30 MINUTES ) = 0 ) AS sub 
GROUP BY
	id ,
	title ,
	deal_user_id ,
	stime ,
	etime 
HAVING
	CURRENT TIMESTAMP BETWEEN MAX(stime,etime - #day# days) 
	AND
	etime

selectRule12UL
===
SELECT
	* 
FROM
	(	SELECT
			ins.id   AS id ,
			ins.title,
			to_date((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_1' AND
						attr.NODE_ID = ins.current_node_instance_id),'YYYYMMDD') AS stime ,
			(	SELECT
					user_id 
				FROM
					t_workflow_node_instance_dealer 
				WHERE
					node_instance_id = ins.current_node_instance_id AND
					TYPE IN ('DID_DEAL',
					'OVER_DEAL') )       AS deal_user_id 
		FROM
			t_workflow_instance ins 
			LEFT JOIN t_workflow_node_instance ni 
			ON ni.id = ins.current_node_instance_id 
		WHERE
			ins.state = 'DEALING' AND
			ins.model_name = '诉讼' AND
			ni.node_name = '案件跟进' 
			
			) AS sub 
GROUP BY
	id,
	title,
	deal_user_id,
	stime 
HAVING
	CURRENT TIMESTAMP BETWEEN stime - #day# days AND
	stime AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = sub.deal_user_id AND
			CURRENT TIMESTAMP < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule12' AND
			tl.uuid = sub.id AND
			CURRENT TIMESTAMP < tl.trigger_time + 30 minutes ) = 0 

selectRule13UL
===
SELECT
	* 
FROM
	(	SELECT
			ins.id   AS id ,
			(	SELECT
					user_id 
				FROM
					t_workflow_node_instance_dealer 
				WHERE
					node_instance_id = ins.current_node_instance_id 
					AND
					TYPE IN( 'DID_DEAL',
					'OVER_DEAL') )       AS deal_user_id,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_2' 
						AND
						attr.NODE_ID = ins.current_node_instance_id)) AS stime 
		FROM
			t_workflow_instance ins 
			LEFT JOIN t_workflow_node_instance ni 
			ON ni.id = ins.current_node_instance_id
			--LEFT JOIN t_workflow_instance_attribute attr ON attr.instance_id = ins.id
		WHERE
			ins.state = 'DEALING' 
			AND
			ins.model_name = '诉讼' 
			AND
			ni.node_name = '判决生效，执行'
			--AND attr.attr_key IN('dt_2')
	) AS sub 
GROUP BY
	id,
	deal_user_id,
	stime 
HAVING
	CURRENT TIMESTAMP BETWEEN stime - #day# days 
	AND
	stime 
	AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = sub.deal_user_id 
			AND
			CURRENT TIMESTAMP < ut.expr_time ) > 0 
	AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule13' 
			AND
			tl.uuid = sub.id 
			AND
			CURRENT TIMESTAMP < tl.trigger_time + 30 minutes ) = 0

selectRule14UL
===
SELECT
	* 
FROM
	(	SELECT
			DISTINCT ins.id AS id ,
			ins.deal_user_id,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_start' AND
						attr.NODE_ID = ins.current_node_instance_id))        AS 
			stime ,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_end' AND
						attr.NODE_ID = ins.current_node_instance_id))        AS 
			etime 
		FROM
			t_workflow_instance ins 
			LEFT JOIN t_workflow_node_instance ni 
			ON ni.id = ins.current_node_instance_id
			--LEFT JOIN t_workflow_node_attribute attr ON ins.current_node_instance_id = attr.node_id
		WHERE
			ins.state = 'DEALING' AND
			ins.model_name = '资产处置' AND
			ni.node_name IN ('省联社备案',
			'出账处理',
			'处置过程')
			--AND attr.attr_key IN('dt_start' , 'dt_end')
			AND
			(	SELECT
					COUNT(*) 
				FROM
					t_user_token ut 
				WHERE
					ut.user_id = ins.deal_user_id AND
					now() < ut.expr_time ) > 0 AND
			(	SELECT
					COUNT(*) 
				FROM
					t_notice_trigger_log tl 
				WHERE
					tl.rule_name = 'rule14' AND
					tl.uuid = ins.id AND
					NOW() < tl.trigger_time + 30 minutes ) = 0 ) AS sub 
GROUP BY
	id,
	deal_user_id,
	stime,
	etime 
HAVING
	now BETWEEN etime - #day# days AND
	etime
	
	
selectRule15UL
===
SELECT
	ins.id ,
	ins.TITLE,
	dl.user_id AS deal_user_id,
    ni.ADD_TIME + #day# days as etime
FROM
	t_workflow_instance ins 
		LEFT JOIN t_workflow_node_instance ni 
		ON ins.current_node_instance_id = ni.id 
		LEFT JOIN t_workflow_node_instance_dealer dl 
		ON dl.node_instance_id = ni.id 
WHERE
	ins.model_name = '诉讼' AND
	ins.state = 'DEALING' AND
	ni.node_name = '法院立案' AND
	DAYS(CURRENT TIMESTAMP) - DAYS(ni.add_time) <= #day# AND
	dl.type IN('DID_DEAL' ,
	'CAN_DEAL',
	'OVER_DEAL') AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = dl.user_id AND
			CURRENT TIMESTAMP < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule15' AND
			tl.uuid = ins.id AND
			CURRENT TIMESTAMP < tl.trigger_time + 30 minutes ) = 0
			
selectRule16UL			
===
SELECT
	ins.id ,
	ins.title,
	dl.user_id AS deal_user_id ,
    ni.ADD_TIME + #day# days as etime
FROM
	t_workflow_instance ins 
		LEFT JOIN t_workflow_node_instance ni 
		ON ins.current_node_instance_id = ni.id 
		LEFT JOIN t_workflow_node_instance_dealer dl 
		ON dl.node_instance_id = ni.id 
WHERE
	ins.model_name = '诉讼' AND
	ins.state = 'DEALING' AND
	ni.node_name = '缴费' AND
	DAYS(CURRENT TIMESTAMP) - DAYS(ni.add_time) <= #day# AND
	dl.type IN('DID_DEAL' ,
	'CAN_DEAL',
	'OVER_DEAL') AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = dl.user_id AND
			CURRENT TIMESTAMP < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule16' AND
			tl.uuid = ins.id AND
			CURRENT TIMESTAMP < tl.trigger_time + 30 minutes ) = 0
			

selectGentaskUL
===
SELECT
	sub.*,
	MAX(case 
	    when last_time is null then CURRENT TIMESTAMP
	    else last_time + #v3# MONTHS 
    end, CURRENT TIMESTAMP) as plan_start_time  
FROM
	(	SELECT
			p1.LOAN_ACCOUNT,
			p1.BILL_NO ,
			p1.PRD_TYPE ,
			p1.CUST_MGR ,
			(	SELECT
					ins.plan_start_time 
				FROM
					t_workflow_instance ins 
				WHERE
					ins.model_name LIKE '%贷后跟踪%' AND
					ins.LOAN_ACCOUNT = p1.LOAN_ACCOUNT AND
					ins.auto_created = 1 
				ORDER BY
					plan_start_time DESC FETCH FIRST 1 ROWS ONLY ) AS last_time 
		FROM
			RPT_M_RPT_SLS_ACCT p1 
		WHERE
			--法人机构号（惠州农商银行0801）
			p1.CREUNIT_NO = '0801'
			--信贷产品号
			AND p1.BIZ_TYPE = #v0#
			--贷款额度
			AND p1.LOAN_AMOUNT >= #v1# 
			AND p1.LOAN_AMOUNT <= #v2#
			--台帐状态
			AND
			p1.ACCOUNT_STATUS = 1 ) AS sub 
WHERE
	last_time IS NULL OR
	( last_time IS NOT NULL AND
	CURRENT TIMESTAMP > last_time + #v3# MONTHS - #v4# days )

