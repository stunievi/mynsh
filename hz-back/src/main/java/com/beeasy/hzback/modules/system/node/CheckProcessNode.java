package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.modules.system.entity_kt.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CheckProcessNode extends CheckNode{

    //关联任务ID
    private String id;
    //关联任务处理结果
    private String result;

    public CheckProcessNode(Map v) {
        super(v);

//        if(v.containsKey("id")){
//            id = String.valueOf(v.get("id"));
//        }
//        if(v.containsKey("result")){
//            result = String.valueOf(v.get("result"));
//        }
    }


    @Override
    public void submit(User user, WorkflowNodeInstance wNInstance, Map data) {
        super.submit(user, wNInstance, data);

        //补充私有字段
        if(data.containsKey(getId())){
            addAttribute(user,wNInstance,getId(),String.valueOf(data.get("id")));
        }
        if(data.containsKey(getResult())){
            addAttribute(user,wNInstance,getResult(),String.valueOf(data.get("result")));
        }

    }
}
