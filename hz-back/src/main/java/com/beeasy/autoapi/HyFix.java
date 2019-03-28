package com.beeasy.autoapi;

import com.alibaba.druid.pool.DruidDataSource;
import com.beeasy.hzback.entity.GP;
import com.beeasy.hzback.entity.Org;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.entity.WfModel;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.$;
import org.osgl.util.S;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HyFix {

    public static void deal(String db){
        SQLManager hy = getSqlmanager("fzsys_hy");
        SQLManager dy = getSqlmanager(String.format("fzsys_%s", db));

        List<GP> needToInsert = new ArrayList<>();
        List<WfModel> oldModels = hy.lambdaQuery(WfModel.class)
                .select();
        for (WfModel oldModel : oldModels) {
            //被替换的对象
           List<WfModel> newModels = hy.lambdaQuery(WfModel.class)
                    .andEq(WfModel::getModelName, oldModel.getModelName())
                    .select();

            if (newModels.size() == 0) {
                log.info(S.fmt("找不到模型 %s", oldModel.getModelName()));
                continue;
            }

            for (WfModel newModel : newModels) {
                log.info(S.fmt("正在处理%s %s", db, newModel.getModelName()));

                //清理节点
                dy.lambdaQuery(GP.class)
                        .andEq(GP::getObjectId, newModel.getId())
                        .andEq(GP::getType, GP.Type.WORKFLOW_MAIN_QUARTER)
                        .delete();

                //查询所有下属节点
                List<GP> oldgps = hy.lambdaQuery(GP.class)
                        .andEq(GP::getObjectId, oldModel.getId())
                        .andEq(GP::getType, GP.Type.WORKFLOW_MAIN_QUARTER)
                        .select();

                //通过oid查找类似的岗位
                for (GP oldgp : oldgps) {
                    Org o = hy.single(Org.class, oldgp.getOid());
                    if (o == null) {
                        continue;
                    }
                    //查询类似的岗位名
                    LambdaQuery<Org> query = dy.lambdaQuery(Org.class)
                            .andLike(Org::getName, o.getName())
                            .orLike(Org::getName, o.getName().replaceAll("[\\(（].+?[\\)）]", ""));
                    if(o.getName().indexOf("行长") > -1){
                        query.orLike(Org::getName, "%行长%");
                    }
                    if(o.getName().indexOf("审") > - 1){
                        query.orLike(Org::getName, "%审%");
                    }
                    if(o.getName().indexOf("主管") > -1){
                        query.orLike(Org::getName, "%主管%");
                    }
                    List<Org> matchedOrgs = query.select();
                    for (Org matchedOrg : matchedOrgs) {
                        GP cp = $.map(oldgp).to(GP.class);
                        cp.setId(null);
                        cp.setObjectId(newModel.getId());
                        cp.setOid(matchedOrg.getId());
//                    if(needToInsert.size() > 20){
//                        dy.insertBatch(GP.class, needToInsert);
//                        needToInsert.clear();
//                    }
//                    needToInsert.add(cp);
                        try{
                            dy.insert(cp);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

            }

        }
//        dy.insertBatch(GP.class, needToInsert);
        log.info("success");
    }

    public static void main(String[] args){
        String[] dbs = {"dy","lp","hp","zj","lc"};
        for (String db : dbs) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deal(db);
                }
            }).start();
        }
    }


    public static SQLManager getSqlmanager(String db){
        DataSource dataSource = DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .driverClassName("com.ibm.db2.jcc.DB2Driver")
                .url(S.fmt("jdbc:db2://172.25.196.1:3035/%s", db))
                .username("db2inst1")
                .password("11111111")
                .build();
       ConnectionSource source = ConnectionSourceHelper.getSingle(dataSource);
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        SQLLoader loader = new ClasspathLoader("/sql");
        return new SQLManager(new DB2SqlStyle(),loader,source,nc,new Interceptor[]{});
    }
}
