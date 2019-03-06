package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.entity.BeetlPager;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.Table;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Table(name = "T_SYSTEM_VARIABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysVar extends TailBean implements ValidGroup {
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

    @Override
    public Object onGetList(SQLManager sqlManager, Map<String, Object> params) {
        //直接返回全部
        Map map = C.newMap();
        List<SysVar> list = sqlManager.lambdaQuery(SysVar.class)
            .select();
        for (SysVar sysVar : list) {
            map.put(sysVar.getVarName(), sysVar.getVarValue());
        }
        return map;
    }


    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){

            /**
             * 设置系统变量
             */
            case "set":
                User.AssertMethod("系统管理.系统设置.系统设置", "实验室功能.系统更新");

                sqlManager.lambdaQuery(SysVar.class)
                    .andIn(SysVar::getVarName, object.keySet())
                    .delete();
                List<SysVar> vars = object.entrySet().stream()
                    .map(item -> new SysVar(null,item.getKey(), (String)item.getValue(), true))
                    .collect(Collectors.toList());
                //补强: 如果有这个字段, 那么先删除所有有这个字段的
                if(vars.stream().anyMatch(v -> v.getVarName().startsWith("ACC_"))){
                    sqlManager.lambdaQuery(SysVar.class)
                        .andLike(SysVar::getVarName, "ACC_%")
                        .delete();
                }
                sqlManager.insertBatch(SysVar.class, vars);
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
