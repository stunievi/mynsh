deleteTriggerLogs
===
delete from t_notice_trigger_log


selectRule1
===
SELECT
	a.BILL_NO,
	a.CUS_ID,
	b.PHONE
FROM
	ACC_LOAN AS a
LEFT JOIN CUS_BASE AS b ON
	a.CUS_ID = b.CUS_ID
LEFT JOIN t_notice_trigger_log tl ON tl.RULE_NAME = 'rule1' AND tl.UUID = a.BILL_NO AND VARCHAR_FORMAT(
				CURRENT TIMESTAMP,
				'YYYYMM'
			) = VARCHAR_FORMAT(
				tl.TRIGGER_TIME,
				'YYYYMM'
			)
WHERE
    'on' = #on#
    tl.ID is NULL AND
	a.ACCOUNT_STATUS = '1' AND
	VARCHAR_FORMAT(CURRENT TIMESTAMP,'DD') = #day#

selectRule10
===
SELECT DISTINCT
	ins.*
FROM
	t_workflow_instance ins
WHERE
	ins.state = 'DEALING'
AND(
	SELECT
		count(*)
	FROM
		t_workflow_instance child
	WHERE
		child.parent_id = ins.id
	AND child.model_name IN('催收' , '诉讼')
) = 0
AND NOW() > ins.plan_start_time
AND NOW() BETWEEN (plan_start_time + #day1# days - #day0# day)
AND plan_start_time + #day1# days
AND ins.model_name = '不良资产管理流程'
AND(
	SELECT
		count(*)
	FROM
		t_user_token ut
	WHERE
		ut.user_id = ins.deal_user_id
	AND now() < ut.expr_time
) > 0
AND(
	SELECT
		count(*)
	FROM
		t_notice_trigger_log tl
	WHERE
		tl.rule_name = 'rule10'
	AND tl.uuid = ins.id
	AND NOW() < tl.trigger_time + 30 MINUTES
) = 0


selectRule11
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
					TYPE IN ('DID_DEAL' ,
					'OVER_DEAL') )       AS deal_user_id ,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_start' 
						AND
						attr.NODE_ID = ins.current_node_instance_id)) AS stime ,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_end' 
						AND
						attr.NODE_ID = ins.current_node_instance_id)) AS etime 
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
					now() < ut.expr_time ) > 0 
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
					NOW() < tl.trigger_time + 30 MINUTES ) = 0 ) AS sub 
GROUP BY
	id ,
	deal_user_id ,
	stime ,
	etime 
HAVING
	now() BETWEEN MAX(stime,etime - #day# days) 
	AND
	etime

selectRule12
===
SELECT
	* 
FROM
	(	SELECT
			ins.id   AS id ,
			DATE((	SELECT
						attr_value 
					FROM
						T_WORKFLOW_NODE_ATTRIBUTE attr 
					WHERE
						attr.ATTR_KEY = 'dt_1' AND
						attr.NODE_ID = ins.current_node_instance_id)) AS stime ,
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
			ni.node_name = '案件跟进' ) AS sub 
GROUP BY
	id,
	deal_user_id,
	stime 
HAVING
	now() BETWEEN stime - #day# days AND
	stime AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = deal_user_id AND
			now() < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule12' AND
			tl.uuid = id AND
			NOW() < tl.trigger_time + 30 minutes ) = 0

selectRule13
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
	now() BETWEEN stime - #day# days 
	AND
	stime 
	AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = deal_user_id 
			AND
			now() < ut.expr_time ) > 0 
	AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule13' 
			AND
			tl.uuid = id 
			AND
			NOW() < tl.trigger_time + 30 minutes ) = 0

selectRule14
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
	
	
selectRule15
===
SELECT
	ins.id ,
	dl.user_id AS deal_user_id 
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
	DAY(now()) - DAY(ni.add_time) >= #day# AND
	dl.type IN('DID_DEAL' ,
	'CAN_DEAL',
	'OVER_DEAL') AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = dl.user_id AND
			now() < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule15' AND
			tl.uuid = ins.id AND
			now() < tl.trigger_time + 30 minutes ) = 0
			
selectRule16			
===
SELECT
	ins.id ,
	dl.user_id AS deal_user_id 
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
	DAY(now()) - DAY(ni.add_time) >= #day# AND
	dl.type IN('DID_DEAL' ,
	'CAN_DEAL',
	'OVER_DEAL') AND
	(	SELECT
			COUNT(*) 
		FROM
			t_user_token ut 
		WHERE
			ut.user_id = dl.user_id AND
			now() < ut.expr_time ) > 0 AND
	(	SELECT
			COUNT(*) 
		FROM
			t_notice_trigger_log tl 
		WHERE
			tl.rule_name = 'rule16' AND
			tl.uuid = ins.id AND
			NOW() < tl.trigger_time + 30 minutes ) = 0
			

selectGentask
===
SELECT
	* 
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
			AND
			p1.BIZ_TYPE = #v0#
			--贷款额度
			AND
			p1.LOAN_AMOUNT >= #v1# AND
			p1.LOAN_AMOUNT <= #v2#
			--台帐状态
			AND
			p1.ACCOUNT_STATUS = 1 ) AS sub 
WHERE
	last_time IS NULL OR
	( last_time IS NOT NULL AND
	now() > last_time + #v3# MONTHS - #v4# days )
	
selectModelName
===
SELECT
    p1.LOAN_ACCOUNT, 
	CASE 
		WHEN UPPER(l.type) = 'HOME_BANK' 
		THEN '贷后跟踪-公司银行部' 
		WHEN UPPER(l.type) = 'MINI_WEI' AND
		p1.PRD_TYPE = '01' 
		THEN '贷后跟踪-小微部公司类' 
		WHEN UPPER(l.type) = 'MINI_WEI' AND
		p1.PRD_TYPE = '02' 
		THEN '贷后跟踪-小微部个人类' 
		WHEN UPPER(l.type) = 'SALES_PERSONAL' AND
		p1.PRD_TYPE = '02' AND
		a1.MORTGAGE_FLG = '1' 
		THEN '贷后跟踪-零售银行部个人按揭' 
		WHEN UPPER(l.type) = 'SALES_PERSONAL' AND
		p1.PRD_TYPE = '02' AND
		a1.MORTGAGE_FLG = '0' AND
		a1.PRD_USERDF_TYPE = '1003' 
		THEN '贷后跟踪-零售银行部个人消费' 
		WHEN UPPER(l.type) = 'SALES_PERSONAL' AND
		p1.PRD_TYPE = '02' AND
		a1.MORTGAGE_FLG = '0' AND
		a1.PRD_USERDF_TYPE = '1004' 
		THEN '贷后跟踪-零售银行部个人经营' 
	END AS MODEL_NAME 
FROM
	t_auto_task_link l,RPT_M_RPT_SLS_ACCT p1
		LEFT JOIN ACC_LOAN AS a1 
		ON p1.LOAN_ACCOUNT=a1.LOAN_ACCOUNT 
WHERE
	l.acc_code LIKE concat(concat('%(',p1.MAIN_BR_ID),')%') AND
	p1.LOAN_ACCOUNT in (#join(v0)#) 
