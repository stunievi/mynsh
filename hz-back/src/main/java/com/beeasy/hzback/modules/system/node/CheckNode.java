package com.beeasy.hzback.modules.system.node;

import com.alibaba.druid.util.StringUtils;
import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;

import java.util.LinkedHashMap;
import java.util.Map;

//import org.beetl.core.misc.NumberUtil;

@Getter
@Setter
public class CheckNode extends BaseNode{

    String question;
    String key = "key";
    String ps = "ps";
    int count = 1;
//    Content content = new Content();
    private Map<String,State> states = new LinkedHashMap();


    public CheckNode(String name, Map<String,Object> v) {
        super(name, "check", v);

        if (v.containsKey("count")) {
            setCount(NumberUtils.toInt(String.valueOf(v.get("count"))));
        }
        if (v.containsKey("ps")) {
            setPs(String.valueOf(v.get("ps")));
        }
        if (v.containsKey("key")) {
            setKey(String.valueOf(v.get("key")));
        }
        if (v.containsKey("question")) {
            setQuestion(String.valueOf(v.get("question")));
        }

        //状态机
        if (v.containsKey("states")) {
            ((Map<String, Map>) (v.get("states"))).forEach((kk, vv) -> {
                CheckNode.State state = new CheckNode.State(
                        String.valueOf(vv.get("item")),
                        (Integer) vv.get("condition"),
                        String.valueOf(vv.get("behavior"))
                );
                getStates().put(kk, state);
            });
        }
    }



    @Override
    public void submit(User user, WorkflowNodeInstance wNInstance, Map data) {
        String item = String.valueOf(data.get(getKey()));
        String ps = String.valueOf(data.get(getPs()));
        //如果可选项不在选项里面, 那么无视
        boolean hasOption = getStates().entrySet()
                .stream()
                .filter(entry -> entry.getValue().getItem().equals(item))
                .count() > 0;
        if (!hasOption) {
            return;
        }
        //每个审批节点只允许审批一次
        addNode(user,wNInstance,getKey(),item);

        //如果填写了审核说明
        if (!StringUtils.isEmpty(ps)) {
            addNode(user,wNInstance,getPs(),ps);
        }
    }


    //    @Getter
//    @Setter
//    public static class Content extends AbstractBaseEntity{
////        Set<String> items;
////        String passItem;
//
////        int pass = 1;
////        int fail = 1;
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class State extends AbstractBaseEntity{
        private String item;
        private int condition;
        private String behavior;
    }
}
