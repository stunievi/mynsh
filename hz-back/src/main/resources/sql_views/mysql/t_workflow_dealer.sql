

CREATE OR REPLACE SQL SECURITY DEFINER VIEW `t_workflow_dealer` AS SELECT
	`gpc`.`type` AS `type` ,
	`gpc`.`user_type` AS `user_type` ,
	`gpc`.`object_id` AS `object_id` ,
	`gpc`.`link_id` AS `link_id` ,
	`gpc`.`description` AS `description` ,
	`gpc`.`uname` AS `uname` ,
	`gpc`.`uid` AS `uid` ,
	`gpc`.`dname` AS `dname` ,
	`gpc`.`did` AS `did` ,
	`gpc`.`rname` AS `rname` ,
	`gpc`.`rid` AS `rid` ,
	`gpc`.`qname` AS `qname` ,
	`gpc`.`qid` AS `qid` ,
	`gpc`.`uacc_code` AS `uacc_code` ,
	`ins`.`id` AS `instance_id` ,
	`ins`.`current_node_instance_id` AS `node_instance_id`
FROM
	(
		`t_workflow_instance` `ins`
		JOIN `t_global_permission_center` `gpc`
	)
WHERE
	(
		(`ins`.`state` = 'DEALING')
		AND(
			(
				(
					`gpc`.`type` = 'WORKFLOW_MAIN_QUARTER'
				)
				AND(
					`gpc`.`object_id` = `ins`.`current_node_model_id`
				)
			)
			OR(
				(`gpc`.`type` = 'WORKFLOW_PUB')
				AND(
					`gpc`.`object_id` = `ins`.`workflow_model_id`
				)
			)
		)
	);