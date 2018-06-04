package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import com.beeasy.hzback.modules.system.entity.WorkflowModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UniversalNode extends InputNode {

    private int count = 1;
    private Map<String,State> states = new HashMap<>();
    private String behavior = "";


    public UniversalNode(WorkflowModel workflowModel, Map v) {
        super(workflowModel,v);

        count = (int) v.getOrDefault("count",1);
        if(count < 1){
            count = 1;
        }

        Map<String,Map> states = (Map<String, Map>) v.getOrDefault("states",new HashMap<>());
        states.forEach((kk,vv) -> {
            this.states.put(kk,new State(
                    String.valueOf(vv.get("fieldName")),
                    String.valueOf(vv.get("value")),
                    Integer.valueOf(String.valueOf(vv.get("condition"))),
                    String.valueOf(vv.get("behavior"))
            ));
        });

        behavior = (String) v.getOrDefault("behavior","");

    }




    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class State extends AbstractBaseEntity{
        String key;
        String value;
        int condition;
        String behavior;
    }
}
