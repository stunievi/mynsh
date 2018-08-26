check_token
===
select count(1) from t_user_token where token = #token# and expr_time >  CURRENT TIMESTAMP

check_user
===
select count(1)

all_user
===
select * from t_user_token
@orm.single({"userId":"id"},"User");  

selectQuarters
===
select q.* from t_user_quarters uq left join t_quarters q on q.id = uq.quarters_id
where uq.user_id = #userId#

selectPsByUser
===
select * from t_global_permission_center where type in #type# and user_type in #userType# and uid = #uid#

selectManagedDepartment
===
select * from t_department_manager where uid = #uid#
