canPub
===
select count(1) as NUM from T_GLOBAL_PERMISSION_CENTER where type = 'WORKFLOW_PUB' and uid = #uid# and OBJECT_ID in (#join(mid)#)

canPoint
===
select count(1) as num from T_GLOBAL_PERMISSION_CENTER gpc
left join t_quarters q on gpc.link_id = q.id
left join T_DEPARTMENT_MANAGER dm on dm.id = q.department_id
where
gpc.type = 'WORKFLOW_PUB' and dm.uid = #uid# and gpc.OBJECT_ID in (#join(mid)#)

canPubOrPoint
===
SELECT
	SUM(num1) AS pub,
	SUM(num2) AS point,
	object_id 
FROM
	(	SELECT
			COUNT(1) AS NUM1,
			0        AS num2 ,
			object_id 
		FROM
			T_GLOBAL_PERMISSION_CENTER 
		WHERE
			TYPE = 'WORKFLOW_PUB' AND
			uid = #uid# AND
			OBJECT_ID IN (#join(mid)#) 
		GROUP BY
			object_id 
		UNION
			ALL SELECT
					0        AS num1 ,
					COUNT(1) AS num2,
					object_id 
				FROM
					T_GLOBAL_PERMISSION_CENTER gpc 
					LEFT JOIN t_quarters q 
					ON gpc.link_id = q.id 
					LEFT JOIN T_DEPARTMENT_MANAGER dm 
					ON dm.id = q.department_id 
				WHERE
					gpc.type = 'WORKFLOW_PUB' AND
					dm.uid = #uid# AND
					gpc.OBJECT_ID IN (#join(mid)#) 
				GROUP BY
					object_id ) 
GROUP BY
	object_id