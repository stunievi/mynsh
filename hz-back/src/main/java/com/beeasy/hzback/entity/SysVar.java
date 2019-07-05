package com.beeasy.hzback.entity;

import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Table(name = "T_SYSTEM_VARIABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysVar extends ValidGroup {
    @AssignID("simple")
    Long id;
    String varName;
    String varValue;
    Boolean canDelete = true;


    /****/
    @Override
    public String onGetListSql(Map<String,Object> params) {
        return null;
    }


    /**
     * @api {get} {辅助系统地址}/api/auto/sysvar/getList 系统变量列表
     * @apiGroup FZSYS
     * @apiVersion 0.0.1
     * @apiUse FzCommon
     *
     * @apiSuccess {string} sys_name 系统名
     * @apiSuccess {string} sys_des 系统描述
     * @apiSuccess {string} msg_api_open 是否开启短信网关
     * @apiSuccess {string} MSG_RULE_x 消息规则参数，x为消息规则几，通常用来保存发送消息的触发时间
     * @apiSuccess {string} MSG_RULE_x_ON 消息规则参数，x为消息规则几，通常用来保存是否开启该规则
     * @apiSuccess {string} MSG_RULE_x_TMPL 消息规则参数，x为消息规则几，通常用来保存消息对应的消息模板
     * @apiSuccess {string} ACC_x_BIZ_TYPE 任务自动生成参数，x为生成规则几，通常用来保存产品的编号
     * @apiSuccess {string} ACC_x_EXPECT_DAY 任务自动生成参数，x为生成规则几，通常用来保存预提醒时间（天）
     * @apiSuccess {string} ACC_x_LOAN_CHECK 任务自动生成参数，x为生成规则几，通常用来保存检查时间间隔（月）
     * @apiSuccess {string} ACC_x_LOAN_AMOUNT_MIN 任务自动生成参数，x为生成规则几，通常用来保存贷款额度(元）最小值
     * @apiSuccess {string} ACC_x_LOAN_AMOUNT_MAX 任务自动生成参数，x为生成规则几，通常用来保存贷款额度(元）最大值
     *
     *
     */
    @Override
    public Object onGetList(SQLManager sqlManager, Map<String, Object> params) {
        //直接返回全部
        Map map = C.newMap();
        List<SysVar> list = sqlManager.lambdaQuery(SysVar.class)
                .select();
        for (SysVar sysVar : list) {
            map.put(sysVar.getVarName(), sysVar.getVarValue());
        }
//        Log.log("查询系统环境变量");
        return map;
    }


    /**
     * @api {post} {辅助系统地址}/api/auto/sysvar/set 设置系统变量
     * @apiGroup FZSYS
     * @apiVersion 0.0.1
     * @apiDescription 该接口使用JSONPOST一个object，object的key为变量名，value为变量值，变量名参考getList当中的变量名，无返回错误信息就视为已经成功
     * @apiUse FzCommon
     *
     * @apiHeader {String} content-type applicaton/json; charset=utf-8
     *
     */

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){

            /**
             * 设置系统变量
             */
            case "set":
                User.AssertMethod("系统管理.系统设置.系统设置", "实验室功能.系统更新");
                if(object.size() > 0){
                    sqlManager.lambdaQuery(SysVar.class)
                            .andIn(SysVar::getVarName, object.keySet())
                            .delete();
                }
                List<SysVar> vars = object.entrySet().stream()
                        .map(item -> new SysVar(null,item.getKey(), (String)item.getValue(), true))
                        .collect(Collectors.toList());
                //补强: 如果有这个字段, 那么先删除所有有这个字段的
                if(vars.stream().anyMatch(v -> v.getVarName().startsWith("ACC_"))){
                    sqlManager.lambdaQuery(SysVar.class)
                            .andLike(SysVar::getVarName, "ACC_%")
                            .delete();
                }

                for (SysVar var : vars) {
                    if(var.getVarName().equals("file_white_list")){
//                        File file = new File("hz-back/src/main/resources/whiteList.txt");
                        RandomAccessFile raf =null;
                        // 获取属性配制文件中的值
                        Environment env = U.getBean(Environment.class);
                        String path = env.getProperty("filepath.whitelistpath");
                        try {
                            File file = new File(path);
                            if(file.exists()){
                                file.delete();
                            }
//                            file.createNewFile();
                            file.getParentFile().mkdirs();
                            String value ="";
                            if(S.isNotBlank(var.getVarValue())){
                                value = var.getVarValue()+",";
                            }
                            raf = new RandomAccessFile(file, "rw");
                            raf.write(value.replace("，",",").replaceAll(" ", "").getBytes(CharsetUtil.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(null != raf){
                                    raf.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                sqlManager.insertBatch(SysVar.class, vars);
                Log.log("设置系统环境变量");
                break;

            /**
             * 得到系统版本
             * 开放所有权限
             */
            case "getDataVersion":
                JSONObject info = new JSONObject();
                info.put("sysName", sqlManager.lambdaQuery(SysVar.class).andEq(SysVar::getVarName, "sys_name").select(SysVar::getVarValue).stream().map(SysVar::getVarValue).findFirst().orElse(null));
                List<JSONObject> objs = sqlManager.execute(new SQLReady("SELECT src_sys_date FROM RPT_M_RPT_SLS_ACCT FETCH FIRST 1 ROWS ONLY"),JSONObject.class);
                try{
                    info.put("dataVersion",objs.get(0).getString("srcSysDate"));
                }
                catch (Exception e){
                }
//                Log.log("查询系统数据版本");
                return info;


            /**
             * 统计开放所有权限
             */
            case "loanStat":
                return sqlManager.selectSingle("accloan.233", C.newMap("uid", AuthFilter.getUid()),JSONObject.class);
            case "taskStat":
                return sqlManager.select("workflow.查询任务统计",JSONObject.class , C.newMap("uid", AuthFilter.getUid()));

        }

        return null;
    }


    /**
     * 得到单独的系统配置
     * 开放所有权限
     * @param sqlManager
     * @param object
     * @return
     */
    public Object getConfig(SQLManager sqlManager, JSONObject object){
        List<String> keys = U.toList(object.getString("key"),String.class)
                .stream()
                .filter(S::notBlank)
                .distinct()
                .collect(Collectors.toList());
        JSONObject map = new JSONObject();
        for (SysVar item : sqlManager.lambdaQuery(SysVar.class)
                .andIn(SysVar::getVarName, keys)
                .select()) {
            map.put(item.getVarName(), item.getVarValue());
        }
        return map;
    }
}
