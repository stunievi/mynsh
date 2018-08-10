CREATE OR REPLACE VIEW t_global_permission_center AS SELECT
	gp."TYPE" AS TYPE ,
	gp.user_type AS user_type ,
	gp.object_id AS object_id ,
	gp.link_id AS link_id ,
	gp.description AS description ,
	u.true_name AS uname ,
	u.id AS uid ,
	d.name AS dname ,
	d.id AS did ,
	r.name AS rname ,
	r.id AS rid ,
	q.name AS qname ,
	q.id AS qid ,
	u.acc_code AS uacc_code ,
	u.phone AS phone
FROM
	(
		t_global_permission gp CROSS
	JOIN T_USER u
	LEFT JOIN t_user_quarters uq ON
		u.id = uq.user_id
	LEFT JOIN t_quarters q ON
		q.id = uq.quarters_id
	LEFT JOIN t_department d ON
		d.id = q.department_id
	LEFT JOIN t_user_role ur ON
		u.id = ur.user_id
	LEFT JOIN t_role r ON
		r.id = ur.role_id
	)
WHERE
	(
		(
			(
				gp.user_type = 'QUARTER'
			)
			AND(
				gp.link_id = q.id
			)
		)
		OR(
			(
				gp.user_type = 'ROLE'
			)
			AND(
				gp.link_id = r.id
			)
		)
		OR(
			(
				gp.user_type = 'USER'
			)
			AND(
				gp.link_id = u.id
			)
		)
	)