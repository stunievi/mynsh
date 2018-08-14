package com.beeasy.hzback.modules.system.node;


import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class LogicNode extends BaseNode {

    private String condition;
    private int interval;
    private Map<String, String> result = new LinkedHashMap<>();

    public LogicNode(Map map) {
        if (map.containsKey("condition")) {
            setCondition(String.valueOf(map.get("condition")));
        }
        if (map.containsKey("interval")) {
            setInterval((Integer) map.get("interval"));
        }
        if (map.containsKey("result")) {
            Map<String, String> result = (Map<String, String>) map.get("result");
            result.forEach((k, v) -> {
                getResult().put(k, v);
            });
        }
    }

}
