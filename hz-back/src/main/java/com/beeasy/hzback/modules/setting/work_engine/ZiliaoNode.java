package com.beeasy.hzback.modules.setting.work_engine;

//import com.beeasy.hzback.modules.setting.entity.WorkNode;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.util.List;
import java.util.Set;

@Data
public class ZiliaoNode extends BaseWorkNode {
    private List<Node> fields;

    @Data
    public static class Node{
        private String name;
        private String type;
        private boolean required;
        private String value;
        private Set<String> items;
        private Integer atLeast;
    }


}
