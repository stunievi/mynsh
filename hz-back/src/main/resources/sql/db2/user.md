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

用户登录校验
===
select id,true_name,cloud_username,cloud_password from t_user where (acc_code = #username# or username = #username#) and password = #password#

查询用户列表
===
select 
@pageTag(){
id,add_time,true_name,username,phone
, case when baned = 0 then '启用' else '禁用' end as state 
@if(isNotEmpty(ooid)){
    , case when exists(select 1 from t_user_org where uid = u.id and oid = #ooid#) then 1 else 0 end as has_org
@}
@}
from t_user u
where 1 = 1 
@if(isNotEmpty(name)){
   and (username like #'%'+name+'%'# or true_name like #'%'+name+'%'#)
@}
@if(baned == 1 || baned == '1' || baned == 0 || baned == '0'){
    and baned = #baned#
@}
@if(onlyO == 1 || onlyO == '1'){
    and exists(select 1 from t_user_org where uid = u.id and oid = #ooid#)
@}
@pageIgnoreTag(){
    order by add_time desc
@}


查询部门列表
===
select 
id,name,info,parent_id,acc_code
-- case when type = 'DEPARTMENT' then '部门' else '岗位' end as type
from t_org where type = 'DEPARTMENT'
@if(isNotEmpty(accCode)){
    and acc_code = #accCode#
@}
order by sort asc



查询部门和岗位列表
===
select 
id,name,info,parent_id,type,acc_code
-- case when type = 'DEPARTMENT' then '部门' else '岗位' end as type
from t_org where type in ('DEPARTMENT','QUARTERS')
@if(isNotEmpty(accCode)){
    and acc_code = #accCode#
@}
order by sort asc

查询组织机构
===
select o1.*, 
case 
when o1.parent_id = 0 or o1.parent_id is null then '顶级部门' 
else o2.name 
end as pname from t_org o1
left join t_org o2 on o1.parent_id = o2.id
where o1.id = #id#

查询角色列表
===
select 
@pageTag(){
*
@}
from t_org where type = 'ROLE'
@if(isNotEmpty(name)){
    and name like #'%' + name + '%'#
@}
@pageIgnoreTag(){
    order by sort asc
@}

查询组织机构列表
===
select
@pageTag(){
*
@}
from t_org
where 1 = 1
@if(isNotEmpty(role)){
    and type = 'ROLE'
@}
@if(isNotEmpty(type)){
    and type in (#join(type)#) 
@}
@pageIgnoreTag(){
    order by sort asc
@}

查询子机构ID
===
with rs(id,pid) as (
select id,PARENT_ID as pid from t_org where id = #oid#
union all
select o1.id, o1.parent_id as pid from t_org o1, rs o2 where o1.parent_id = o2.id
) select * from rs


查询用户(通过组织机构)
===
select u.id,u.username,u.phone,u.true_name,u.acc_code from t_user u
inner join t_user_org uo on uo.uid = u.id and uo.oid = #oid#

查询组织机构列表(通过用户ID)
===
select oid as id,oname as name from t_org_user where uid = #uid#

解除用户组织机构(通过组织机构ID)
===
delete from t_user_org where oid in (#join(oids)#)

解除用户组织机构(通过用户和组织机构ID)
===
delete from t_user_org where oid = #oid# and uid = #uid#

添加用户组织机构
===
insert into t_user_org(oid,uid)values(#oid#,#uid#)


查询授权列表
===
select gp.*,o.type as otype,o.full_name as oname from t_global_permission gp
left join t_org_ext o on o.id = gp.oid
where 1 = 1
@if(isNotEmpty(objectId)){
    and gp.object_id = #objectId#
@}
@if(isNotEmpty(type)){
    and gp.type = #type# 
@}
@if(isNotEmpty(oid)){
    and gp.oid = #oid#
@}
@if(isNotEmpty(k1)){
    and gp.k1 = #k1#
@}

是否拥有权限
===
select max(num) from (SELECT
			COUNT(1)  as num
		FROM
			t_global_permission_center 
		WHERE
			type = 'USER_METHOD' AND
			uid = #uid# AND
			k1 IN (#join(methods)#)
			union all
SELECT
	COUNT(1)  as num
FROM
	t_user 
WHERE
	su = 1 AND
	id = #uid# )


