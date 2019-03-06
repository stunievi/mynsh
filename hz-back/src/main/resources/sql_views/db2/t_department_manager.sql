CREATE OR REPLACE VIEW T_DEPARTMENT_MANAGER AS
SELECT d.*,u.id as uid FROM t_department d
left join t_quarters q on d.id = q.department_id
left join t_user_quarters uq on uq.quarters_id = q.id
left join t_user u on u.id = uq.user_id
where q.manager = 1