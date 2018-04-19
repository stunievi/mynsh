package com.beeasy.hzback.modules.system.node;

import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeAttribute;
import com.beeasy.hzback.modules.system.entity.WorkflowNodeInstance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
abstract public class BaseNode implements Serializable {
    protected static final long serialVersionUID = 1L;

    public BaseNode(String name, String type, Map map) {
        this.name = name;
        this.type = type;

        if(map.containsKey("next")){
            Object next = map.get("next");
            if(next instanceof Collections){
                setNext(new LinkedHashSet((Collection) next));
            }
            else{
                setNext(new LinkedHashSet(Collections.singleton(next)));
            }
        }

    }

    @ApiModelProperty(required = true)
    protected String name;

    protected String type;

    @ApiModelProperty(hidden = true)
    protected boolean start = false;
    @ApiModelProperty(hidden = true)
    protected boolean end = false;


    //下一个节点, 通畅只有资料节点有, 其他则用behavior执行
    @ApiModelProperty(required = true)
    protected LinkedHashSet<String> next = new LinkedHashSet<>();



    public static BaseNode create(String k, Map v){
        switch (String.valueOf(v.get("type"))){
            case "check":
                return new CheckNode(k,v);

            case "checkprocess":
                return new CheckProcessNode(k,v);

            case "input":
                return new InputNode(k,v);

            case "logic":
                return new LogicNode(k,v);

            case "universal":
                return new UniversalNode(k,v);

            case "end":
                NormalNode node = new NormalNode(k,v);
                node.setEnd(true);
                return node;
        }

        throw new RuntimeException();
    }

    public  void submit(User user, WorkflowNodeInstance wNInstance, Map<String, Object> data){}

    protected void addNode(User user, WorkflowNodeInstance wNInstance, String key, String value){
        WorkflowNodeAttribute attribute = wNInstance.getAttributeList()
                .stream()
                .filter(a -> a.getDealUser().getId().equals(user.getId()) && a.getAttrKey().equals(key))
                .findAny()
                .orElse(new WorkflowNodeAttribute());
        attribute.setDealUser(user);
        attribute.setAttrKey(key);
        attribute.setAttrValue(value);
        attribute.setNodeInstance(wNInstance);
        wNInstance.getAttributeList().add(attribute);
    }
}
