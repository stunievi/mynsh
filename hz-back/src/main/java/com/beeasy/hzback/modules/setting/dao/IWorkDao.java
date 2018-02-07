package com.beeasy.hzback.modules.setting.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.setting.entity.Work;
//import com.beeasy.hzback.modules.setting.entity.WorkNode;
import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;
import com.beeasy.hzback.modules.setting.work_engine.ShenheNode;
import com.beeasy.hzback.modules.setting.work_engine.ZiliaoNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


public interface IWorkDao extends JpaRepository<Work, Integer> {


    @Transactional
    default boolean updateNodeList(Work work, String list) throws RuntimeException {
//        IWorkNodeDao workNodeDao = (IWorkNodeDao) SpringContextUtils.getBean(IWorkNodeDao.class);
        if (work.getId() > 0) {
            Work target = this.findOne(work.getId());
            if (target == null) {
                return false;
            }
        }
//
        //删除附属节点
        /**
         * 注：后续需要涉及到业务模型的升级，所以这里不能简单的删除，而是区分版本进行控制，待所有的业务升级完毕后，方可删除
         * 非常重要！！！！！！！！！！！！
         */
//        workNodeDao.deleteAllByWork(work);
        List<BaseWorkNode> nodeList;
        try {
            JSONArray arr = JSON.parseArray(list);
            nodeList = arr.stream().map(o -> {
                JSONObject it = (JSONObject) o;
                return it.getString("type").equals("shenhe") ? it.toJavaObject(ShenheNode.class) : it.toJavaObject(ZiliaoNode.class);
            }).collect(Collectors.toList());

//            for(Object item : arr){
//                JSONObject it = (JSONObject)item;
//                BaseWorkNode workNode;
//
////                workNode.setType(it.getString("type"));
//                if(it.getString("type").equals("shenhe")){
//                    workNode = it.toJavaObject(ShenheNode.class);
//                }
//                else{
//                    workNode = it.toJavaObject(ZiliaoNode.class);
//                }
//                nodeList.add(workNode);
//            }
        } catch (Exception e) {
            return false;
        }
        work.setNodeList(nodeList);
        Work result = this.save(work);
        if (result.getId() == null || result.getId() <= 0) {
            return false;
        }
        return true;

    }

}
