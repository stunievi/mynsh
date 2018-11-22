关联状态字典
===
left join t_dict dict on dict.name = '工作流-任务状态' and ins.state = dict.v_key

任务删除判断
===
select count(1) from t_workflow_instance where 
state = 'DEALING' 
and deal_user_id = #uid# 
and start_node_name = current_node_name 
and auto_created = 0
and model_name <> '不良资产主流程'
and id 

任务查看权限
===
(
ins.deal_user_id = #uid# 
--任务相关
or exists(select 1 from t_wf_ins_dealer where uid = #uid# and type in ('DID_DEAL','OVER_DEAL','CAN_DEAL'))
--信贷主管
or exists(select 1 from t_department_manager where uid = #uid# and id = ins.dep_id) 
--总行角色
or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'DATA_SEARCH_CONDITION')
--观察者权限
or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'WORKFLOW_OBSERVER' and object_id = ins.model_id)
--任务管理权限
or exists(select 1 from t_global_permission_center where uid = #uid# and type = 'USER_METHOD' and k1 = '系统管理.任务管理')
)

用户是否可以删除任务
===
#use("任务删除判断")# = #id#

查询任务统计
===
select 
    count(1) as value,
    (100*count(1)/sum(count(1)) over ()) || '%' as per,
    model_name as name,
    year(current timestamp) as y
from (
select 
distinct ins.id,ins.model_name
from t_workflow_instance ins
left join t_user u on ins.deal_user_id = u.id
left join t_department_manager dm on dm.id = ins.dep_id 
where (dm.uid = #uid# or u.id = #uid#) and state = 'FINISHED' and year(ins.add_time) = year(current timestamp)
) inq
group by model_name


canDeal
===
select count(inss.id) from t_workflow_instance inss
left join t_workflow_node_instance_dealer dll on inss.current_node_instance_id = dll.node_instance_id
where inss.id = #iid# and dll.user_id = #uid# and dll.TYPE in ('CAN_DEAL','DID_DEAL') and inss.state = 'DEALING'

canDealNode
===
select count(1) from t_workflow_instance inss
left join t_wf_ins_dealer dll on inss.id = dll.ins_id and inss.current_node_instance_id = dll.node_id
where dll.node_id = #niid# and dll.uid = #uid# and dll.TYPE in ('CAN_DEAL','DID_DEAL') and inss.state = 'DEALING'

canCancel
===
select count(1) from t_workflow_instance inss
where inss.deal_user_id = #uid# and inss.state = 'DEALING' and inss.pub_user_id = #uid# and inss.id = #iid# and (select count(1) from t_workflow_node_instance nii where nii.instance_id = inss.id) = 1



selectModels
===
select 
@pageTag(){
    id,name,model_name,add_time,open,first_open,last_modify_time
@}
from t_workflow_model 
where 1 = 1 
@if(isNotEmpty(modelName)){
    and model_name like #'%' + modelName + '%'#
@}
@pageIgnoreTag(){
    order by last_modify_time desc
@}

查询工作流模型(ID)
===
select id,name,model_name,info from t_workflow_model where id = #id#

selectNodeAttrMapUL
===
select 
@for(key in keys){
    LISTAGG(case when attr_cname = '#text(key)#' then attr_value else '' end,'') as #text(key)# #text(keyLP.last?"":"," )#
@}
from t_workflow_node_attribute where node_id = #nid# and deal_user_id = #uid#

selectInsAttrMapUL
===
select 
@for(key in keys){
    LISTAGG(case when attrcname = '#text(key)#' then attr_value else '' end,'') as #text(key)# #text(keyLP.last?"":"," )#
@}
from t_workflow_ins_attr where instance_id = #id#

selectNodeAttr
===
select attr_key, attr_value from t_workflow_node_attribute where node_id = #nid# and deal_user_id = #uid#
union all
select attr_key, attr_value from t_workflow_ins_attr where instance_id = #iid#
and type = 'INNATE'

selectNodeByName
===
select 

查询节点可处理人(任务ID)
===
select * from (
select gpc.*
-- 单一部门个数
, (select count(distinct pid) from T_GLOBAL_PERMISSION_CENTER where type = 'WORKFLOW_MAIN_QUARTER' and object_id = ins.model_id and k1 =  
@if(isNotEmpty(name)){
    #name#
@}else{
    ins.start_node_name
@}
) as dc
-- 是否部门线
, case when otype = 'QUARTERS' then max(FUNC_CAN_MANAGE(gpc.oid,ins.dep_id),FUNC_CAN_MANAGE(ins.dep_id,gpc.oid)) else 0 end as online
from t_global_permission_center gpc, t_workflow_instance ins
where gpc.type = 'WORKFLOW_MAIN_QUARTER' and  gpc.object_id = ins.model_id and k1 = 
@if(isNotEmpty(name)){
    #name#
@}else{
    ins.start_node_name
@}
and ins.id = #id#
) inq
where (inq.otype = 'QUARTERS' and (inq.dc = 1 or inq.online > 0)) or (inq.otype <> 'QUARTERS')


selectNodeDealUidsUL
===
select gpc.*
 -- 单一部门个数
 ,case when user_type = 'QUARTER' then
        (select count(distinct id) as NUM from t_global_permission where type = 'WORKFLOW_MAIN_QUARTER'   and object_id = gpc.object_id)
    else 0 end
     as dcount
 -- 是否部门线
 ,case when user_type = 'QUARTER' then max(FUNC_CAN_MANAGE(gpc.did,#did#),FUNC_CAN_MANAGE(#did#,gpc.did)) else 0 end
     as online
 from t_global_permission_center gpc
 where type = 'WORKFLOW_MAIN_QUARTER' and object_id = #nid#
 
selectNodeByInsAndName
===
select no.*,ins.deal_user_id,ins.dep_Id from t_workflow_instance ins left join t_workflow_node no on no.model_id = ins.workflow_model_id where no.name = #name# and ins.id = #iid#


selectWorkflowDealers
===
select dl.*, ni.node_name, ni.type as node_type, coalesce(ni.files,'') as files
from t_workflow_node_instance ni 
right join t_workflow_node_instance_dealer dl on ni.id = dl.node_instance_id
where ni.instance_id = #iid#
and
dl.type in ('CAN_DEAL','DID_DEAL','OVER_DEAL')
order by dl.last_modify asc, dl.node_instance_id asc


selectWorkflowNodeAttributes
===
select attr.* 
from t_workflow_node_instance ni 
right join t_workflow_node_attribute attr on ni.id = attr.node_id
where ni.INSTANCE_ID = #iid#


selectInsById
===
select ins.*, (#use("canDeal")#) as deal, (#use("canCancel")#) as cancel 
from t_workflow_instance ins where ins.id = #iid#


selectWorkflowDepartmentByManager
===
select distinct gpc.did,gpc.dname,gpc.uname,gpc.utname,gpc.uid from t_global_permission_center gpc
inner join t_department_manager dm on dm.id = gpc.did and dm.uid = #uid#
where gpc.object_id = #mid# and gpc.type = 'WORKFLOW_PUB'

selectWorkflowDepartmentByUser
===
select distinct gpc.did,gpc.dname,gpc.uname,gpc.utname,gpc.uid from t_global_permission_center gpc
where gpc.object_id = #mid# and gpc.type = 'WORKFLOW_PUB' and gpc.uid = #uid#

查询绑定部门ID列表
===
select id from t_org_ext where #text(keys)#


查询工作流开启状态
===
select count(1) from t_workflow_model where model_name = (select model_name from t_workflow_model where id = #id#) and open = 1



查询可接受的任务列表
===
select 
@pageTag(){
    ins.id,ins.title,ins.model_name,ins.add_time,ins.plan_start_time, ins.loan_account
    , max(case when attr_key = 'CUS_NAME' then attr_value else '' end ) over(partition by ins.id) as CUS_NAME
    , value(dict.v_value,ins.state) as state
@}
from t_workflow_instance ins
left join t_workflow_ins_attr attr on ins.id = attr.instance_id
left join t_dict dict on dict.name = '工作流-任务状态' and ins.state = dict.v_key
where ins.state in ('POINT','TRANSFORM') and point_receiver = #uid#
@if(isNotEmpty(startTime)){
    and ins.plan_start_time >= timestamp('#text(startTime)#')
@}
@if(isNotEmpty(endTime)){
    and ins.plan_start_time <= timestamp('#text(endTime)#')
@}
@pageIgnoreTag(){
    order by ins.plan_start_time desc
@}



查询所有子任务
===
with t1(id,pid,state) as (
    select id, parent_id as pid,state from t_workflow_instance where PARENT_ID = #iid#
    union all
    select child.id, child.parent_id as pid, child.state from t1, t_workflow_instance child where child.parent_id = t1.id 
)
select distinct * from t1 where state <> 'FINISHED'

 
查询模型(包括发起节点)
===
select 
distinct m.* 
, (select n.node from t_workflow_node n where n.model_id = m.id and n.start = 1 fetch first rows only) as start_node
from t_workflow_model m 
 left join t_global_permission_center gpc on gpc.type = 'WORKFLOW_PUB' and m.id = gpc.object_id
 where m.open = 1 and m.MODEL_NAME = #mname# and gpc.uid = #uid#
 
 
查询任务发布权限(通过UID)
===
select uid,utname,uname,oid,oname,otype,pid,pname from t_global_permission_center gpc where 
gpc.k1 = #nname# and gpc.object_id = #mid#
and type = 'WORKFLOW_MAIN_QUARTER'
and (uid in 
(select uid from T_ORG_USER ou where pid in(select id from t_department_manager where uid = #uid#)) or uid = #uid#)

查询任务发布权限(通过UID获取1个)
===
select * from (#use("查询任务发布权限(通过UID)")#) as inq
where inq.uid = #uuid#

 
查询任务(验证授权)
===
select 
ins.*
, coalesce(ni.files,'') as files
from t_workflow_instance ins
left join t_workflow_node_instance ni on ins.current_node_instance_id = ni.id
where ins.id = #iid# and FUNC_CAN_DEAL_BY_INS(#uid#,#iid#) > 0


查询任务
===
select * from t_workflow_instance ins
where #use("任务查看权限")#
and ins.id = #id#

查询任务列表
===
select
@pageTag(){
    ins.id,ins.title,ins.model_id,ins.model_name,ins.plan_start_time,ins.add_time, auto_created,ins.dep_name,ins.deal_user_name,ins.parent_id,ins.start_node_name,ins.current_node_name,ins.pub_user_id
        , ins.loan_Account
        , ins.finished_date
        , ins.f1 as CUS_NAME 
        , ins.f2 as PHONE
        , ins.f3 as CERT_TYPE
        , ins.f4 as CERT_CODE
    , case when ins.auto_created = 1 then '是' else '否' end as auto_created 
    -- 任务状态
    , value(dict.v_value, ins.state) as state
    
    --资料收集-绑定页面 start
    @if(isNotEmpty(infolink)){
        , li.LOAN_ACCOUNT
        , li.id as link_id
    @}
    --end
    
    --已处理任务 start
        , (select last_modify from t_wf_ins_dealer where type = 'OVER_DEAL' AND ins_id = ins.id and uid = #uid# order by last_modify desc fetch first 1 rows only) as last_deal_date
    --end
    
    -- 指派移交列表用到 start
        , case when ins.state in ('COMMON','UNRECEIVED') then 1 else 0 end as point
        , case when ins.state in ('DEALING') then 1 else 0 end as transform
        , (#use("任务删除判断")# = ins.id) as delete
    -- end
@}
    from t_workflow_instance ins 
    -- 对照的资料收集/不良主流程
    @if(isNotEmpty(bindCusId)){
       inner join RPT_M_RPT_SLS_ACCT p on p.cus_id = (#bindCusId#) and p.loan_account = ins.loan_account  
    @}
    @if(isNotEmpty(bindBL)){
       inner join RPT_M_RPT_SLS_ACCT p on p.cus_id = (#bindBL#) and p.loan_account = ins.loan_account  
    @}
    @if(isNotEmpty(ob)){
        inner join t_global_permission_center gpc on gpc.type = 'WORKFLOW_OBSERVER' and gpc.uid = #uid# and gpc.object_id = ins.model_id
    @}
    -- end
    
    --资料收集-绑定页面 start
    @if(isNotEmpty(infolink)){
        left join t_info_collect_link li on li.ins_id = ins.id        
    @}
    --end
    
    #use("关联状态字典")#
    where 1 = 1
    
    --资料收集-绑定页面 start
    @if(isNotEmpty(infolink)){
        and ins.model_name = '资料收集'
    @}
    --资料收集-绑定页面 end
    
    --待处理任务 start
    --只看自己的
    @if(isNotEmpty(daichuli)){ 
        and ins.state = 'DEALING' and exists(
            select 1 from t_wf_ins_dealer where ins_id = ins.id and node_id = ins.current_node_instance_id and type in ('DID_DEAL', 'CAN_DEAL') and uid = #uid#
        )
    --已处理任务 start
    @}else if(isNotEmpty(yichuli)){ 
        and exists(
           select 1 from t_wf_ins_dealer where ins_id = ins.id and type in ('OVER_DEAL') and uid = #uid#
       ) 
    --公共任务 start
    @}else if(isNotEmpty(common)){
        and ins.state = 'COMMON' and (
         exists(
           select 1 from t_wf_ins_dealer where ins_id = ins.id and type in ('CAN_DEAL') and uid = #uid#) 
          or exists(select 1 from t_department_manager where uid = #uid# and id = ins.dep_id)
        )
    --end
    @}else{
        -- 默认只查询自己和自己管理的任务
        -- 不是管理员的情况
        @if(isEmpty(su)){
            -- 主管查看
            and (
                #use("任务查看权限")#
            )
        @}
    @}
    
    @if(isNotEmpty(LOAN_ACCOUNT)){
        @if(isNotEmpty(infolink)){
            and li.loan_account = #LOAN_ACCOUNT#
        @} else {
            and ins.loan_account = #LOAN_ACCOUNT#
        @}
    @}
    
    @if(isNotEmpty(id)){
        and ins.id = #id#
    @}
    @if(isNotEmpty(parentId)){
        and ins.parent_id = #parentId#
    @}
    @if(isNotEmpty(state)){
        and ins.state = #state#
    @}
    @if(isNotEmpty(modelName)){
        and ins.model_name like #'%' + modelName + '%'#
    @}
    @if(isNotEmpty(start_date)){
        and ins.plan_start_time >= timestamp('#text(start_date)#')
    @}
    @if(isNotEmpty(end_date)){
        and ins.plan_start_time <= timestamp('#text(end_date)#')
    @}
    --字段删选 start
    @if(isNotEmpty(CUS_NAME)){
        and a1.attr_value like #'%' + CUS_NAME + '%'#
    @}
    --字段筛选 end
    --预任务
    @if(isNotEmpty(pre)){
        and ins.plan_start_time >= current timestamp
    @}
    --部门未完成
    @if(isNotEmpty(department)){
        @if(department == 0 || department == '0'){
            and ins.state = 'DEALING'
        @}else if(department == 1 || department == '1'){
            and ins.state = 'FINISHED'
        @}
    @}
    -- 绑定-资料收集
    @if(isNotEmpty(bindCusId)){
        and ins.state = 'FINISHED' and ins.model_name = '资料收集'
    @}
    -- 绑定-不良主流程
    @if(isNotEmpty(bindBL)){
        and ins.state = 'FINISHED' and ins.model_name = '不良资产主流程'
    @}

@pageIgnoreTag(){
    order by plan_start_time desc
@}



查询贷后检查表
===
select 
ins.* 
, (select count(1) from t_workflow_instance tins where tins.MODEL_NAME = ins.model_name and state = 'FINISHED' ) as total_num
, (select count(1) from t_workflow_instance tins where tins.MODEL_NAME = ins.model_name and state = 'FINISHED' and year(plan_start_time) = year(current timestamp) and tins.PLAN_START_TIME <= ins.PLAN_START_TIME  ) as now_num
, year(current timestamp) as curr_year
from t_workflow_instance ins where id = #id#

查询拒贷记录列表
===
select 
@pageTag(){
* 
@}
from
(
select distinct
--客户名称
max(case when attr.attr_key = 'CUS_NAME' then attr.attr_value else '' end ) over(partition by ins.id order by last_modify_date desc) as CUS_NAME
--证件号码
, max(case when attr.attr_key = 'CERT_CODE' then attr.attr_value else '' end ) over(partition by ins.id order by last_modify_date desc) as CERT_CODE
--联系电话
, max(case when attr.attr_key = 'PHONE' then attr.attr_value else '' end ) over(partition by ins.id order by last_modify_date desc) as PHONE
--拒贷说明
, max(case when attr.attr_key = 'ps' then attr.attr_value else '' end ) over(partition by ins.id order by last_modify_date desc) as ps
, ins.finished_date, ins.deal_user_name, ins.dep_name, ins.id
from t_workflow_instance ins
inner join t_workflow_ins_attr attr on attr.ins_id = ins.id
inner join (select at.ins_id from t_workflow_ins_attr at where at.attr_key = 'key' and at.attr_value = '否') ddd on ddd.ins_id = ins.id
where 
--ins.id in 
--(select at.ins_id from t_workflow_ins_attr at where at.ins_id = ins.id and at.attr_key = 'key' and at.attr_value = '否')
--and 
ins.model_name = '资料收集' and ins.state = 'FINISHED'
@if(isNotEmpty(CUS_NAME)){
    and exists (select 1 from t_workflow_ins_attr  where attr_key = 'CUS_NAME' and ATTR_VALUE like #'%'+CUS_NAME+'%'# and ins_id = ins.id)
@}
@if(isNotEmpty(PHONE)){
    and exists (select 1 from t_workflow_ins_attr  where attr_key = 'PHONE' and ATTR_VALUE like #'%'+PHONE+'%'# and ins_id = ins.id)
@}
@if(isNotEmpty(CERT_CODE)){
    and exists (select 1 from t_workflow_ins_attr  where attr_key = 'CERT_CODE' and ATTR_VALUE like #'%'+CERT_CODE+'%'# and ins_id = ins.id)
@}
@if(isNotEmpty(startDate)){
    and ins.finished_date >= timestamp('#text(startDate)#')
@}
@if(isNotEmpty(endDate)){
    and ins.finished_date <= timestamp('#text(endDate)#')
@}


@pageIgnoreTag(){
order by finished_date desc
@}
)

检查前置任务是否已完成
===
select count(1) from t_workflow_instance where id = (select prev_instance_id from t_workflow_instance where id = #id# ) and state = 'FINISHED'


用户是否可以管理这个任务
===
select count(1) from  
(select deal_user_id, dep_id from t_workflow_instance where id = #iid# and state not in ('COMMON', 'POINT', 'TRANSFORM')) ins
where (ins.deal_user_id = #uid#) or (
    exists(select count(1) from t_department_manager where uid = #uid# and id = ins.dep_id)
)

