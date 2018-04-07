package com.beeasy.hzback.modules.system.node;


import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class LogicNode extends BaseNode {

    private String condition;
    private int time;
    private Map<String,String> result = new LinkedHashMap<>();

    public LogicNode(String name, Map map) {
        super(name, "logic", map);
        if (map.containsKey("condition")) {
            setCondition(String.valueOf(map.get("condition")));
        }
        if (map.containsKey("time")) {
            setTime((Integer) map.get("time"));
        }
        if (map.containsKey("result")) {
            Map<String, String> result = (Map<String, String>) map.get("result");
            result.forEach((k, v) -> {
                getResult().put(k, v);
            });
        }
    }

}
