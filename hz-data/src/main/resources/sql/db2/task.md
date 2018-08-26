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