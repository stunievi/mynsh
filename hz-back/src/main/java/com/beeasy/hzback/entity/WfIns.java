package com.beeasy.hzback.entity;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.modules.system.service.NoticeService2;
import com.beeasy.hzback.view.DManager;
import com.beeasy.hzback.view.DepartmentUser;
import com.beeasy.hzback.view.GPC;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.BigDecimalValueObjectCodec;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.util.DigestUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.validation.constraints.AssertTrue;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beeasy.hzback.entity.WfIns.State.*;
import static com.beeasy.hzback.entity.WfInsAttr.Type.INNATE;
import static com.beeasy.hzback.entity.WfNodeDealer.Type.*;
import static java.util.stream.Collectors.toList;

@Table(name = "T_WORKFLOW_INSTANCE")
@Getter
@Setter
public class WfIns extends ValidGroup {
    @AssignID("simple")
    Long id;
    Long        currentNodeInstanceId;
    Long        currentNodeModelId;
    String      currentNodeName;
    Long        modelId;
    State       state;
    Date        finishedDate;
    Long        depId;
    String      depName;
    Long        parentId;
    String      parentModelName;
    Long        pubUserId;
    String pubUserName;
    String pubRoleName;
    Long        dealUserId;
    String      dealUserName;

    Long pointReceiver;
    Long pointManager;
    Long prevInstanceId;

    String title;
    String info;

    Date addTime;
    Date planStartTime;

    String  modelName;
    String startNodeName;
    Boolean autoCreated;

    String billNo;
    String loanAccount;

    String nodes;
    String model;
//    String cusName;


    //附加字段
    String f1;
    String f2;
    String f3;
    String f4;
    String f5;
    String f6;
    String f7;

    public enum State {
        //公共任务
        COMMON,
        //待指派
        UNRECEIVED,
        //处理中
        DEALING,
        //已取消
        CANCELED,
        //已完成
        FINISHED,
        //暂停中
        PAUSE,
        //指派中
        POINT,
        //移交中
        TRANSFORM ;
    }


//    @AssertTrue(message = "你没有权限删除这个任务", groups = Delete.class)
//    protected boolean getZDelete() {
//        User user = U.getSQLManager().unique(User.class, AuthFilter.getUid());
//        Long[] ids = (Long[]) get("$id");
//        for (Long aLong : ids) {
//            if (!user.getSu() && !canManage(AuthFilter.getUid(), aLong)) {
//                return false;
//            }
//        }
//        return true;
//    }
//



    @AssertTrue(groups = Add.class)
    protected boolean getZAdd() throws NoSuchFieldException, IllegalAccessException {
        SQLManager sqlManager = U.getSQLManager();

        addTime = new Date();
        state = State.DEALING;
        if(null == autoCreated){
            autoCreated = false;
        }

        if(null == pubUserId){
            pubUserId = AuthFilter.getUid();
        }

        //检查任务ID是否被占用
        Assert(null == id || sqlManager.templateCount(getClass(), C.newMap("id", id)) == 0, "该任务已被创建, 请不要重复提交");

        //检索模型
        WfModel wfModel = null;
        if (null != modelId) {
            wfModel = sqlManager.lambdaQuery(WfModel.class)
                .andEq(WfModel::getId, modelId)
                .andEq(WfModel::getOpen, true)
                .single();
        } else if (null != modelName) {
            wfModel = sqlManager.lambdaQuery(WfModel.class)
                .andEq(WfModel::getModelName, modelName)
                .andEq(WfModel::getOpen, true)
                .single();
        }
        Assert(null != wfModel, "没找到对应的工作流模型");

        modelId = wfModel.getId();
        modelName = wfModel.getModelName();
        startNodeName = wfModel.getStartNodeName();

        GPC gpc = null;
        //公共任务随便获取一个
        if(Objects.equals((Boolean)get("$common"), true)) {
            gpc = sqlManager.selectSingle("workflow.查询任务发布权限(通过UID)", C.newMap("mid", modelId, "nname", wfModel.getStartNodeName(), "uid", pubUserId), GPC.class);
        }
        //获取具体的执行人
        else{
            gpc = sqlManager.selectSingle("workflow.查询任务发布权限(通过UID获取1个)", C.newMap("mid", modelId, "nname", wfModel.getStartNodeName(), "uid", pubUserId, "uuid", dealUserId), GPC.class);
        }
        Assert(null != gpc, "你没有权限发布这个任务");

        //确定所属部门
        if (gpc.getOtype().equals(Org.Type.QUARTERS)) {
            depId = gpc.getPid();
            depName = gpc.getPname();
        }

        if(Objects.equals((Boolean)get("$common"), true)){
            state = COMMON;
            Assert(modelName.indexOf("贷后跟踪") > -1 , "只有贷后任务才允许发布公共任务");

            //拿到所属部门的主管岗位
            DManager dm = sqlManager.lambdaQuery(DManager.class)
                .andEq(DManager::getUid, pubUserId)
                .andEq(DManager::getId, depId)
                .single();
            if(null != dm){
                pubUserName = dm.getUtname();
                pubRoleName = dm.getOname();
            }
        }
        //指派
        else if(!Objects.equals(dealUserId,pubUserId)){
            User u = sqlManager.lambdaQuery(User.class)
                .andEq(User::getId, dealUserId)
                .select(User::getTrueName)
                .stream()
                .findFirst()
                .orElse(null);
            dealUserName = u.getTrueName();

            //贷后和不良登记才可以指派
            Assert(modelName.indexOf("贷后跟踪") > -1, "只有贷后任务才允许指派");

            //拿到所属部门的主管岗位
            DManager dm = sqlManager.lambdaQuery(DManager.class)
                .andEq(DManager::getUid, pubUserId)
                .andEq(DManager::getId, depId)
                .single();
            if(null != dm){
                pubUserName = dm.getUtname();
                pubRoleName = dm.getOname();
            }
        }
        //自己执行
        else {
            dealUserId = gpc.getUid();
            dealUserName = gpc.getUtname();
            pubUserName = gpc.getUtname();
            pubRoleName = gpc.getOname();
        }

        if (null == planStartTime) {
            planStartTime = new Date();
        }

        //查找初始节点
        JSONObject model = JSON.parseObject(wfModel.getModel());
        this.model = wfModel.getModel();
        JSONObject node = getNodeModel(model, wfModel.getStartNodeName());
        Assert(null != node, "找不到初始节点, 请联系管理员");


        JSONObject innateMap = new JSONObject();
        JSONObject data = (JSONObject) get("$data");
        JSONObject startNodeData = (JSONObject) get("$startNodeData");
        if(null == startNodeData) startNodeData = new JSONObject();
        innateMap.putAll(data);
        innateMap.putAll(startNodeData);
        loanAccount = (innateMap.getString("LOAN_ACCOUNT"));
        billNo = (innateMap.getString("BILL_NO"));

        //保存自定义的固有字段
        String[] custom = {"CUS_NAME","PHONE","CERT_TYPE","CERT_CODE"};
        bindCustomFields(custom,innateMap);

        //检查子任务
        //检查是否存在主流程
        WfIns buliang = sqlManager.lambdaQuery(WfIns.class)
            .andEq(WfIns::getLoanAccount, loanAccount)
            .andEq(WfIns::getModelName, "不良资产主流程")
            .select(WfIns::getId, WfIns::getPrevInstanceId)
            .stream()
            .findFirst()
            .orElse(null);

        List<String> childModelNames = C.newList("催收","诉讼","资产处置","抵债资产接收","利息减免","强制执行","房屋出租","资产拍卖","资产协议出售");
        //子任务要求父任务存在
        if(childModelNames.contains(modelName)){
            Assert(
                null != buliang
               , "查询不到符合条件的不良主流程"
            );
            parentId = buliang.id;
        }
        //同一时间只能存在一个不良主流程
        if(S.eq(modelName, "不良资产主流程")){
            Assert(null == buliang, "同一个贷款台账只允许一条激活的不良主流程存在");
        }
        //同一时间只能存在一个不良资产登记
        if(S.eq(modelName, "不良资产登记")){
            Assert(
                sqlManager.lambdaQuery(WfIns.class)
                    .andEq(WfIns::getLoanAccount, loanAccount)
                    .andEq(WfIns::getModelName, "不良资产登记")
                    .andEq(WfIns::getState, State.DEALING)
                    .count() == 0
                , "同一个台账只允许一条激活的不良资产登记存在"
            );
        }
        //利息减免要求前置任务完成
        if(S.eq(modelName, "利息减免") || S.eq(modelName, "资产处置") || S.eq(modelName, "抵债资产接收")){
            JSONObject obj = sqlManager.selectSingle("workflow.检查前置任务是否已完成", C.newMap("id", buliang.getId()), JSONObject.class);
            Assert(
                obj.getInteger("1") > 0, "对应的不良资产登记没有完成, 无法发起该任务"
            );
        }



        //保存数据到下一个chain
        set("$model", model);
        set("$node", node);
        set("$gpc", gpc);
        set("$innateMap", innateMap);

        Log.log("发起任务");

        return true;
    }


    /***********/

    private static final ScriptEngine JsEngine = new ScriptEngineManager().getEngineByName("javascript");

    /**
     * @apiDefine FZPageParam
     *
     * @apiParam {int} page 页码，默认1
     * @apiParam {int} size 每页数量，默认10
     *
     */

    /**
     * @apiDefine FzCommon
     *
     * @apiHeader {string} HZToken 登录后获得的令牌
     *
     * @apiSuccessExample 请求成功
     * {
     *     "success": true,
     *     "data":{
     *          ...
     *     }
     * }
     * @apiErrorExample 请求异常:
     * {
     *     "success": false,
     *     "errorMessage": "错误请求"
     * }
     */

    /**
     * @api {get} {辅助系统地址}/api/auto/wfins/getList 查询任务列表
     * @apiGroup FZSYS
     * @apiVersion 0.0.1
     * @apiUse FzCommon
     *
     * @apiParam {int} daichuli 是否待处理任务 1/0
     * @apiParam {int} yichuli 是否已处理任务 1/0
     * @apiParam {int} ob 是否观察的任务 1/0
     * @apiParam {int} common 是否公共任务 1/0
     * @apiParam {int} pre 是否预任务 1/0
     * @apiParam {int} su 是否管理员查看任务, 需有管理员权限方可生效 1/0
     * @apiParam {int} zhipaiyijiao 是否可以指派移交, 需有该任务管理权限方可生效 1/0
     * @apiParam {int} department 是否查看部门任务, 需有该任务管理权限方可生效 1为部门已处理, 0为部门待处理
     * @apiParam {int} infolink 是否查看已绑定的资料收集任务 1/0
     * @apiParam {string} modelName 任务模型名
     * @apiParam {string} start_date/START_DATE 任务查询起始时间
     * @apiParam {string} end_date/END_DATE 任务查询结束时间
     * @apiParam {string} CUS_NAME 任务关联客户名（如果有)
     * @apiParam {string} PHONE 任务关联手机号（如果有)
     * @apiParam {string} CERT_CODE 任务关联证件号（如果有)
     * @apiParam {string} LOAN_ACCOUNT 任务绑定贷款账号（如果有)
     * @apiParam {string} state 任务状态
     * @apiParam {long} id 任务ID
     *
     * @apiUse FZPageParam
     *
     *
     * @apiSuccess {long} id id
     * @apiSuccess {string} addTime 创建时间
     * @apiSuccess {string} modelId 模型ID
     * @apiSuccess {string} title 任务名
     * @apiSuccess {int} delete 是否可以删除 1/0
     * @apiSuccess {int} point 是否可以指派 1/0
     * @apiSuccess {int} transform 是否可以移交 1/0
     * @apiSuccess {string} loanAccount 贷款账号
     * @apiSuccess {string} dealUserName 任务处理人
     * @apiSuccess {string} state 任务状态
     * @apiSuccess {string} autoCreated 是否系统自动创建的任务 是/否
     * @apiSuccess {string} finishedDate 任务完成时间
     * @apiSuccess {string} planStartTime 预任务计划开始时间
     * @apiSuccess {string} depName 所属部门全名
     * @apiSuccess {long} parentId 父任务ID
     * @apiSuccess {string} startNodeName 起始节点名
     * @apiSuccess {long} pubUserId 发布人ID
     * @apiSuccess {string} modelName 任务模型名
     * @apiSuccess {string} currentNodeName 当前节点名
     *
     */
    @Override
    public String onGetListSql(Map<String,Object> params) {
        params.put("uid", AuthFilter.getUid());
        return "workflow.查询任务列表";
    }


    @Override
    public void onBeforeGetOne(SQLManager sqlManager, Object id) {
        //信贷主管

        //是否总行角色

        //是否拥有该任务观察者权限

    }

    @Override
    public JSONObject onGetOne(SQLManager sqlManager, Object id) {
        String msg = "权限校验失败";
        JSONObject object = sqlManager.selectSingle("workflow.查询任务", C.newMap("uid", AuthFilter.getUid(), "id", id), JSONObject.class);
        Assert(null != object, msg);

        //附加所有属性
        List<WfInsAttr> attrs = sqlManager.lambdaQuery(WfInsAttr.class)
            .andEq(WfInsAttr::getInsId, object.getLong("id"))
            .select("uid", "attr_key", "attr_value", "attr_cname", "type","node_id");
        object.put("attrs", attrs);
        //所有处理人
        List<WfNodeDealer> dls = sqlManager.lambdaQuery(WfNodeDealer.class)
            .andEq(WfNodeDealer::getInsId, object.getLong("id"))
            .andNotEq(WfNodeDealer::getType, NOT_DEAL)
            .select();
        object.put("dealers", dls);
        JSONArray nodes = JSON.parseArray(object.getString("nodes"));
        object.put("nodes", nodes);
        return object;
    }
    

    @Override
    public Object onAdd(SQLManager sqlManager) {
        JSONObject innateMap = (JSONObject) get("$innateMap");
        JSONObject model = (JSONObject) get("$model");
        JSONObject node = (JSONObject) get("$node");

        //如果是自动生成，检查X天内是否有一个相同贷款账号且未办理的任务
        if(autoCreated && modelName.contains("贷后跟踪")){
            long count = sqlManager.lambdaQuery(WfIns.class)
                    .andEq(WfIns::getF7, innateMap.getString("CONT_NO"))
                    .andEq(WfIns::getState, DEALING)
                    .andEq(WfIns::getAutoCreated, true)
                    .andLike(WfIns::getModelName, "%贷后跟踪%")
                    .count();
            if(count > 0){
                throw new SameContNoException("同一个贷款账号只生成同一个任务");
            } else {
                f7 = innateMap.getString("CONT_NO");
            }

            //替换为所有合同号的总金额
            List<JSONObject> list = sqlManager.execute(new SQLReady(String.format("select sum(loan_balance) from RPT_M_RPT_SLS_ACCT where cont_no = '%s'", innateMap.getString("CONT_NO"))), JSONObject.class);
            BigDecimal money = list.get(0).getBigDecimal("1");
            innateMap.put("CONT_NO", money);

        }


        //创建节点列表
        JSONArray arr = new JSONArray();
        JSONObject startNode = createNodeIns(node);
        //创建初始节点
        arr.add(startNode);
        currentNodeInstanceId = startNode.getLong("id");
        currentNodeName = startNode.getString("name");
        nodes = JSON.toJSONString(arr);
        sqlManager.insert(this, true);

        //自动绑定
        if(S.notEmpty(loanAccount)){
            InfoLink infoLink = new InfoLink();
            infoLink.setLoanAccount(loanAccount);
            infoLink.setInsId(id);
            sqlManager.insert(infoLink, true);
        }

        List<WfInsAttr> attrs = model.getJSONArray("innates").stream()
            .map(o -> {
                JSONObject innate = (JSONObject) o;
                return createAttribute(id, null, pubUserId, INNATE, innate.getString("ename"), innateMap.getString(innate.getString("ename")), innate.getString("cname"));
            })
//            .filter(item -> S.notEmpty(item.getAttrValue()))
            .collect(toList());
        sqlManager.insertBatch(WfInsAttr.class, attrs);

        //写入处理人
        List<WfNodeDealer> dealers = C.newList();
        //公共任务
        if (Objects.equals(get("$common"), true)) {
            List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人(任务ID)", GPC.class, C.newMap("id", id));
            for (GPC gpc1 : gpcs) {
                dealers.add(
                    new WfNodeDealer(
                        null
                        , CAN_DEAL
                        , id
                        , startNode.getLong("id")
                        , currentNodeName
                        , gpc1.getUid()
                        , gpc1.getUname()
                        , gpc1.getUtname()
                        , gpc1.getOid()
                        , gpc1.getOname()
                        , new Date()
                    )
                );
            }
            sqlManager.insertBatch(WfNodeDealer.class, dealers);
        }
        //指派
        else if (!Objects.equals(dealUserId, pubUserId)) {
            //创建指派
            List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人(任务ID)", GPC.class, C.newMap("id", id));
            for (GPC gpc : gpcs) {
                if(!gpc.getUid().equals(dealUserId)){
                    continue;
                }
                if(dealers.size() > 0){
                    continue;
                }
                dealers.add(
                    new WfNodeDealer(
                        null
                        , CAN_DEAL
                        , id
                        , startNode.getLong("id")
                        , currentNodeName
                        , gpc.getUid()
                        , gpc.getUname()
                        , gpc.getUtname()
                        , gpc.getOid()
                        , gpc.getOname()
                        , new Date()
                    )
                );
            }
            sqlManager.insertBatch(WfNodeDealer.class, dealers);
        }
        //自己执行
        else {
            User user = sqlManager.unique(User.class, pubUserId);
            GPC gpc = (GPC) get("$gpc");
            WfNodeDealer dl = (new WfNodeDealer(null, WfNodeDealer.Type.DID_DEAL, id, startNode.getLong("id"), startNode.getString("name"), user.getId(), user.getUsername(), user.getTrueName(), gpc.getOid(), gpc.getOname(), new Date()));
            sqlManager.insert(dl, true);

            //只有自己执行的时候, 才保存第一个节点的数据
            try {
                submit(sqlManager, this, innateMap, Objects.equals(get("$goNext"), true), (JSONArray) get("$files"), null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }



        return id;
    }


    @Override
    public void onBeforeDelete(SQLManager sqlManager, Long[] id) {
        for (Long aLong : id) {
            Assert(canDelete(AuthFilter.getUid(), aLong), "你没有权限删除这个任务");
        }
    }

    @Override
    public void onAfterDelete(SQLManager sqlManager, Long[] id) {
        for (Long aLong : id) {
            //清空属性表
            sqlManager.lambdaQuery(WfInsAttr.class)
                .andEq(WfInsAttr::getInsId, aLong)
                .delete();
            //清空授权表
            sqlManager.lambdaQuery(WfNodeDealer.class)
                .andEq(WfNodeDealer::getInsId, aLong)
                .delete();
        }
    }


    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        object.put("uid", AuthFilter.getUid());
        switch (action) {
            case "submit":
                try {
                    submit(sqlManager, sqlManager.unique(WfIns.class, object.getLong("id")), object.getJSONObject("data"), object.getBoolean("goNext") == true, object.getJSONArray("files") ,object.getString("password"));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case "daichuli":
                return U.beetlPageQuery("workflow.查询待处理任务", JSONObject.class, object);

            case "yichuli":
                return U.beetlPageQuery("workflow.select已处理任务", JSONObject.class,object);

//            case "gonggong":
//                return U.beetlPageQuery("workflow.查询公共任务列表", JSONObject.class, object);

            case "kejieshou":
                return U.beetlPageQuery("workflow.查询可接受的任务列表", JSONObject.class, object);

            case "zhipaiyijiao":
                object.put("zhipaiyijiao", "123");
                object.put("department", 100);
                return U.beetlPageQuery("workflow.查询任务列表", JSONObject.class, object);

            //可指派/移交的用户
            case "kezhipai":
                return sqlManager.select("workflow.查询节点可处理人(任务ID)", JSONObject.class, object);

            //拒贷记录
            case "judai":
                return U.beetlPageQuery("workflow.查询拒贷记录列表", JSONObject.class, object);

            case "receiveCommon":
                receiveCommon(sqlManager,U.toIdList(object.getString("id")));
                break;

            case "reject":
                reject(sqlManager, U.toIdList(object.getString("id")), U.sGet(object,"info", ""));
                break;

            case "accept":
                accept(sqlManager, U.toIdList(object.getString("id")));
                break;

            //任务的再指派/移交
            case "point":
                point(sqlManager, object.getLong("id"), object.getLong("toUid"));
                break;
            case "batchPoint":
                batchPoint(sqlManager, object.getString("modelIds"), object.getLong("toUid"));
                break;
            case "setP":
                setP(sqlManager, object);
                break;

            case "cancel":
                Long id = object.getLong("id");
                onBeforeDelete(sqlManager, new Long[]{id});
                sqlManager.lambdaQuery(WfIns.class)
                    .andEq(WfIns::getId, id)
                    .updateSelective(new WfIns(){{
                        setState(CANCELED);
                    }});
                break;


            /**
             * 系统管理.任务管理
             * 验证授权
             */
            case "getSUList":
                User.AssertMethod("系统管理.任务管理", "授权失败");
//                Log.log("查找任务管理列表");
                object.put("su", 1);
                return U.beetlPageQuery("workflow.查询任务列表", JSONObject.class, object);


            /**
             * 得到用户对工作流的授权
             * 验证授权
             */
            case "getPerList":
                User.AssertMethod("系统管理.工作流管理", "权限校验失败");
                return sqlManager.select("user.查询授权列表", JSONObject.class, object);
        }
        Log.log("处理任务");
        return null;
    }

    /**
     * 设置权限
     * @param sqlManager
     * @param object
     */
    public void setP(SQLManager sqlManager, JSONObject object){
        sqlManager.lambdaQuery(GP.class)
            .andEq(GP::getObjectId, object.getLong("objectId"))
            .andEq(GP::getType, object.getString("type"))
            .andEq(GP::getK1, object.getString("k1"))
            .delete();
        List<GP> list = U.toIdList(object.getString("oids")).stream()
            .map(i -> {
                GP gp = new GP();
                gp.setK1(object.getString("k1"));
                gp.setObjectId(object.getLong("objectId"));
                gp.setOid(i);
                gp.setType(GP.Type.valueOf(object.getString("type")));
                return gp;
            })
            .collect(toList());
        sqlManager.insertBatch(GP.class, list);
    }



    public synchronized void batchPoint(SQLManager sqlManager,String modelIds,long toUid){
        String [] strList = modelIds.split(",");
        for (int i = 0;i < strList.length; i++){
            WfIns  wfIns = sqlManager.lambdaQuery(WfIns.class)
                    .andEq(WfIns::getId, Long.parseLong(strList[i]))
                    .select(
                            WfIns::getCurrentNodeInstanceId
                            , WfIns::getCurrentNodeName
                            , WfIns::getState
                            , WfIns::getDealUserId
                            , WfIns::getDepId
                    ).get(0);
            if(!wfIns.getState().equals(DEALING)){
                continue;
            }

                //检查是否拥有指派权限
            if(sqlManager.lambdaQuery(DManager.class)
                    .andEq(DManager::getUid, AuthFilter.getUid())
                    .andEq(DManager::getId, wfIns.getDepId())
                    .count() <= 0){
                continue;
            }

                //检查权限
                List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人(任务ID)", GPC.class, C.newMap("id",Long.parseLong(strList[i]), "uid", AuthFilter.getUid()));
                GPC gpc = gpcs.stream().filter(g -> g.getUid().equals(toUid)).findFirst().orElse(null);
                if(null == gpc){
                    continue;
                }
                //更新任务为这个人
                updateTaskBelongs(sqlManager, Long.parseLong(strList[i]), wfIns.getDealUserId(), gpc);

        }
    }

    /**
     * 任务的重新指派
     * @param sqlManager
     * @param id
     * @param toUid
     */
    public synchronized void point(SQLManager sqlManager, long id, long toUid){
            WfIns  wfIns = sqlManager.lambdaQuery(WfIns.class)
                    .andEq(WfIns::getId, id)
                    .select(
                            WfIns::getCurrentNodeInstanceId
                            , WfIns::getCurrentNodeName
                            , WfIns::getState
                            , WfIns::getDealUserId
                            , WfIns::getDepId
                    ).get(0);


            //检查是否拥有指派权限
            Assert(sqlManager.lambdaQuery(DManager.class)
                    .andEq(DManager::getUid, AuthFilter.getUid())
                    .andEq(DManager::getId, wfIns.getDepId())
                    .count() > 0, "权限验证失败");

        //检查权限
        List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人(任务ID)", GPC.class, C.newMap("id",id, "uid", AuthFilter.getUid()));

        GPC gpc = gpcs.stream().filter(g -> g.getUid().equals(toUid)).findFirst().orElse(null);
        Assert(null != gpc, "选中的人没有执行这个任务的权限");

        //更新任务为这个人
        updateTaskBelongs(sqlManager, id, wfIns.getDealUserId(), gpc);

        //以下为指派
        if(true) return;
        int count = sqlManager.lambdaQuery(WfIns.class)
            .andEq(WfIns::getId, id)
            .andIn(WfIns::getState, C.newList(COMMON,UNRECEIVED))
            .updateSelective(new WfIns(){{
                setState(POINT);
                setPointManager(AuthFilter.getUid());
                setPointReceiver(toUid);
            }});

        Assert(count > 0, "指派失败");

        //查找任务权限记录
        sqlManager.lambdaQuery(WfNodeDealer.class)
            .andEq(WfNodeDealer::getInsId,id)
            .delete();
        //写入新的权限
        sqlManager.insert(createDealerFromGPC(CAN_DEAL, id, wfIns.getCurrentNodeInstanceId(), wfIns.getCurrentNodeName(), gpc));

    }

    public void updateTaskBelongs(SQLManager sqlManager, long insId, long sUid, GPC gpc){
        //更新主任务
        sqlManager.lambdaQuery(WfIns.class)
            .andEq(WfIns::getId, insId)
            .updateSelective(new WfIns(){{
                setDealUserId(gpc.getUid());
                setDealUserName(gpc.getUtname());
            }});
        //更新授权表
        sqlManager.lambdaQuery(WfNodeDealer.class)
            .andEq(WfNodeDealer::getInsId, insId)
            .andEq(WfNodeDealer::getUid, sUid)
            .updateSelective(new WfNodeDealer(){{
                setOid(gpc.getOid());
                setOname(gpc.getOname());
                setUid(gpc.getUid());
                setUname(gpc.getUname());
                setUtname(gpc.getUtname());
            }});
        //更新子任务
        List<Long> childIds = sqlManager.lambdaQuery(WfIns.class)
            .andEq(WfIns::getParentId, insId)
            .select(WfIns::getId)
            .stream()
            .map(WfIns::getId)
            .collect(toList());
        for (Long childId : childIds) {
            updateTaskBelongs(sqlManager, insId, sUid, gpc);
        }
    }



    /**
     * 拒绝接受指派/移交
     * @param sqlManager
     * @param ids
     * @param info
     */
    public synchronized void reject(SQLManager sqlManager, List<Long> ids, String info){
        Assert(S.notBlank(info), "请填写拒绝理由!");
        //更新指派为未接受
        List<SysNotice> notices = sqlManager.lambdaQuery(WfIns.class)
            .andIn(WfIns::getId, ids)
            .andEq(WfIns::getPointReceiver, AuthFilter.getUid())
            .andEq(WfIns::getState, POINT)
            .select("point_manager", "id")
            .stream()
            .map(i -> {
                return getNoticeService().makeNotice(
                    SysNotice.Type.WORKFLOW
                    , i.getPointManager()
                    , S.fmt("%s 拒绝接受任务 %d ，拒绝理由：%s", AuthFilter.getUtname(), i.getId(), info)
                    , C.newMap(
                        "taskId", i.getId()
                    )
                );
            })
            .collect(toList());

        sqlManager.lambdaQuery(WfIns.class)
            .andIn(WfIns::getId, ids)
            .andEq(WfIns::getPointReceiver, AuthFilter.getUid())
            .andEq(WfIns::getState, POINT)
            .updateSelective(new WfIns(){{
                setState(UNRECEIVED);
                setPointReceiver(null);
            }});

        //发送拒绝理由
        getNoticeService().saveNotices(notices);

    }


    /**
     * 接受指派/移交
     * @param sqlManager
     * @param ids
     */
    public synchronized void accept(SQLManager sqlManager, List<Long> ids){
        //设置任务为执行中
        sqlManager.lambdaQuery(WfIns.class)
            .andIn(WfIns::getId, ids)
            .andEq(WfIns::getPointReceiver, AuthFilter.getUid())
            .andEq(WfIns::getState, POINT)
            .updateSelective(new WfIns(){{
                setState(DEALING);
                setPointReceiver(null);
                setDealUserId(AuthFilter.getUid());
                setDealUserName(AuthFilter.getUtname());
            }});

    }

    /**
     * 领取公共任务
     * @param sqlManager
     * @param ids
     */
    public synchronized void receiveCommon(SQLManager sqlManager, List<Long> ids){
        User user = sqlManager.unique(User.class, AuthFilter.getUid());
        //把自己标记为可处理的
        List<Long> canids = sqlManager.lambdaQuery(WfNodeDealer.class)
            .andIn(WfNodeDealer::getInsId, ids)
            .andEq(WfNodeDealer::getType, CAN_DEAL)
            .andEq(WfNodeDealer::getUid, user.getId())
            .select("ins_id")
            .stream()
            .map(dl -> dl.getInsId())
            .distinct()
            .collect(toList());
        //检查是否有可执行权限
        Assert(canids.size() > 0, "你没有权限领取该任务");
        //更新任务
        sqlManager.lambdaQuery(WfIns.class)
            .andIn(WfIns::getId, canids)
            .andEq(WfIns::getState, COMMON)
            .updateSelective(C.newMap("dealUserId", user.getId(), "dealUserName", user.getTrueName(), "state", DEALING));

        //更新处理人
        sqlManager.lambdaQuery(WfNodeDealer.class)
            .andIn(WfNodeDealer::getInsId, canids)
            .andEq(WfNodeDealer::getUid, user.getId())
            .andEq(WfNodeDealer::getType, CAN_DEAL)
            .updateSelective(C.newMap("type", DID_DEAL));

        //本次的其他人更新为CANCEL
        sqlManager.lambdaQuery(WfNodeDealer.class)
            .andIn(WfNodeDealer::getInsId, canids)
            .andNotEq(WfNodeDealer::getUid, user.getId())
            .andEq(WfNodeDealer::getType, CAN_DEAL)
            .updateSelective(C.newMap("type", NOT_DEAL));

        Log.log("领取公共任务");

    }

    /**
     * 向一个节点保存数据
     * @param sqlManager
     * @param wfIns
     * @param data
     */
    public void submit(SQLManager sqlManager, WfIns wfIns, JSONObject data, boolean gonext, JSONArray files, String password) throws NoSuchFieldException, IllegalAccessException {
        //数据准备
        Long uid = (Long) get("$uid");
        if(null == uid){
            uid = AuthFilter.getUid();
        }
        JSONObject model = JSON.parseObject(wfIns.getModel());
        JSONArray nodes = JSON.parseArray(wfIns.getNodes());
        String nextNodeName = null;
        boolean waitOthers = false;

        //验证授权
        Assert(null != wfIns && wfIns.getState().equals(State.DEALING), "找不到符合条件的任务");
        Assert(canDealNode(uid,wfIns.getCurrentNodeInstanceId()), "你没有权限提交数据");

        JSONObject node = getNodeModel(model, wfIns.getCurrentNodeName());
        JSONObject nIns = getNodeIns(nodes, wfIns.getCurrentNodeInstanceId());

        //查询除我之外已经确定或者处理的人数
        long now = sqlManager.lambdaQuery(WfNodeDealer.class)
            .andIn(WfNodeDealer::getType, C.newList(WfNodeDealer.Type.DID_DEAL, OVER_DEAL))
            .andEq(WfNodeDealer::getNodeId, nIns.getLong("id"))
            .andNotEq(WfNodeDealer::getUid, uid)
            .groupBy(WfNodeDealer::getUid)
            .count();

        int count = U.sGet(node,"count",1);
        Assert(now + 1 <= count, "该节点处理人已超过配置的最大人数");

        //锁定该任务
        sqlManager.lock(WfIns.class, wfIns.getId());

        //写入数据
        List<WfInsAttr> attrs = C.newList();
        switch (node.getString("type")){
            case "input":
                for (Object content : node.getJSONArray("content")) {
                    JSONObject v = (JSONObject) content;
                    String type = v.getString("type");
                    String attrKey = v.getString("ename");
                    Boolean required = v.getBoolean("required");
                    String value = data.getString(attrKey);
                    //特殊行为，保存提交的抵押物关联
                    if(S.eq(type, "diya") && S.notBlank(value)){
                        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from t_wf_ins_grt where iid = %d", wfIns.id)));
                        Pattern p = Pattern.compile("^.+\\((.+?)\\)$");
                        List<String> strs = Arrays.stream(value.split(","))
                            .map(s -> {
                                Matcher m = p.matcher(s);
                                if(m.find()){
                                    return m.group(1);
                                }
                                return "";
                            })
                            .filter(S::notBlank)
                            .collect(toList());
                        for (String str : strs) {
                            sqlManager.executeUpdate(new SQLReady(S.fmt("insert into t_wf_ins_grt(iid,gid)values(%d,'%s')", wfIns.id, str)));
                        }
                    }
                    //保存草稿不校验
                    if(!gonext && !data.containsKey(attrKey)){
                        continue;
                    }
                    if(gonext && Objects.equals(required,true)){
                        Assert(S.notBlank(value), "%s字段没有填写", v.getString("cname"));
                    }
                    attrs.add(
                        createAttribute(
                            wfIns.getId()
                            , nIns.getLong("id")
                            , uid
                            , WfInsAttr.Type.NODE
                            , attrKey
                            , data.getString(attrKey)
                            , v.getString("cname")
                        )
                    );
                }

                //暂时只在资料节点可以自由跳转
                if(gonext){
                    if(node.containsKey("goNext")){
                        String attrJSON = data.toJSONString();
                        for (Object goNext : node.getJSONArray("goNext")) {
                            JSONObject next = (JSONObject) goNext;
                            String ca = next.getString("case");
                            try {
                                StringBuilder sb = new StringBuilder();
                                sb.append(S.fmt("(function(){ var kv = %s; ", attrJSON));
                                sb.append(S.fmt("with(kv){ return %s; }", ca));
                                sb.append("})()");
                                //处理表达式
                                boolean flag = (boolean) JsEngine.eval(sb.toString());
                                if (flag) {
                                    nextNodeName = next.getString("go");
                                    break;
                                }
                            } catch (Exception e){
                                //任何错误, 走下一步
                                continue;
                            }
                        }
                    }
                    else{
                        try{
                           Object next = node.get("next");
                           if(next instanceof JSONArray){
                               nextNodeName = (String) ((JSONArray) next).get(0);
                           }
                           else if(next instanceof String){
                               nextNodeName = ((String) next).split(",")[0];
                           }
                        }
                        catch (Exception e){

                        }
                    }
                }
                break;

            case "check":
                String key = U.sGet(node, "key", "key");
                String ps = U.sGet(node, "ps", "ps");
                String keyV = data.getString(key);
                String psV = U.sGet(data, ps, "");
                Assert(
                    node.getJSONArray("states").stream()
                        .map(o -> (JSONObject)o)
                        .anyMatch(obj -> S.eq(obj.getString("item"), keyV))
                    , "选项内没有这个选项"
                );
                attrs.add(
                    createAttribute(
                        wfIns.getId()
                        , nIns.getLong("id")
                        , uid
                        , WfInsAttr.Type.NODE
                        , key
                        , keyV
                        , U.sGet(node, "question", "审批选项")
                    )
                );
                attrs.add(
                    createAttribute(
                        wfIns.getId()
                        , nIns.getLong("id")
                        , uid
                        , WfInsAttr.Type.NODE
                        , ps
                        , psV
                        , "审批意见"
                    )
                );

                if(gonext){
                    Assert(S.notEmpty(U.sGet(data,key)), "你还没有提交信息, 无法提交下一步");
                    //查出这个节点所有人的提交
                    JSONObject state = node.getJSONArray("states")
                        .stream()
                        .map(o -> (JSONObject)o)
                        .filter(s -> {
                            return sqlManager.lambdaQuery(WfInsAttr.class)
                                .andEq(WfInsAttr::getInsId, wfIns.getId())
                                .andEq(WfInsAttr::getNodeId, nIns.getLong("id"))
                                .andEq(WfInsAttr::getAttrKey, key)
                                .andEq(WfInsAttr::getAttrValue, s.getString("item"))
                                .count() + (S.eq(keyV, s.getString("item")) ? 1 : 0) >= s.getInteger("condition");
                        })
                        .findFirst()
                        .orElse(null);
                    if(null == state){
                        waitOthers = true;
                    }
                    else{
                        nextNodeName = U.sGet(state, "go", "");
                    }
                }
                break;

        }
        sqlManager.lambdaQuery(WfInsAttr.class)
            .andEq(WfInsAttr::getInsId, wfIns.getId())
            .andEq(WfInsAttr::getNodeId, nIns.getLong("id"))
            .andEq(WfInsAttr::getUid, uid)
            .delete();
        sqlManager.insertBatch(WfInsAttr.class, attrs);

        //写入处理人
        List<WfNodeDealer> dls = sqlManager.lambdaQuery(WfNodeDealer.class)
            .andEq(WfNodeDealer::getInsId, wfIns.getId())
            .andEq(WfNodeDealer::getNodeId, nIns.getLong("id"))
            .andEq(WfNodeDealer::getType, WfNodeDealer.Type.CAN_DEAL)
            .select();
        List<Long> noticeUids = C.newList();
        for (WfNodeDealer dl : dls) {
            if(Objects.equals(dl.getUid(), uid)){
                dl.setType(WfNodeDealer.Type.DID_DEAL);
                dl.setLastModify(new Date());
            }
            else if(now + 1 >= count){
                dl.setType(WfNodeDealer.Type.NOT_DEAL);
                dl.setLastModify(new Date());
                noticeUids.add(dl.getUid());
            }
            else{
                //pass
            }
        }
        sqlManager.updateByIdBatch(dls);

        //保存文件
        if(C.notEmpty(files)){
            nIns.getJSONArray("files").clear();
            nIns.getJSONArray("files").addAll(files);
        }

        //保存固有字段
        //保存自定义的固有字段
        String[] custom = {"CUS_NAME","PHONE","CERT_TYPE","CERT_CODE"};
        bindCustomFields(custom,data);

        //异步发送消息
        List<SysNotice> notices = C.newList();
        NoticeService2 noticeService2 = getNoticeService();
        notices.addAll(
            noticeService2.makeNotice(
                SysNotice.Type.WORKFLOW,
                noticeUids,
                S.fmt("你的任务 %s 节点 %s 已被别人处理", wfIns.getTitle(), wfIns.getCurrentNodeName()),
                C.newMap(
                    "taskId", wfIns.getId(),
                    "taskName", wfIns.getTitle(),
                    "nodeId", nIns.getLong("id"),
                    "nodeName", nIns.getString("name")
                )
            )
        );
        noticeService2.saveNotices(notices);

        //gonext
        if(!gonext){
            wfIns.setNodes(JSON.toJSONString(nodes));
            sqlManager.updateById(wfIns);
            return;
        }

        //主流程结束需要输入密码
        if(S.eq(wfIns.modelName, "不良资产主流程")){
            if(S.empty(password)) password = "";
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            Assert(
                sqlManager.lambdaQuery(User.class).andEq(User::getId, uid).andEq(User::getPassword, password).count() > 0
                , "密码错误, 无法继续操作"
            );
        }

        //如果是按揭贷后任务
        if (wfIns.modelName.contains("按揭")) {
            LoanManager loanManager = sqlManager.lambdaQuery(LoanManager.class)
                .andEq(LoanManager::getLoanAccount, wfIns.loanAccount)
                .single();
            if (loanManager == null) {
                loanManager = new LoanManager();
            }
            //写回loan_manager
            for (WfInsAttr attr : attrs) {
                switch (attr.getAttrKey()){
                    case "FCZ":
                        if(attr.getAttrValue().equals("已出证")){
                            loanManager.setFcz("1");
                        } else {
                            loanManager.setFcz("0");
                        }
                        break;

                    case "FCZ_DATE":
                        try{
                            loanManager.setFczDate(DateUtil.parse(attr.attrValue, "yyyy-MM-dd hh:mm:ss"));
                        }
                        catch (Exception e){
                        }
                        break;

                    case "MMHTJYRQ_DATE":
                        try{
                            loanManager.setMmhtjyrqDate(DateUtil.parse(attr.attrValue, "yyyy-MM-dd hh:mm:ss"));
                        }
                        catch (Exception e){}
                        break;
                    case "DHJYZXQK":
                        try{
                            loanManager.setDshjyzxqk(attr.getAttrValue());
                        }
                        catch (Exception e){}
                        break;

                }

            }

            if (loanManager.getId() == null) {
                loanManager.setLoanAccount(wfIns.loanAccount);
                sqlManager.insert(loanManager);
            } else {
                sqlManager.updateById(loanManager);
            }
        }

        //标记为已处理
        sqlManager.lambdaQuery(WfNodeDealer.class)
            .andEq(WfNodeDealer::getUid, uid)
            .andEq(WfNodeDealer::getInsId, wfIns.getId())
            .andEq(WfNodeDealer::getNodeId, nIns.getLong("id"))
            .updateSelective(C.newMap(
                "type",  OVER_DEAL
            ));

        //如果需要等待别人
        if(waitOthers){
            return;
        }

        Assert(S.notEmpty(nextNodeName), "没有合适的节点跳转, 请联系管理员");
        JSONObject nextNode = getNodeModel(model, nextNodeName);
        //保存处理完毕时间
        nIns.put("dealDate", new Date());
        //创建新节点
        JSONObject nNIns = createNodeIns(nextNode);

        //处理结束的行为
        if(S.eq(nextNode.getString("type"), "end")){
            wfIns.setFinishedDate(new Date());
            wfIns.setState(State.FINISHED);

            //关闭所有子流程
            sqlManager.lambdaQuery(WfIns.class)
                .andEq(WfIns::getParentId, wfIns.getId())
                .updateSelective(new WfIns(){{
                    setState(CANCELED);
                }});

        }
        //如果之前的节点中出现了和这个同名的节点, 那么需要回滚数据
        else if(S.eq(nextNode.getString("type"), "input")){
            int len = nodes.size();
            while(len-- > 0){
                JSONObject his = nodes.getJSONObject(len);
                if(S.eq(his.getString("name"), nextNodeName)){
                    //重新写入所有属性
                    List<WfInsAttr> hisAttrs = sqlManager.lambdaQuery(WfInsAttr.class)
                        .andEq(WfInsAttr::getInsId, wfIns.getId())
                        .andEq(WfInsAttr::getNodeId, his.getLong("id"))
                        .select()
                        .stream()
                        .map(item -> {
                            item.setId(null);
                            item.setNodeId(nNIns.getLong("id"));
                            return item;
                        })
                        .collect(toList());
                    sqlManager.insertBatch(WfInsAttr.class, hisAttrs);
                    break;
                }
            }
        }

        //添加到节点列表
        nodes.add(nNIns);
        wfIns.setNodes(JSON.toJSONString(nodes));
        wfIns.setCurrentNodeInstanceId(nNIns.getLong("id"));
        wfIns.setCurrentNodeName(nextNodeName);



        //异步发送处理消息
        notices = C.newList();
        switch (nextNode.getString("type")){
            case "input":
            case "check":
                List<GPC> gpcs = getNodeDealUids(sqlManager, wfIns, nextNode);
                List<WfNodeDealer> dealers = C.newList();
                List<Long> toUids = C.newList();
                for (GPC gpc : gpcs) {
                    WfNodeDealer dl = new WfNodeDealer(
                        null
                        , gpc.getId().equals(-1) ? DID_DEAL : CAN_DEAL
                        , wfIns.getId()
                        , nNIns.getLong("id")
                        , nextNodeName
                        , gpc.getUid()
                        , gpc.getUname()
                        , gpc.getUtname()
                        , gpc.getOid()
                        , gpc.getOname()
                        , new Date()
                    );
                    dealers.add(dl);
                    if(!gpc.getId().equals(-1)){
                        toUids.add(gpc.getUid());
                    }
                }
                sqlManager.insertBatch(WfNodeDealer.class, dealers);
                notices.addAll(
                    noticeService2.makeNotice(
                        SysNotice.Type.WORKFLOW
                        , toUids
                        , S.fmt("你有新的任务 %s 节点 %s 可以处理", wfIns.getTitle(), nextNodeName)
                        , C.newMap(
                            "taskId", wfIns.getId(),
                            "taskName", wfIns.getTitle(),
                            "nodeId", nNIns.getLong("id"),
                            "nodeName", nNIns.getString("name")
                        )
                    )
                );
                break;

            case "end":
                notices.add(
                    noticeService2.makeNotice(
                        SysNotice.Type.WORKFLOW
                        , wfIns.getDealUserId()
                        , String.format("你的任务 %s 已经结束", wfIns.getTitle())
                        , C.newMap(
                        "taskId", wfIns.getId()
                            ,"taskName", wfIns.getTitle()
                        )
                    )
                );
                break;
        }

        sqlManager.updateById(wfIns);


        //处理当前节点的结束行为
        if(node.containsKey("behavior")){
            new Thread(() -> {
                JSONArray behaviors = node.getJSONArray("behavior");
                for (Object o : behaviors) {
                    JSONArray behavior = (JSONArray) o;
                    String method = null;
                    if(o instanceof String){
                        callMethod(sqlManager, wfIns.getId(), method, null);
                    }
                    else if(o instanceof JSONArray){
                        JSONArray args = new JSONArray();
                        args.addAll(((JSONArray) o).subList(1, ((JSONArray) o).size()));
                        callMethod(
                            sqlManager
                            , wfIns.getId()
                            , ((JSONArray)o).getString(0)
                            , args
                        );
                    }
                }
            }).start();
        }
//        List<Long> uids = sqlManager.lambdaQuery(WfInsAttr.class)
//            .andEq(WfInsAttr::getInsId, wfIns.getId())
//            .andEq(WfInsAttr::getNodeId, nIns.getLong("id"))
//            .andEq(WfInsAttr::getType, WfInsAttr.Type.NODE)
//            .select("uid")
//            .stream()
//            .map(WfInsAttr::getUid)
//            .distinct()
//            .collect(toList());
//        notices.addAll(
//            noticeService2.makeNotice(
//                SysNotice.Type.WORKFLOW
//                , uids
//                , S.fmt("你已处理完毕任务 %s 节点"))
//        )


        //



//
//        switch (node.getString("type")){
//            case "input":
//                for (Object content : node.getJSONArray("content")) {
//                    JSONObject v = (JSONObject) content;
//                }
//                break;
//            case "check":
//                break;
//        }

    }

    /**
     * 获取一个节点的可处理人
     *
     * @param sqlManager
     * @param ins
     * @param node
     * @return
     */
    public List<GPC> getNodeDealUids(SQLManager sqlManager, WfIns ins, JSONObject node){
        List<GPC> gpcs = sqlManager.select("workflow.查询节点可处理人(任务ID)", GPC.class, C.newMap("id", ins.getId(), "name", node.getString("name")));

        //如果该任务只有一个人可以处理
        if(1 == U.sGet(node, "count", 1)){
            //查询同名节点的可处理人
            WfNodeDealer dl = sqlManager.lambdaQuery(WfNodeDealer.class)
                .andEq(WfNodeDealer::getInsId,ins.getId())
                .andEq(WfNodeDealer::getType, OVER_DEAL)
                .single();
            //必定只有一个
            if(null != dl){
                List<GPC> newGpcs = gpcs.stream()
                    .filter(item -> item.getUid().equals(dl.getUid()))
                    .peek(item -> item.setId(-1L))
                    .collect(toList());
                if(newGpcs.size() > 0){
                    gpcs = newGpcs;
                }
            }
        }
        return gpcs;
    }

    /*****/

    /**
     * 是否可以处理这个节点
     * @param uid
     * @param niid
     * @return
     */
    private boolean canDealNode(long uid, long niid) {
        Map map = U.getSQLManager().selectSingle("workflow.canDealNode", C.newMap("niid", niid, "uid", uid), Map.class);
        return (int) map.getOrDefault("1", 0) > 0;
    }


    /**
     * 检索对应的节点模型
     * @param model
     * @param name
     * @return
     */
    private JSONObject getNodeModel(JSONObject model, String name) {
        JSONObject node = model.getJSONArray("flow").stream()
            .map(o -> (JSONObject) o)
            .filter(o -> S.eq(o.getString("name"), name))
            .findFirst()
            .orElseThrow(new RestException("找不到名为%s的节点模型",name));
        return node;
    }

    /**
     * 检索对应的节点实例
     * @param nodes
     * @param id
     * @return
     */
    private JSONObject getNodeIns(JSONArray nodes, long id){
        return nodes.stream()
            .map(o -> (JSONObject)o)
            .filter(item -> Objects.equals(item.getLong("id"), id))
            .findFirst()
            .orElseThrow(new RestException("找不到ID为%d的节点实例", id));
    }


    /**
     * 根据模型创建节点实例
     * @param object
     * @return
     */
    private JSONObject createNodeIns(JSONObject object) {
        JSONObject nodeIns = new JSONObject();
        nodeIns.put("id", U.getSnowflakeIDWorker().nextId());
        nodeIns.put("name", object.getString("name"));
        nodeIns.put("files", new JSONArray());
        nodeIns.put("addTime", new Date());
        nodeIns.put("type", object.getString("type"));
        return nodeIns;
    }

    /**
     * 创建属性
     * @param instanceId
     * @param nInsId
     * @param uid
     * @param type
     * @param key
     * @param value
     * @param cname
     * @return
     */
    private WfInsAttr createAttribute(long instanceId, Long nInsId, long uid, WfInsAttr.Type type, String key, String value, String cname) {
        WfInsAttr attr = new WfInsAttr();
        attr.setUid(uid);
        attr.setAttrKey(key);
        attr.setAttrValue(value);
        attr.setAttrCname(cname);
        attr.setType(type);
        attr.setInsId(instanceId);
        attr.setNodeId(nInsId);
        return attr;
    }

    /**
     * 通过授权创建执行人记录
     * @param type
     * @param insId
     * @param nodeId
     * @param nodeName
     * @param gpc
     * @return
     */
    private WfNodeDealer createDealerFromGPC(WfNodeDealer.Type type, long insId, long nodeId, String nodeName, GPC gpc){
        return new WfNodeDealer(
            null
            , type
            , insId
            , nodeId
            , nodeName
            , gpc.getUid()
            , gpc.getUname()
            , gpc.getUtname()
            , gpc.getOid()
            , gpc.getOname()
            , new Date()
        );
    }

    /**
     * 绑定模型的自定义字段
     *
     * @param keys
     * @param data
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void bindCustomFields(String[] keys, JSONObject data) throws NoSuchFieldException, IllegalAccessException {
        for(int i = 0; i < keys.length; i++){
            if(S.notEmpty(data.getString(keys[i]))){
                Field field = getClass().getDeclaredField("f" + (i + 1));
                field.setAccessible(true);
                field.set(this, data.getString(keys[i]));
            }
        }
    }


    /**
     * 工作流自定义行为
     *
     * @param sqlManager
     * @param insId
     * @param methodName
     * @param args
     */
    private void callMethod(SQLManager sqlManager, long insId, String methodName, JSONArray args){
        switch (methodName){
            case "newTask":
                WfIns wfIns = sqlManager.unique(WfIns.class, insId);
                List<WfInsAttr> attrs = sqlManager.lambdaQuery(WfInsAttr.class)
                    .andEq(WfInsAttr::getInsId, insId)
                    .andEq(WfInsAttr::getType, INNATE)
                    .select();
                JSONObject atm = new JSONObject();
                for (WfInsAttr attr : attrs) {
                    atm.put(attr.getAttrKey(), attr.getAttrValue());
                }
                WfIns ins = new WfIns();
                ins.modelName = args.getString(0);
                ins.title = modelName;
                ins.dealUserId = ins.pubUserId = wfIns.getDealUserId();
                //保存关联
                ins.prevInstanceId = wfIns.id;

                ins.set("$data", atm);
                ins.set("$goNext", false);
                ins.set("$uid", ins.dealUserId);
                valid(ins, Add.class);
                ins.onAdd(sqlManager);
                //
                break;
        }
    }


    /**
     * 检查一个用户是否可以管理这个任务(是我自己的任务或者为任务所属部门主管)
     * @param uid
     * @param insId
     * @return
     */
    public static boolean canManage(long uid, long insId){
        return U.assertFromSql("workflow.用户是否可以管理这个任务", C.newMap("uid",uid, "iid",insId));
    }

    /**
     * 检查一个用户是否可以删除这个任务(是我自己的任务)
     * @param uid
     * @param insId
     * @return
     */
    public static boolean canDelete(long uid, long insId){
        return U.assertFromSql("workflow.用户是否可以删除任务", C.newMap("uid", uid, "id", insId));
    }


    //工具函数

    private static NoticeService2 $noticeService2;
    private static NoticeService2 getNoticeService(){
        if(null == $noticeService2){
            $noticeService2 = U.getBean(NoticeService2.class);
        }
        return $noticeService2;
    }


    public static class SameContNoException extends RuntimeException{
        private String _msg;

        public SameContNoException(String _msg) {
            this._msg = _msg;
        }
    }

}
